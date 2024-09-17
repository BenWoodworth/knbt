package net.benwoodworth.knbt

public class StringifiedNbtConfiguration internal constructor(
    override val encodeDefaults: Boolean,
    override val ignoreUnknownKeys: Boolean,
    override val lenientNbtNames: Boolean,
    public val prettyPrint: Boolean,
    @ExperimentalNbtApi
    public val prettyPrintIndent: String,
) : NbtFormatConfiguration() {
    @OptIn(ExperimentalNbtApi::class)
    override fun toString(): String =
        "StringifiedNbtConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ", lenientNbtNames=$lenientNbtNames" +
                ", prettyPrint=$prettyPrint" +
                ", prettyPrintIndent='$prettyPrintIndent'" +
                ")"
}

@Suppress("ConstPropertyName")
internal object StringifiedNbtDefaults {
    const val prettyPrint: Boolean = false
    const val prettyPrintIndent: String = "    "
}

/**
 * Builder of the [StringifiedNbt] instance provided by `StringifiedNbt { ... }` factory function.
 */
@NbtDslMarker
public class StringifiedNbtBuilder internal constructor(nbt: StringifiedNbt?) : NbtFormatBuilder(nbt) {
    /**
     * Specifies whether resulting Stringified NBT should be pretty-printed.
     *  `false` by default.
     */
    public var prettyPrint: Boolean =
        nbt?.configuration?.prettyPrint ?: StringifiedNbtDefaults.prettyPrint

    /**
     * Specifies indent string to use with [prettyPrint] mode
     * 4 spaces by default.
     * Experimentality note: this API is experimental because
     * it is not clear whether this option has compelling use-cases.
     */
    @ExperimentalNbtApi
    public var prettyPrintIndent: String =
        nbt?.configuration?.prettyPrintIndent ?: StringifiedNbtDefaults.prettyPrintIndent

    @OptIn(ExperimentalNbtApi::class)
    override fun build(): StringifiedNbt {
        if (!prettyPrint) {
            require(prettyPrintIndent == StringifiedNbtDefaults.prettyPrintIndent) {
                "Indent should not be specified when default printing mode is used"
            }
        } else if (prettyPrintIndent != StringifiedNbtDefaults.prettyPrintIndent) {
            // Values allowed by JSON specification as whitespaces
            val allWhitespaces = prettyPrintIndent.all { it == ' ' || it == '\t' || it == '\r' || it == '\n' }
            require(allWhitespaces) {
                "Only whitespace, tab, newline and carriage return are allowed as pretty print symbols. Had $prettyPrintIndent"
            }
        }

        return StringifiedNbt(
            configuration = StringifiedNbtConfiguration(
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
                lenientNbtNames = lenientNbtNames,
                prettyPrint = prettyPrint,
                prettyPrintIndent = prettyPrintIndent,
            ),
            serializersModule = serializersModule,
        )
    }
}
