package net.benwoodworth.knbt

public class JavaNetworkNbtConfiguration internal constructor(
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
    override val compression: NbtCompression,
    override val compressionLevel: Int?,
    public val protocolVersion: Int,
) : BinaryNbtFormatConfiguration() {
    override fun toString(): String =
        "JavaNetworkNbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", compression=$compression" +
                ", compressionLevel=$compressionLevel" +
                ", protocolVersion=$protocolVersion" +
                ")"
}

/**
 * Builder of the [JavaNetworkNbt] instance provided by `JavaNetworkNbt { ... }` factory function.
 */
@NbtDslMarker
public class JavaNetworkNbtBuilder internal constructor(nbt: JavaNetworkNbt?) : BinaryNbtFormatBuilder(nbt) {
    /**
     * The protocol version of the Minecraft client and server. Required.
     *
     * #### Protocol Version Changelog
     *
     * | Release | Snapshot   | Notes                                  |
     * |---------|------------|----------------------------------------|
     * | 0       | 0x40000001 | Serializes with an empty root tag name |
     * | 764     | 0x40000090 | Serializes without a root tag name     |
     */
    public var protocolVersion: Int? = nbt?.configuration?.protocolVersion
        set(value) {
            if (value != null) {
                require(value >= 0) { "Protocol version must be non-negative, but is $value" }
                require(value != 0x40000000) { "Invalid snapshot protocol version: 0x40000000. Snapshot versions start at 0x40000001" }
            }
            field = value
        }

    private fun getConfiguredProtocolVersion(): Int =
        requireNotNull(protocolVersion) { "Protocol version is required, but has not been configured." }

    override fun build(): JavaNetworkNbt {
        return JavaNetworkNbt(
            configuration = JavaNetworkNbtConfiguration(
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
                compression = getConfiguredCompression(),
                compressionLevel = compressionLevel,
                protocolVersion = getConfiguredProtocolVersion(),
            ),
            serializersModule = serializersModule,
        )
    }
}
