package net.benwoodworth.knbt.tag

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.asNbtDecoder
import net.benwoodworth.knbt.asNbtEncoder
import net.benwoodworth.knbt.internal.NbtTagType
import kotlin.jvm.JvmInline

@JvmInline
@Serializable(NbtDoubleSerializer::class)
public value class NbtDouble internal constructor(internal val value: Double) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Double

    override fun toString(): String = value.toString()
}

public fun Double.toNbtDouble(): NbtDouble = NbtDouble(this)
public fun NbtDouble.toDouble(): Double = value


internal object NbtDoubleSerializer : KSerializer<NbtDouble> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.tag.NbtDouble", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: NbtDouble): Unit =
        encoder.asNbtEncoder().encodeDouble(value.value)

    override fun deserialize(decoder: Decoder): NbtDouble =
        NbtDouble(decoder.asNbtDecoder().decodeDouble())
}
