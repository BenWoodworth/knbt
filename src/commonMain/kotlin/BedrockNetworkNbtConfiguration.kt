package net.benwoodworth.knbt

public class BedrockNetworkNbtConfiguration internal constructor(
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
    override val compression: NbtCompression,
    override val compressionLevel: Int?,
    public val protocolVersion: Int,
) : BinaryNbtFormatConfiguration() {
    override fun toString(): String =
        "BedrockNetworkNbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", compression=$compression" +
                ", compressionLevel=$compressionLevel" +
                ", protocolVersion=$protocolVersion" +
                ")"
}

/**
 * Builder of the [BedrockNetworkNbt] instance provided by `BedrockNetworkNbt { ... }` factory function.
 */
@NbtDslMarker
public class BedrockNetworkNbtBuilder internal constructor(nbt: BedrockNetworkNbt?) : BinaryNbtFormatBuilder(nbt) {
    /**
     * The protocol version of the Minecraft client and server. Required.
     *
     * **Note:** There are currently no NBT differences between Bedrock protocol versions, but there may be changes
     * introduced later similar to [JavaNetworkNbt]'s [protocolVersion][JavaNetworkNbtBuilder.protocolVersion].
     */
    public var protocolVersion: Int? = nbt?.configuration?.protocolVersion
        set(value) {
            if (value != null) {
                require(value >= 0) { "Protocol version must be non-negative, but is $value" }
            }
            field = value
        }

    private fun getConfiguredProtocolVersion(): Int =
        requireNotNull(protocolVersion) { "Protocol version is required, but has not been configured." }

    override fun build(): BedrockNetworkNbt {
        return BedrockNetworkNbt(
            configuration = BedrockNetworkNbtConfiguration(
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
                compression = getConfiguredCompression(),
                compressionLevel = compressionLevel,
                protocolVersion = getConfiguredProtocolVersion(),
            ),
            serializersModule = serializersModule,
        )
    }

    private fun bleh(a: String?) {
        a!!
    }
}
