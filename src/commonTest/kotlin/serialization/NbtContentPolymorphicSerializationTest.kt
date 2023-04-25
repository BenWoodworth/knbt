/*
 * NOTICE: Modified from `JsonContentPolymorphicSerializerTest` in kotlinx.serialization v1.5.0.
 *
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package net.benwoodworth.knbt.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import net.benwoodworth.knbt.NbtContentPolymorphicSerializer
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.StringifiedNbt
import net.benwoodworth.knbt.nbtCompound
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtContentPolymorphicSerializationTest {
    @Serializable
    @SerialName("Choices")
    private sealed class Choices {
        @Serializable
        @SerialName("HasA")
        data class HasA(val a: String) : Choices()

        @Serializable
        @SerialName("HasB")
        data class HasB(val b: Int) : Choices()

        @Serializable
        @SerialName("HasC")
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
    @SerialName("WithChoices")
    private data class WithChoices(
        @Serializable(ChoicesParametricSerializer::class)
        val response: Choices
    )

    private val testDataInput = listOf(
        """{WithChoices:{response:{a:"string"}}}""",
        """{WithChoices:{response:{b:42}}}""",
        """{WithChoices:{response:{c:1b}}}"""
    )

    private val testDataOutput = listOf(
        WithChoices(Choices.HasA("string")),
        WithChoices(Choices.HasB(42)),
        WithChoices(Choices.HasC(true))
    )

    @Test
    fun testParsesParametrically() {
        for (i in testDataInput.indices) {
            assertEquals(
                testDataOutput[i],
                StringifiedNbt.decodeFromString(WithChoices.serializer(), testDataInput[i]),
                "failed test on ${testDataInput[i]}"
            )
        }
    }

    @Test
    fun testSerializesParametrically() {
        for (i in testDataOutput.indices) {
            assertEquals(
                testDataInput[i],
                StringifiedNbt.encodeToString(WithChoices.serializer(), testDataOutput[i]),
                "failed test on ${testDataOutput[i]}"
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
        assertEquals(
            SuccessfulPayment("1.0", "03.02.2020"),
            StringifiedNbt.decodeFromString(PaymentSerializer, """{amount:"1.0",date:"03.02.2020"}""")
        )
        assertEquals(
            RefundedPayment("2.0", "03.02.2020", "complaint"),
            StringifiedNbt.decodeFromString(
                PaymentSerializer,
                """{amount:"2.0",date:"03.02.2020",reason:"complaint"}"""
            )
        )
    }
}
