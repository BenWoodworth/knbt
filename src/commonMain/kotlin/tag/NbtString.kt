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
@Serializable(NbtStringSerializer::class)
public value class NbtString internal constructor(internal val value: String) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_String

    override fun toString(): String = value
}

public fun String.toNbtString(): NbtString = NbtString(this)


internal object NbtStringSerializer : KSerializer<NbtString> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NbtString): Unit =
        encoder.asNbtEncoder().encodeString(value.value)

    override fun deserialize(decoder: Decoder): NbtString =
        NbtString(decoder.asNbtDecoder().decodeString())
}
