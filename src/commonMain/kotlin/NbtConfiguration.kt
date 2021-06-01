package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    public val variant: NbtVariant?,
    public val compression: NbtCompression,
    public val encodeDefaults: Boolean,
    public val ignoreUnknownKeys: Boolean,
) {
    override fun toString(): String =
        "NbtConfiguration(" +
                "variant=$variant" +
                ", compression=$compression" +
                ", encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ")"
}

public enum class NbtCompression {
    None,
    Gzip,
    Zlib,
}
