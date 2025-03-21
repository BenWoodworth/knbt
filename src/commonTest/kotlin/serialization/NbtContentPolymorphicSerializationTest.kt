/*
 * NOTICE: Modified from `JsonContentPolymorphicSerializerTest` in kotlinx.serialization v1.5.0.
 *
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package net.benwoodworth.knbt.serialization

import kotlinx.serialization.*
import net.benwoodworth.knbt.*
import kotlin.test.*

class NbtContentPolymorphicSerializationTest : SerializationTest() {
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

    private val testDataInput = listOf(
        """{"response":{"a":"string"}}""",
        """{"response":{"b":42}}""",
        """{"response":{"c":true}}"""
    )

    private val testDataOutput = listOf(
        WithChoices(Choices.HasA("string")),
        WithChoices(Choices.HasB(42)),
        WithChoices(Choices.HasC(true))
    )

    @Test
    fun testParsesParametrically() {
        for (i in testDataInput.indices) {
            defaultNbt.testDecoding(
                WithChoices.serializer(),
                testDataOutput[i],
                StringifiedNbt.decodeFromString(testDataInput[i])
            )
        }
    }

    @Test
    fun testSerializesParametrically() {
        for (i in testDataOutput.indices) {
            defaultNbt.testEncoding(
                WithChoices.serializer(),
                testDataOutput[i],
                StringifiedNbt.decodeFromString(testDataInput[i])
            )
        }
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
    fun testDocumentationSample() {
        defaultNbt.testDecoding(
            PaymentSerializer,
            SuccessfulPayment("1.0", "03.02.2020"),
            StringifiedNbt.decodeFromString("""{"amount":"1.0","date":"03.02.2020"}"""),
        )
        defaultNbt.testDecoding(
            PaymentSerializer,
            RefundedPayment("2.0", "03.02.2020", "complaint"),
            StringifiedNbt.decodeFromString("""{"amount":"2.0","date":"03.02.2020","reason":"complaint"}"""),
        )
    }
}
