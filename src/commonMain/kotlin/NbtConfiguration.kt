package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    public val variant: NbtVariant,
    public val compression: NbtCompression,
    public val compressionLevel: Int?,
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
    override val classDiscriminator: String,
    override val nameRootClasses: Boolean,
) : NbtFormatConfiguration {
    override fun toString(): String =
        "NbtConfiguration(" +
                "variant=$variant" +
                ", compression=$compression" +
                ", compressionLevel=$compressionLevel" +
                ", encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", classDiscriminator='$classDiscriminator'" +
                ", nameRootClasses=$nameRootClasses" +
                ")"
}
