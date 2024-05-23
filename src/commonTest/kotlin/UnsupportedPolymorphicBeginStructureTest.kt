package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.*
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
class UnsupportedPolymorphicBeginStructureTest {
    private fun expectedInformativeMessage(descriptor: SerialDescriptor): String =
        "Unable to serialize type with serial name '${descriptor.serialName}'. " +
                "beginning structures with polymorphic serial kinds is not supported."

    private object SealedStructureSerializer : KSerializer<Unit> {
        override val descriptor: SerialDescriptor =
            buildSerialDescriptor("SealedStructure", PolymorphicKind.SEALED)

        override fun serialize(encoder: Encoder, value: Unit): Unit =
            encoder.encodeStructure(descriptor) { }

        override fun deserialize(decoder: Decoder): Unit =
            decoder.decodeStructure(descriptor) { }
    }

    private object SealedCollectionSerializer : SerializationStrategy<Unit> {
        override val descriptor: SerialDescriptor =
            buildSerialDescriptor("SealedCollection", PolymorphicKind.SEALED)

        override fun serialize(encoder: Encoder, value: Unit): Unit =
            encoder.encodeCollection(descriptor, 0) { }
    }

    private object OpenStructureSerializer : KSerializer<Unit> {
        override val descriptor: SerialDescriptor =
            buildSerialDescriptor("OpenStructure", PolymorphicKind.OPEN)

        override fun serialize(encoder: Encoder, value: Unit): Unit =
            encoder.encodeStructure(descriptor) { }

        override fun deserialize(decoder: Decoder): Unit =
            decoder.decodeStructure(descriptor) { }
    }

    private object OpenCollectionSerializer : SerializationStrategy<Unit> {
        override val descriptor: SerialDescriptor =
            buildSerialDescriptor("OpenCollection", PolymorphicKind.OPEN)

        override fun serialize(encoder: Encoder, value: Unit): Unit =
            encoder.encodeCollection(descriptor, 0) { }
    }

    @Test
    fun encoding_structure_with_polymorphic_kind_should_throw_UnsupportedOperationException() = parameterizeTest {
        val serializer by parameterOf(
            SealedStructureSerializer,
            SealedCollectionSerializer,
            OpenStructureSerializer,
            OpenCollectionSerializer,
        )

        val failure = assertFailsWith<UnsupportedOperationException> {
            NbtFormat().encodeToNbtTag(serializer, Unit)
        }

        assertEquals(expectedInformativeMessage(serializer.descriptor), failure.message)
    }

    @Test
    fun decoding_structure_with_polymorphic_kind_should_throw_UnsupportedOperationException() = parameterizeTest {
        val serializer by parameterOf(
            SealedStructureSerializer,
            OpenStructureSerializer,
        )

        val failure = assertFailsWith<UnsupportedOperationException> {
            NbtFormat().decodeFromNbtTag(serializer, NbtList(emptyList()))
        }

        assertEquals(expectedInformativeMessage(serializer.descriptor), failure.message)
    }
}
