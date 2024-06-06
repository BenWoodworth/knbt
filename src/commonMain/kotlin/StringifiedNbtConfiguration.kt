package net.benwoodworth.knbt

public class StringifiedNbtConfiguration internal constructor(
    encodeDefaults: Boolean,
    ignoreUnknownKeys: Boolean,
    public val prettyPrint: Boolean,
    @ExperimentalNbtApi
    public val prettyPrintIndent: String,
) : NbtFormatConfiguration(
    encodeDefaults,
    ignoreUnknownKeys,
) {
    @OptIn(ExperimentalNbtApi::class)
    override fun toString(): String =
        "StringifiedNbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", prettyPrint=$prettyPrint" +
                ", prettyPrintIndent='$prettyPrintIndent'" +
                ")"
}
