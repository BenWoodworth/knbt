package net.benwoodworth.knbt.tag

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.InternalNbtApi
import net.benwoodworth.knbt.asNbtDecoder
import net.benwoodworth.knbt.asNbtEncoder
import net.benwoodworth.knbt.internal.NbtTagType

@Serializable(NbtTagSerializer::class)
public sealed interface NbtTag {
    /**
     * For internal use only. Will be marked as internal once Kotlin supports it on sealed interface members.
     */
    @InternalNbtApi
    public val type: NbtTagType // TODO Make internal

    // TODO https://github.com/Kotlin/kotlinx.serialization/issues/1207
    public companion object {
        public fun serializer(): KSerializer<NbtTag> = NbtTagSerializer
    }
}

private object NbtTagSerializer : KSerializer<NbtTag> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("net.benwoodworth.knbt.NbtTag")

    override fun serialize(encoder: Encoder, value: NbtTag): Unit =
        encoder.asNbtEncoder().encodeNbtTag(value)

    override fun deserialize(decoder: Decoder): NbtTag =
        decoder.asNbtDecoder().decodeNbtTag()
}
