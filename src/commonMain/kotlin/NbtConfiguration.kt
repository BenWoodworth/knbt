package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    encodeDefaults: Boolean,
    ignoreUnknownKeys: Boolean,
) : NbtFormatConfiguration(
    encodeDefaults = encodeDefaults,
    ignoreUnknownKeys = ignoreUnknownKeys,
) {
    override fun toString(): String =
        "NbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ")"
}

public class NbtBuilder(nbt: Nbt? = null) : NbtFormatBuilder(nbt) {
    override fun build(): Nbt {
        return Nbt(
            configuration = NbtConfiguration(
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
            ),
            serializersModule = serializersModule,
        )
    }
}
