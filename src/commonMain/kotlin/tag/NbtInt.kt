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
@Serializable(NbtIntSerializer::class)
public value class NbtInt internal constructor(internal val value: Int) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Int

    override fun toString(): String = value.toString()
}

public fun Int.toNbtInt(): NbtInt = NbtInt(this)
public fun NbtInt.toInt(): Int = value


internal object NbtIntSerializer : KSerializer<NbtInt> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.tag.NbtInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: NbtInt): Unit =
        encoder.asNbtEncoder().encodeInt(value.value)

    override fun deserialize(decoder: Decoder): NbtInt =
        NbtInt(decoder.asNbtDecoder().decodeInt())
}
