package net.benwoodworth.knbt

public sealed interface NbtFormatConfiguration {
    public val encodeDefaults: Boolean
    public val ignoreUnknownKeys: Boolean
}
