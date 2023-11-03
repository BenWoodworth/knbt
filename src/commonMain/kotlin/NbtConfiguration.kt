package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    public val variant: NbtVariant,
    public val compression: NbtCompression,
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
) : NbtFormatConfiguration {
    override fun toString(): String =
        "NbtConfiguration(" +
                "variant=$variant" +
                ", compression=$compression" +
                ", encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ")"
}
