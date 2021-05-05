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
@Serializable(with = NbtByteSerializer::class)
public value class NbtByte internal constructor(internal val value: Byte) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Byte

    override fun toString(): String = value.toString()
}

public fun NbtByte.toByte(): Byte = value
public fun Int.toNbtByte(): NbtByte = NbtByte(toByte())
public fun Byte.toNbtByte(): NbtByte = NbtByte(this)

private object NbtByteSerializer : KSerializer<NbtByte> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtByte", PrimitiveKind.BYTE)

    override fun serialize(encoder: Encoder, value: NbtByte): Unit =
        encoder.asNbtEncoder().encodeByte(value.value)

    override fun deserialize(decoder: Decoder): NbtByte =
        NbtByte(decoder.asNbtDecoder().decodeByte())
}
