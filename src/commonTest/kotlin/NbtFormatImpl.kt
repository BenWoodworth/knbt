package net.benwoodworth.knbt

fun NbtFormat(
    encodeDefaults: Boolean = false,
    ignoreUnknownKeys: Boolean = false,
): NbtFormat = Nbt {
    variant = NbtVariant.Java
    compression = NbtCompression.None
    this.ignoreUnknownKeys = ignoreUnknownKeys
    this.encodeDefaults = encodeDefaults
}
