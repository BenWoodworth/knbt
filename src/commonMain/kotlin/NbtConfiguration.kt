package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    public val variant: NbtVariant?,
    public val compression: NbtCompression?,
    public val compressionLevel: Int?,
    public val encodeDefaults: Boolean,
    public val ignoreUnknownKeys: Boolean,
) {
    override fun toString(): String =
        "NbtConfiguration(" +
                "variant=$variant" +
                ", compression=$compression" +
                ", compressionLevel=$compressionLevel" +
                ", encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ")"
}
