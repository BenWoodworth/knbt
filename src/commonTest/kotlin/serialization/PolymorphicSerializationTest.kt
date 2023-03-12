package net.benwoodworth.knbt.serialization

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.NbtEncodingException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PolymorphicSerializationTest : SerializationTest() {
    @Serializable
    @SerialName("Sealed")
    private sealed interface Sealed {
        @Serializable
        data class A(val a: String) : Sealed

        @Serializable
        data class B(val b: Int) : Sealed
    }

    @Test
    fun serializing_polymorphically_should_fail_with_helpful_unsupported_message() {
        val encodingException = assertFailsWith<NbtEncodingException> {
            defaultNbt.encodeToNbtTag<Sealed>(Sealed.A("a"))
        }

        val decodingException = assertFailsWith<NbtDecodingException> {
            defaultNbt.decodeFromNbtTag<Sealed>(NbtByte(4))
        }

        val expectedMessage = "Polymorphic serialization is not yet supported"
        assertEquals(expectedMessage, encodingException.message)
        assertEquals(expectedMessage, decodingException.message)
    }


    private object SealedAsPrimitiveContentSerializer : NbtContentPolymorphicSerializer<Sealed>(Sealed::class) {
        override fun selectDeserializer(tag: NbtTag): DeserializationStrategy<Sealed> = when (tag) {
            is NbtString -> AsStringSerializer
            is NbtInt -> AsIntSerializer
            else -> throw SerializationException("Expected content to be NbtString or NbtInt, but was $tag")
        }

        private object AsStringSerializer : KSerializer<Sealed.A> {
            override val descriptor = PrimitiveSerialDescriptor("SealedAsString", PrimitiveKind.STRING)
            override fun serialize(encoder: Encoder, value: Sealed.A): Unit = encoder.encodeString(value.a)
            override fun deserialize(decoder: Decoder): Sealed.A = Sealed.A(decoder.decodeString())
        }

        private object AsIntSerializer : KSerializer<Sealed.B> {
            override val descriptor = PrimitiveSerialDescriptor("SealedAsInt", PrimitiveKind.INT)
            override fun serialize(encoder: Encoder, value: Sealed.B): Unit = encoder.encodeInt(value.b)
            override fun deserialize(decoder: Decoder): Sealed.B = Sealed.B(decoder.decodeInt())
        }
    }

    @Test
    fun serializing_NbtContentPolymorphicSerializer_at_the_root_should_nest_under_base_class_serial_name() {
        defaultNbt.testSerialization(
            SealedAsPrimitiveContentSerializer,
            Sealed.A("value"),
            buildNbtCompound {
                put("Sealed", "value")
            }
        )
    }

//    // Serializes to an int or a string, with no compound
//    @Serializable(SealedPrimitiveSerializer::class)
//    private sealed interface SealedPrimitive {
//        @Serializable(OfIntSerializer::class)
//        data class OfInt(val int: Int) : SealedPrimitive
//
//        @Serializable(OfStringSerializer::class)
//        data class OfString(val string: String) : SealedPrimitive
//
//        private object OfIntSerializer : KSerializer<OfInt> {
//            override val descriptor: SerialDescriptor =
//                PrimitiveSerialDescriptor("OfInt", PrimitiveKind.INT)
//
//            override fun serialize(encoder: Encoder, value: OfInt): Unit =
//                encoder.encodeInt(value.int)
//
//            override fun deserialize(decoder: Decoder): OfInt =
//                OfInt(decoder.decodeInt())
//        }
//
//        private object OfStringSerializer : KSerializer<OfString> {
//            override val descriptor: SerialDescriptor =
//                PrimitiveSerialDescriptor("OfString", PrimitiveKind.STRING)
//
//            override fun serialize(encoder: Encoder, value: OfString): Unit =
//                encoder.encodeString(value.string)
//
//            override fun deserialize(decoder: Decoder): OfString =
//                OfString(decoder.decodeString())
//        }
//    }
//
//    private object SealedPrimitiveSerializer : NbtContentPolymorphicSerializer<SealedPrimitive>(SealedPrimitive::class) {
//        override fun selectDeserializer(tag: NbtTag): DeserializationStrategy<SealedPrimitive> = when {
//            tag is
//        }
//    }
//
//
//
//    @Test
//    fun serializing_NbtContentPolymorphicSerializer_at_the_root_is_disallowed() {
//        val encodingException = assertFailsWith<NbtEncodingException> {
//            defaultNbt.encodeToNbtTag(SealedContentSerializer, Sealed.A("a"))
//        }
//
//        val decodingException = assertFailsWith<NbtDecodingException> {
//            defaultNbt.decodeFromNbtTag(SealedContentSerializer, NbtByte(4))
//        }
//
//        val expectedMessage = "Serializing with NbtContentPolymorphicSerializer at the root is unsupported"
//        assertEquals(expectedMessage, encodingException.message)
//        assertEquals(expectedMessage, decodingException.message)
//    }

//    @Test
//    fun serializing_with_NbtContentPolymorphicSerializer() {
//
//
//        defaultNbt.testSerialization(
//            SealedSerializer(),
//            Sealed.A(
//                a = "a string"
//            ),
//            buildNbtCompound("A") {
//                put("a", "a string")
//            }
//        )
//
//        defaultNbt.testSerialization(
//            SealedSerializer(),
//            Sealed.B(
//                b = 7
//            ),
//            buildNbtCompound("B") {
//                put("b", 7)
//            }
//        )
//    }
}
