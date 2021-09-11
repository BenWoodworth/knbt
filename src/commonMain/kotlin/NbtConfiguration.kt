package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    public val variant: NbtVariant,
    public val compression: NbtCompression,
    public val compressionLevel: Int?,
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
) : NbtFormatConfiguration {
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use StringifiedNbt instead", level = DeprecationLevel.ERROR)
    public val prettyPrint: Boolean
        get() = error("Use StringifiedNbt instead")

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Use StringifiedNbt instead", level = DeprecationLevel.ERROR)
    @ExperimentalNbtApi
    public val prettyPrintIndent: String
        get() = error("Use StringifiedNbt instead")

    @OptIn(ExperimentalNbtApi::class)
    override fun toString(): String =
        "NbtConfiguration(" +
                "variant=$variant" +
                ", compression=$compression" +
                ", compressionLevel=$compressionLevel" +
                ", encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ")"
}
