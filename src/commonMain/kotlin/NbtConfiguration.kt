package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    public val variant: NbtVariant,
    public val compression: NbtCompression,
    public val encodeDefaults: Boolean,
) {
    override fun toString(): String =
        "NbtConfiguration(" +
                "variant=$variant" +
                ", compression=$compression" +
                ", encodeDefaults=$encodeDefaults" +
                ")"
}

public enum class NbtVariant {
    Java,

    /**
     * Currently only [Java] is supported.
     */
    @ExperimentalNbtApi
    Bedrock,
}

public enum class NbtCompression {
    None,

    /**
     * Currently only supported in Kotlin/JVM
     */
    Gzip,

    /**
     * Currently only supported in Kotlin/JVM
     */
    Zlib
}
