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
@Serializable(NbtLongArraySerializer::class)
public class NbtLongArray @PublishedApi internal constructor(
    internal val value: LongArray,
) : NbtTag, List<Long> by value.asList() {
    override val type: NbtTagType get() = NbtTagType.TAG_Long_Array

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtLongArray && value.contentEquals(other.value))

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = value.contentToString()
}

public inline fun NbtLongArray(size: Int, init: (index: Int) -> Long): NbtLongArray =
    NbtLongArray(LongArray(size) { index -> init(index) })

public fun nbtLongArrayOf(vararg elements: Long): NbtLongArray = NbtLongArray(elements)

public fun LongArray.toNbtLongArray(): NbtLongArray = NbtLongArray(this.copyOf())
public fun Collection<Long>.toNbtLongArray(): NbtLongArray = NbtLongArray(this.toLongArray())


private object NbtLongArraySerializer : KSerializer<NbtLongArray> {
    private object NbtLongArrayDescriptor : SerialDescriptor by serialDescriptor<LongArray>() {
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.NbtLongArray"
    }

    override val descriptor: SerialDescriptor = NbtLongArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtLongArray): Unit =
        encoder.asNbtEncoder().encodeLongArray(value.value)

    override fun deserialize(decoder: Decoder): NbtLongArray =
        NbtLongArray(decoder.asNbtDecoder().decodeLongArray())
}
