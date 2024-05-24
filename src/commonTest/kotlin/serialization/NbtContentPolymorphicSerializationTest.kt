/*
 * NOTICE: Modified from `JsonContentPolymorphicSerializerTest` in kotlinx.serialization v1.5.0.
 *
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package net.benwoodworth.knbt.serialization

import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import net.benwoodworth.knbt.NbtContentPolymorphicSerializer
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.nbtCompound
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import net.benwoodworth.knbt.test.reportedAs
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtContentPolymorphicSerializationTest {
    @Serializable
    private sealed class Choices {
        @Serializable
        data class HasA(val a: String) : Choices()

        @Serializable
        data class HasB(val b: Int) : Choices()

        @Serializable
        data class HasC(val c: Boolean) : Choices()
    }

    private object ChoicesParametricSerializer : NbtContentPolymorphicSerializer<Choices>(Choices::class) {
        override fun selectDeserializer(tag: NbtTag): KSerializer<out Choices> {
            val obj = tag.nbtCompound
            return when {
                "a" in obj -> Choices.HasA.serializer()
                "b" in obj -> Choices.HasB.serializer()
                "c" in obj -> Choices.HasC.serializer()
                else -> throw SerializationException("Unknown choice")
            }
        }
    }

    @Serializable
    private data class WithChoices(@Serializable(ChoicesParametricSerializer::class) val response: Choices)

    private val testData = listOf(
        """{"response":{"a":"string"}}""" to WithChoices(Choices.HasA("string")),
        """{"response":{"b":42}}""" to WithChoices(Choices.HasB(42)),
        """{"response":{"c":true}}""" to WithChoices(Choices.HasC(true))
    )

    @Test
    fun testSerializesParametrically() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        val testCase by parameter(testData)
        val (input, output) = testCase

        nbt.verifyEncoderOrDecoder(
            WithChoices.serializer(),
            output,
            StringifiedNbt.decodeFromString(input),
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    private interface Payment {
        val amount: String
    }

    @Serializable
    private data class SuccessfulPayment(override val amount: String, val date: String) : Payment

    @Serializable
    private data class RefundedPayment(override val amount: String, val date: String, val reason: String) : Payment

    private object PaymentSerializer : NbtContentPolymorphicSerializer<Payment>(Payment::class) {
        override fun selectDeserializer(tag: NbtTag) = when {
            "reason" in tag.nbtCompound -> RefundedPayment.serializer()
            else -> SuccessfulPayment.serializer()
        }
    }

    @Test
    fun testDocumentationSample() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt()

        val testCase by parameterOf(
            """{"amount":"1.0","date":"03.02.2020"}""" to
                    SuccessfulPayment("1.0", "03.02.2020"),

            """{"amount":"2.0","date":"03.02.2020","reason":"complaint"}""" to
                    RefundedPayment("2.0", "03.02.2020", "complaint")

        ).reportedAs(this, "input") { (input, _) -> input }

        val (input, output) = testCase

        nbt.verifyDecoder(
            PaymentSerializer,
            StringifiedNbt.decodeFromString(input),
            testDecodedValue = { decodedValue ->
                assertEquals(output, decodedValue)
            }
        )
    }
}
