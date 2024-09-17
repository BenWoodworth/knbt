package net.benwoodworth.knbt.internal

import kotlinx.serialization.encoding.CompositeDecoder

internal data class NbtCapabilities(
    val namedRoot: Boolean,

    /**
     * Whether the serialized NBT includes lengths with strings/lists/arrays.
     *
     * For deserialization, this means that [CompositeDecoder.decodeSequentially] can be enabled.
     */
    val definiteLengthEncoding: Boolean,

    val rootTagTypes: NbtTagTypeSet,
)
