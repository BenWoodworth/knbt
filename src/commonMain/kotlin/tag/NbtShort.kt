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
@Serializable(NbtShortSerializer::class)
public value class NbtShort internal constructor(internal val value: Short) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Short

    override fun toString(): String = value.toString()
}

public fun Short.toNbtShort(): NbtShort = NbtShort(this)
public fun Int.toNbtShort(): NbtShort = NbtShort(toShort())
public fun NbtShort.toShort(): Short = value


internal object NbtShortSerializer : KSerializer<NbtShort> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.tag.NbtShort", PrimitiveKind.SHORT)

    override fun serialize(encoder: Encoder, value: NbtShort): Unit =
        encoder.asNbtEncoder().encodeShort(value.value)

    override fun deserialize(decoder: Decoder): NbtShort =
        NbtShort(decoder.asNbtDecoder().decodeShort())
}
