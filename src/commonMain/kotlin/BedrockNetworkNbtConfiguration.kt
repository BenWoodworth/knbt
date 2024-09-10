package net.benwoodworth.knbt

public class BedrockNetworkNbtConfiguration internal constructor(
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
    override val compression: NbtCompression,
    override val compressionLevel: Int?,
) : BinaryNbtFormatConfiguration() {
    override fun toString(): String =
        "BedrockNetworkNbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", compression=$compression" +
                ", compressionLevel=$compressionLevel" +
                ")"
}

/**
 * Builder of the [BedrockNetworkNbt] instance provided by `BedrockNetworkNbt { ... }` factory function.
 */
@NbtDslMarker
public class BedrockNetworkNbtBuilder internal constructor(nbt: BedrockNetworkNbt?) : BinaryNbtFormatBuilder(nbt) {
    override fun build(): BedrockNetworkNbt {
        return BedrockNetworkNbt(
            configuration = BedrockNetworkNbtConfiguration(
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
                compression = getConfiguredCompression(),
                compressionLevel = compressionLevel,
            ),
            serializersModule = serializersModule,
        )
    }
}
