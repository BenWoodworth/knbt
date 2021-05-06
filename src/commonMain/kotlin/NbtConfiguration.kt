package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    public val encodeDefaults: Boolean = false,
    public val variant: NbtVariant = NbtVariant.Java,
) {
    override fun toString(): String = "NbtConfiguration(encodeDefaults=$encodeDefaults, variant=$variant)"
}

public enum class NbtVariant {
    Java,

    /**
     * Currently only [Java] is supported.
     */
    @ExperimentalNbtApi
    Bedrock,
}
