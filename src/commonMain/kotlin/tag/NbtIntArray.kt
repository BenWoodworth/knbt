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
@Serializable(NbtIntArraySerializer::class)
public class NbtIntArray @PublishedApi internal constructor(
    internal val value: IntArray,
) : NbtTag, List<Int> by value.asList() {
    override val type: NbtTagType get() = NbtTagType.TAG_Int_Array

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtIntArray && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = value.contentToString()
}

public inline fun NbtIntArray(size: Int, init: (index: Int) -> Int): NbtIntArray =
    NbtIntArray(IntArray(size) { index -> init(index) })

public fun nbtIntArrayOf(vararg elements: Int): NbtIntArray = NbtIntArray(elements)

public fun IntArray.toNbtIntArray(): NbtIntArray = NbtIntArray(this.copyOf())
public fun Collection<Int>.toNbtIntArray(): NbtIntArray = NbtIntArray(this.toIntArray())


internal object NbtIntArraySerializer : KSerializer<NbtIntArray> {
    private object NbtIntArrayDescriptor : SerialDescriptor by serialDescriptor<IntArray>() {
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.NbtIntArray"
    }

    override val descriptor: SerialDescriptor = NbtIntArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtIntArray): Unit =
        encoder.asNbtEncoder().encodeIntArray(value.value)

    override fun deserialize(decoder: Decoder): NbtIntArray =
        NbtIntArray(decoder.asNbtDecoder().decodeIntArray())
}
