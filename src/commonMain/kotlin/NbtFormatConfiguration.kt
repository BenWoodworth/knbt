package net.benwoodworth.knbt

public open class NbtFormatConfiguration internal constructor(
    public val encodeDefaults: Boolean,
    public val ignoreUnknownKeys: Boolean
) {
    override fun toString(): String =
        "NbtFormatConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ")"
}
