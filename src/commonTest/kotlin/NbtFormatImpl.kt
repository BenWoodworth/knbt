package net.benwoodworth.knbt

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

fun NbtFormat(
    serializersModule: SerializersModule = EmptySerializersModule(),
    encodeDefaults: Boolean = false,
    ignoreUnknownKeys: Boolean = false,
): NbtFormat = Nbt {
    variant = NbtVariant.Java
    compression = NbtCompression.None

    this.serializersModule = serializersModule
    this.ignoreUnknownKeys = ignoreUnknownKeys
    this.encodeDefaults = encodeDefaults
}
