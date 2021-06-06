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
@Serializable(NbtLongSerializer::class)
public value class NbtLong internal constructor(internal val value: Long) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Long

    override fun toString(): String = value.toString()
}

public fun Long.toNbtLong(): NbtLong = NbtLong(this)
public fun Int.toNbtLong(): NbtLong = NbtLong(toLong())
public fun NbtLong.toLong(): Long = value


internal object NbtLongSerializer : KSerializer<NbtLong> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.tag.NbtLong", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: NbtLong): Unit =
        encoder.asNbtEncoder().encodeLong(value.value)

    override fun deserialize(decoder: Decoder): NbtLong =
        NbtLong(decoder.asNbtDecoder().decodeLong())
}
