package net.benwoodworth.knbt

public class NbtConfiguration internal constructor(
    public val encodeDefaults: Boolean = false,
) {
    override fun toString(): String =
        "NbtConfiguration(encodeDefaults=$encodeDefaults)"
}
