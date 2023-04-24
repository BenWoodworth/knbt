package net.benwoodworth.knbt.test

import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtVariant

fun NbtFormat(
    encodeDefaults: Boolean = false,
    ignoreUnknownKeys: Boolean = false,
    serializersModule: SerializersModule = EmptySerializersModule()
): NbtFormat = Nbt {
    variant = NbtVariant.Java
    compression = NbtCompression.None
    this.ignoreUnknownKeys = ignoreUnknownKeys
    this.encodeDefaults = encodeDefaults
    this.serializersModule = serializersModule
}
