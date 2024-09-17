package net.benwoodworth.knbt

public class BedrockNbtConfiguration internal constructor(
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
    override val lenientNbtNames: Boolean,
    override val compression: NbtCompression,
    override val compressionLevel: Int?,
) : BinaryNbtFormatConfiguration() {
    override fun toString(): String =
        "BedrockNbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", lenientNbtNames=$lenientNbtNames" +
                ", compression=$compression" +
                ", compressionLevel=$compressionLevel" +
                ")"
}

/**
 * Builder of the [BedrockNbt] instance provided by `BedrockNbt { ... }` factory function.
 */
@NbtDslMarker
public class BedrockNbtBuilder internal constructor(nbt: BedrockNbt?) : BinaryNbtFormatBuilder(nbt) {
    override fun build(): BedrockNbt {
        return BedrockNbt(
            configuration = BedrockNbtConfiguration(
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
                lenientNbtNames = lenientNbtNames,
                compression = getConfiguredCompression(),
                compressionLevel = compressionLevel,
            ),
            serializersModule = serializersModule,
        )
    }
}
