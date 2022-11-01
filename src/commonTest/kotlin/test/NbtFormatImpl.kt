package net.benwoodworth.knbt.test

import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtVariant

fun NbtFormat(
    encodeDefaults: Boolean = false,
    ignoreUnknownKeys: Boolean = false,
): NbtFormat = Nbt {
    variant = NbtVariant.Java
    compression = NbtCompression.None
    this.ignoreUnknownKeys = ignoreUnknownKeys
    this.encodeDefaults = encodeDefaults
}
