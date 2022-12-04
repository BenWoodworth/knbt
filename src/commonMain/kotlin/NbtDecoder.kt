package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.NbtEncodingException

public sealed interface NbtDecoder : Decoder {
    public val nbt: NbtFormat

    public fun decodeByteArray(): ByteArray
    public fun decodeIntArray(): IntArray
    public fun decodeLongArray(): LongArray

    public fun decodeNbtTag(): NbtTag

    public fun beginCompound(descriptor: SerialDescriptor): CompositeNbtDecoder
    public fun beginList(descriptor: SerialDescriptor): CompositeNbtDecoder
    public fun beginByteArray(descriptor: SerialDescriptor): CompositeNbtDecoder
    public fun beginIntArray(descriptor: SerialDescriptor): CompositeNbtDecoder
    public fun beginLongArray(descriptor: SerialDescriptor): CompositeNbtDecoder
}

public fun Decoder.asNbtDecoder(): NbtDecoder =
    this as? NbtDecoder ?: throw NbtDecodingException(
        "This serializer can be used only with NBT format. Expected Decoder to be NbtDecoder, got ${this::class}"
    )

public sealed interface CompositeNbtDecoder : CompositeDecoder {
    public val nbt: NbtFormat

    public fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int): ByteArray
    public fun decodeIntArrayElement(descriptor: SerialDescriptor, index: Int): IntArray
    public fun decodeLongArrayElement(descriptor: SerialDescriptor, index: Int): LongArray

    public fun decodeNbtTagElement(descriptor: SerialDescriptor, index: Int): NbtTag
}

@ExperimentalSerializationApi
internal abstract class AbstractNbtDecoder : AbstractDecoder(), NbtDecoder, CompositeNbtDecoder {
    override fun decodeByteArray(): ByteArray = decodeValue() as ByteArray
    override fun decodeIntArray(): IntArray = decodeValue() as IntArray
    override fun decodeLongArray(): LongArray = decodeValue() as LongArray

    override fun decodeNbtTag(): NbtTag = decodeValue() as NbtTag

    final override fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int): ByteArray =
        decodeByteArray()

    final override fun decodeIntArrayElement(descriptor: SerialDescriptor, index: Int): IntArray = decodeIntArray()
    final override fun decodeLongArrayElement(descriptor: SerialDescriptor, index: Int): LongArray =
        decodeLongArray()

    final override fun decodeNbtTagElement(descriptor: SerialDescriptor, index: Int): NbtTag =
        decodeNbtTag()

    abstract override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder

    override fun beginCompound(descriptor: SerialDescriptor): CompositeNbtDecoder = this
    override fun beginList(descriptor: SerialDescriptor): CompositeNbtDecoder = this
    override fun beginByteArray(descriptor: SerialDescriptor): CompositeNbtDecoder = this
    override fun beginIntArray(descriptor: SerialDescriptor): CompositeNbtDecoder = this
    override fun beginLongArray(descriptor: SerialDescriptor): CompositeNbtDecoder = this

    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        @Suppress("UNCHECKED_CAST")
        when (deserializer.descriptor.kind) {
            is PolymorphicKind -> throw NbtEncodingException("Polymorphic serialization is not yet supported")
            else -> super<AbstractDecoder>.decodeSerializableValue(deserializer)
        }
}
