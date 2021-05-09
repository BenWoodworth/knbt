package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    public val encodeDefaults: Boolean = false,
    public val variant: NbtVariant = NbtVariant.Java,
    public val compression: NbtCompression = NbtCompression.None,
) {
    override fun toString(): String =
        "NbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", variant=$variant" +
                ", compression=$compression" +
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
