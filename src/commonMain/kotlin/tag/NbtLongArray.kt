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
public class NbtLongArray private constructor(
    internal val value: LongArray,
    private val list: List<Long>,
) : NbtTag, List<Long> by list {
    override val type: NbtTagType get() = NbtTagType.TAG_Long_Array

    @PublishedApi
    internal constructor(value: LongArray) : this(value, value.asList())

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtLongArray && value.contentEquals(other.value)
        else -> list == other
    }

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = value.contentToString()
}

public inline fun NbtLongArray(size: Int, init: (index: Int) -> Long): NbtLongArray =
    NbtLongArray(LongArray(size) { index -> init(index) })

public fun nbtLongArrayOf(vararg elements: Long): NbtLongArray = NbtLongArray(elements)

public fun LongArray.toNbtLongArray(): NbtLongArray = NbtLongArray(this.copyOf())
public fun Collection<Long>.toNbtLongArray(): NbtLongArray = NbtLongArray(this.toLongArray())


internal object NbtLongArraySerializer : KSerializer<NbtLongArray> {
    private object NbtLongArrayDescriptor : SerialDescriptor by serialDescriptor<LongArray>() {
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.tag.NbtLongArray"
    }

    override val descriptor: SerialDescriptor = NbtLongArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtLongArray): Unit =
        encoder.asNbtEncoder().encodeLongArray(value.value)

    override fun deserialize(decoder: Decoder): NbtLongArray =
        NbtLongArray(decoder.asNbtDecoder().decodeLongArray())
}
