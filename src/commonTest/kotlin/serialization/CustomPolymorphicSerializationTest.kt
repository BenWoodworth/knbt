package net.benwoodworth.knbt.serialization

import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test

@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
class CustomPolymorphicSerializationTest : SerializationTest() {
    @Serializable(OpenSerializer::class)
    private abstract class Open {
        abstract val a: String
    }

    private data class OpenImplementation(override val a: String) : Open()

    private object OpenSerializer : KSerializer<Open> {
        override val descriptor: SerialDescriptor =
            buildSerialDescriptor("Base", PolymorphicKind.OPEN)

        override fun serialize(encoder: Encoder, value: Open): Unit =
            encoder.encodeString(value.a)

        override fun deserialize(decoder: Decoder): Open =
            OpenImplementation(decoder.decodeString())
    }

    @Test
    fun custom_open_polymorphic_serializer() {
        defaultNbt.testSerialization(
            Open.serializer(),
            value = OpenImplementation("value") as Open,
            nbtTag = NbtString("value"),
        )
    }


    @Serializable(EvenOrOddSerializer::class)
    private sealed interface IntOrString {
        val value: Any

        data class OfInt(override val value: Int) : IntOrString
        data class OfString(override val value: String) : IntOrString
    }

    private object EvenOrOddSerializer : KSerializer<IntOrString> {
        override val descriptor: SerialDescriptor =
            buildSerialDescriptor("IntOrString", PolymorphicKind.SEALED)

        override fun serialize(encoder: Encoder, value: IntOrString): Unit =
            when (value) {
                is IntOrString.OfInt -> encoder.encodeInt(value.value)
                is IntOrString.OfString -> encoder.encodeString(value.value)
            }

        override fun deserialize(decoder: Decoder): IntOrString =
            when (val tag = (decoder as NbtDecoder).decodeNbtTag()) {
                is NbtInt -> IntOrString.OfInt(tag.value)
                is NbtString -> IntOrString.OfString(tag.value)
                else -> throw SerializationException("Expected NbtInt or NbtString, but got ${tag::class.simpleName}")
            }
    }

    @Test
    fun custom_sealed_polymorphic_serializer() = parameterizeTest {
        val testCase by parameterOf(
            IntOrString.OfInt(0) to NbtInt(0),
            IntOrString.OfString("") to NbtString(""),
        )

        val (intOrString, nbtTag) = testCase

        defaultNbt.testSerialization(IntOrString.serializer(), intOrString, nbtTag)
    }
}
