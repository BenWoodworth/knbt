package net.benwoodworth.knbt.serialization

import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.map
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.test.generators.nbtInt
import net.benwoodworth.knbt.test.generators.nbtString
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
    fun custom_open_polymorphic_serializer() = runTest {
        checkAll(Arb.nbtString()) { value ->
            defaultNbt.testSerialization(
                value = OpenImplementation(value.value) as Open,
                nbtTag = value,
            )
        }
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
    fun custom_sealed_polymorphic_serializer() = runTest {
        val testCaseArb = Arb.choice(
            Arb.nbtInt().map { IntOrString.OfInt(it.value) to it },
            Arb.nbtString().map { IntOrString.OfString(it.value) to it },
        )

        checkAll(testCaseArb) { (intOrString, nbtTag) ->
            defaultNbt.testSerialization(intOrString, nbtTag)
        }
    }
}
