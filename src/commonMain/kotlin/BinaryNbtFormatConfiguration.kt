package net.benwoodworth.knbt

public class BinaryNbtFormatConfiguration internal constructor(
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
    public val variant: NbtVariant,
    public val compression: NbtCompression,
    public val compressionLevel: Int?,
) : NbtFormatConfiguration() {
    override fun toString(): String =
        "BinaryNbtFormatConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", variant=$variant" +
                ", compression=$compression" +
                ", compressionLevel=$compressionLevel" +
                ")"
}

internal object BinaryNbtFormatDefaults {
    val compressionLevel: Int? = null
}

/**
 * Builder of the [BinaryNbtFormat] instance provided by `BinaryNbtFormat { ... }` factory function.
 */
@NbtDslMarker
public class BinaryNbtFormatBuilder internal constructor(nbt: BinaryNbtFormat?) : NbtFormatBuilder(nbt) {
    /**
     * The variant of NBT binary format to use. Required.
     */
    public var variant: NbtVariant? = nbt?.configuration?.variant

    /**
     * The compression method to use when writing NBT binary. Required.
     */
    public var compression: NbtCompression? = nbt?.configuration?.compression

    /**
     * The compression level, in `0..9` or `null`.
     * `null` by default.
     *
     * - `0` gives no compression at all
     * - `1` gives the best speed
     * - `9` gives the best compression.
     * - `null` requests a compromise between speed and compression.
     */
    public var compressionLevel: Int? = nbt?.configuration?.compressionLevel ?: BinaryNbtFormatDefaults.compressionLevel
        set(value) {
            require(value == null || value in 0..9) { "Compression level must be in 0..9 or null." }
            field = value
        }

    override fun build(): BinaryNbtFormat {
        val variant = variant
        val compression = compression

        require(variant != null && compression != null) {
            when {
                variant == null && compression == null -> "Variant and compression are required but are null"
                variant == null -> "Variant is required but is null"
                else -> "Compression is required but is null"
            }
        }

        return BinaryNbtFormat(
            configuration = BinaryNbtFormatConfiguration(
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
                variant = variant,
                compression = compression,
                compressionLevel = compressionLevel,
            ),
            serializersModule = serializersModule,
        )
    }
}
