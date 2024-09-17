package net.benwoodworth.knbt

public abstract class BinaryNbtFormatConfiguration internal constructor() : NbtFormatConfiguration() {
    public abstract val compression: NbtCompression
    public abstract val compressionLevel: Int?
}

internal object BinaryNbtFormatDefaults {
    val compressionLevel: Int? = null
}

@NbtDslMarker
public abstract class BinaryNbtFormatBuilder internal constructor(nbt: BinaryNbtFormat?) : NbtFormatBuilder(nbt) {
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

    protected fun getConfiguredCompression(): NbtCompression =
        requireNotNull(compression) { "Compression is required but has not been configured" }
}
