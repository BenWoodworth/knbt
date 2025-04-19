package net.benwoodworth.knbt

public class JavaNbtConfiguration internal constructor(
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
    override val lenientNbtNames: Boolean,
    override val compression: NbtCompression,
    override val compressionLevel: Int?,
) : BinaryNbtFormatConfiguration() {
    override fun toString(): String =
        "JavaNbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", lenientNbtNames=$lenientNbtNames" +
                ", compression=$compression" +
                ", compressionLevel=$compressionLevel" +
                ")"
}

/**
 * Builder of the [JavaNbt] instance provided by `JavaNbt { ... }` factory function.
 */
@NbtDslMarker
public class JavaNbtBuilder internal constructor(nbt: JavaNbt?) : BinaryNbtFormatBuilder(nbt) {
    override fun build(): JavaNbt {
        return JavaNbt(
            configuration = JavaNbtConfiguration(
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
