package net.benwoodworth.knbt

public class StringifiedNbtConfiguration internal constructor(
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
    public val prettyPrint: Boolean,
    @ExperimentalNbtApi
    public val prettyPrintIndent: String,
    override val classDiscriminator: String,
) : NbtFormatConfiguration {
    @OptIn(ExperimentalNbtApi::class)
    override fun toString(): String =
        "NbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", prettyPrint=$prettyPrint" +
                ", prettyPrintIndent='$prettyPrintIndent'" +
                ", classDiscriminator='$classDiscriminator'" +
                ")"
}
