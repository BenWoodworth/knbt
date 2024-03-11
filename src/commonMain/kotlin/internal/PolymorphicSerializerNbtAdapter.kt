package net.benwoodworth.knbt.internal

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*

@OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
internal class PolymorphicSerializerNbtAdapter<T : Any>(
    private val polymorphicSerializer: AbstractPolymorphicSerializer<T>
) : KSerializer<T> {
    override val descriptor: SerialDescriptor
        get() = polymorphicSerializer.descriptor

    override fun serialize(encoder: Encoder, value: T) {
        val nbtEncoder = encoder.asNbtEncoder()

        val polymorphicEncoder = PolymorphicEncoder(nbtEncoder)
        polymorphicSerializer.serialize(polymorphicEncoder, value)

        val encodedType = checkNotNull(polymorphicEncoder.encodedType) {
            "Expected polymorphic 'type' element to be encoded by ${polymorphicSerializer::class.simpleName}"
        }
        val encodedValue = checkNotNull(polymorphicEncoder.encodedValue) {
            "Expected polymorphic 'value' element to be encoded by ${polymorphicSerializer::class.simpleName}"
        }
        val encodedValueDescriptor = checkNotNull(polymorphicEncoder.encodedValueDescriptor) {
            "Encoded value descriptor should have been recorded alongside the encoded value"
        }

        if (encodedValue !is NbtCompound) {
            throw NbtDecodingException("Expected polymorphic data to be a ${NbtTagType.TAG_Compound}, but was ${encodedValue.type}")
        }

        if (CLASS_DISCRIMINATOR in encodedValue) {
            val baseName = polymorphicSerializer.descriptor.serialName
            val actualName = encodedValueDescriptor.serialName

            val isSealed = polymorphicSerializer.descriptor.kind == PolymorphicKind.SEALED
            val classType = if (isSealed) "Sealed" else "Polymorphic"

            error(
                "$classType class '$actualName' cannot be serialized as base class '$baseName' because it " +
                        "has property name that conflicts with NBT class discriminator '$CLASS_DISCRIMINATOR'. " +
                        "Consider renaming property with @SerialName annotation"
            )
        }

        val encodedValueWithDiscriminator = buildNbtCompound {
            put(CLASS_DISCRIMINATOR, encodedType)
            encodedValue.forEach { (key, value) ->
                put(key, value)
            }
        }

        nbtEncoder.encodeSerializableValue(NbtCompoundSerializer, encodedValueWithDiscriminator)
    }

    override fun deserialize(decoder: Decoder): T {
        val nbtDecoder = decoder.asNbtDecoder()
        val decodedValueWithDiscriminator = nbtDecoder.decodeSerializableValue(NbtTag.serializer())

        if (decodedValueWithDiscriminator !is NbtCompound) {
            throw NbtDecodingException("Expected polymorphic data to be a ${NbtTagType.TAG_Compound} with a '$CLASS_DISCRIMINATOR' class discriminator, but was ${decodedValueWithDiscriminator.type}")
        }

        val decodedType = decodedValueWithDiscriminator[CLASS_DISCRIMINATOR]
            ?: throw NbtDecodingException("Expected polymorphic data to be a ${NbtTagType.TAG_Compound} with a '$CLASS_DISCRIMINATOR' class discriminator, but was ${decodedValueWithDiscriminator.type}")

        if (decodedType !is NbtString) {
            throw NbtDecodingException("Expected polymorphic 'type' class discriminator to be a ${NbtTagType.TAG_String}, but was ${decodedType.type}")
        }

        val decodedValue = buildNbtCompound {
            decodedValueWithDiscriminator.forEach { (key, value) ->
                if (key != CLASS_DISCRIMINATOR) put(key, value)
            }
        }

        return polymorphicSerializer.deserialize(PolymorphicDecoder(nbtDecoder, decodedType.value, decodedValue))
    }

    private companion object {
        const val CLASS_DISCRIMINATOR = "type"
    }

    private class PolymorphicEncoder(
        private val nbtEncoder: NbtEncoder
    ) : AbstractEncoder() {
        override val serializersModule: SerializersModule
            get() = nbtEncoder.serializersModule

        var encodedType: String? = null

        var encodedValue: NbtTag? = null
        var encodedValueDescriptor: SerialDescriptor? = null

        override fun encodeString(value: String) {
            encodedType = value
        }

        override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
            val writer = TreeNbtWriter {
                encodedValue = it
                encodedValueDescriptor = serializer.descriptor
            }

            DefaultNbtEncoder(nbtEncoder.nbt, writer)
                .encodeSerializableValue(serializer, value)
        }
    }

    private class PolymorphicDecoder(
        private val nbtDecoder: NbtDecoder,
        private val decodedType: String,
        private val decodedValue: NbtCompound
    ) : AbstractDecoder() {
        override val serializersModule: SerializersModule
            get() = nbtDecoder.serializersModule

        private var nextElementIndex = 0

        override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
            if (nextElementIndex <= 1) {
                nextElementIndex++
            } else {
                CompositeDecoder.DECODE_DONE
            }

        override fun decodeString(): String = decodedType

        override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
            NbtDecoder(nbtDecoder.nbt, TreeNbtReader(decodedValue))
                .decodeSerializableValue(deserializer)
    }
}
