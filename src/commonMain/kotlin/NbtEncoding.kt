package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder

public sealed interface NbtEncoder : Encoder {
    public fun encodeByteArray(value: ByteArray)
    public fun encodeIntArray(value: IntArray)
    public fun encodeLongArray(value: LongArray)

    public fun encodeNbtTag(value: NbtTag)

    public fun beginCompound(descriptor: SerialDescriptor): CompositeNbtEncoder
    public fun beginList(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder
    public fun beginByteArray(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder
    public fun beginIntArray(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder
    public fun beginLongArray(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder
}

public fun Encoder.asNbtEncoder(): NbtEncoder =
    this as? NbtEncoder ?: throw NbtEncodingException(
        "This serializer can be used only with NBT format. Expected Encoder to be NbtEncoder, got ${this::class}"
    )

public sealed interface CompositeNbtEncoder : CompositeEncoder {
    public fun encodeByteArrayElement(descriptor: SerialDescriptor, index: Int, value: ByteArray)
    public fun encodeIntArrayElement(descriptor: SerialDescriptor, index: Int, value: IntArray)
    public fun encodeLongArrayElement(descriptor: SerialDescriptor, index: Int, value: LongArray)

    public fun encodeNbtTagElement(descriptor: SerialDescriptor, index: Int, value: NbtTag)
}

@ExperimentalSerializationApi
internal abstract class AbstractNbtEncoder : AbstractEncoder(), NbtEncoder, CompositeNbtEncoder {
    override fun encodeByteArray(value: ByteArray): Unit = encodeValue(value)
    override fun encodeIntArray(value: IntArray): Unit = encodeValue(value)
    override fun encodeLongArray(value: LongArray): Unit = encodeValue(value)

    override fun encodeNbtTag(value: NbtTag): Unit = encodeNbtTag(value)

    final override fun encodeByteArrayElement(descriptor: SerialDescriptor, index: Int, value: ByteArray) {
        if (encodeElement(descriptor, index)) encodeByteArray(value)
    }

    final override fun encodeIntArrayElement(descriptor: SerialDescriptor, index: Int, value: IntArray) {
        if (encodeElement(descriptor, index)) encodeIntArray(value)
    }

    final override fun encodeLongArrayElement(descriptor: SerialDescriptor, index: Int, value: LongArray) {
        if (encodeElement(descriptor, index)) encodeLongArray(value)
    }

    final override fun encodeNbtTagElement(
        descriptor: SerialDescriptor,
        index: Int,
        value: NbtTag
    ) {
        if (encodeElement(descriptor, index)) encodeNbtTag(value)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        beginCompound(descriptor)

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
        if (descriptor.kind == StructureKind.LIST) {
            beginList(descriptor, collectionSize)
        } else {
            beginCompound(descriptor)
        }

    override fun beginCompound(descriptor: SerialDescriptor): CompositeNbtEncoder = this
    override fun beginList(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder = this
    override fun beginByteArray(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder = this
    override fun beginIntArray(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder = this
    override fun beginLongArray(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder = this

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T): Unit =
        when (serializer) {
            ByteArraySerializer() -> encodeByteArray(value as ByteArray)
            IntArraySerializer() -> encodeIntArray(value as IntArray)
            LongArraySerializer() -> encodeLongArray(value as LongArray)
            is PolymorphicSerializer<*> -> when (serializer.baseClass) {
                NbtTag::class -> encodeNbtTag(value as NbtTag)
                else -> throw NbtEncodingException("Polymorphic serialization is not yet supported")
            }
            else -> super<AbstractEncoder>.encodeSerializableValue(serializer, value)
        }
}
