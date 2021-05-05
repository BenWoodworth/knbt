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
@Serializable(NbtFloatSerializer::class)
public value class NbtFloat internal constructor(internal val value: Float) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Float

    override fun toString(): String = value.toString()
}

public fun Float.toNbtFloat(): NbtFloat = NbtFloat(this)
public fun NbtFloat.toFloat(): Float = value


private object NbtFloatSerializer : KSerializer<NbtFloat> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtFloat", PrimitiveKind.FLOAT)

    override fun serialize(encoder: Encoder, value: NbtFloat): Unit =
        encoder.asNbtEncoder().encodeFloat(value.value)

    override fun deserialize(decoder: Decoder): NbtFloat =
        NbtFloat(decoder.asNbtDecoder().decodeFloat())
}
