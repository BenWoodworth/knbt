package net.benwoodworth.knbt.tag

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.asNbtDecoder
import net.benwoodworth.knbt.asNbtEncoder
import net.benwoodworth.knbt.internal.NbtTagType

@Suppress("OVERRIDE_BY_INLINE")
@Serializable(NbtByteArraySerializer::class)
public class NbtByteArray @PublishedApi internal constructor(
    internal val value: ByteArray,
) : NbtTag, List<Byte> by value.asList() {
    override val type: NbtTagType get() = NbtTagType.TAG_Byte_Array

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtByteArray && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = value.contentToString()
}

public inline fun NbtByteArray(size: Int, init: (index: Int) -> Byte): NbtByteArray =
    NbtByteArray(ByteArray(size) { index -> init(index) })

public fun nbtByteArrayOf(vararg elements: Byte): NbtByteArray = NbtByteArray(elements)

public fun ByteArray.toNbtByteArray(): NbtByteArray = NbtByteArray(this.copyOf())
public fun Collection<Byte>.toNbtByteArray(): NbtByteArray = NbtByteArray(this.toByteArray())


internal object NbtByteArraySerializer : KSerializer<NbtByteArray> {
    private object NbtByteArrayDescriptor : SerialDescriptor by serialDescriptor<ByteArray>() {
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.tag.NbtByteArray"
    }

    override val descriptor: SerialDescriptor = NbtByteArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtByteArray): Unit =
        encoder.asNbtEncoder().encodeByteArray(value.value)

    override fun deserialize(decoder: Decoder): NbtByteArray =
        NbtByteArray(decoder.asNbtDecoder().decodeByteArray())
}
