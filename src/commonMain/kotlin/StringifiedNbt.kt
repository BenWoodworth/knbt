package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.StringFormat
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.*
import kotlin.native.concurrent.ThreadLocal

private val stringifiedNbtCapabilities = NbtCapabilities(
    namedRoot = false,
)

public open class StringifiedNbt internal constructor(
    override val configuration: StringifiedNbtConfiguration,
    serializersModule: SerializersModule,
) : NbtFormat(
    "SNBT",
    configuration,
    serializersModule,
    stringifiedNbtCapabilities
), StringFormat {
    /**
     * The default instance of [StringifiedNbt] with default configuration.
     */
    @ThreadLocal
    public companion object Default : StringifiedNbt(
        configuration = StringifiedNbtConfiguration(
            encodeDefaults = NbtFormat.configuration.encodeDefaults,
            ignoreUnknownKeys = NbtFormat.configuration.ignoreUnknownKeys,
            prettyPrint = false,
            prettyPrintIndent = "    ",
        ),
        serializersModule = EmptySerializersModule(),
    )

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String =
        buildString {
            val context = SerializationNbtContext()
            val writer = StringifiedNbtWriter(this@StringifiedNbt, this)
            val encoder = NbtWriterEncoder(this@StringifiedNbt, context, writer)

            encoder.encodeSerializableValue(serializer, value)
        }

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        val context = SerializationNbtContext()
        val source = CharSource(string)
        val reader = StringifiedNbtReader(context, source)
        val decoder = NbtReaderDecoder(this, context, reader)
        val decoded = decoder.decodeSerializableValue(deserializer)

        var char = source.read()
        while (char != CharSource.ReadResult.EOF) {
            if (!char.toChar().isWhitespace()) {
                throw NbtDecodingException(context, "Expected only whitespace after value, but got '$char'")
            }
            char = source.read()
        }

        return decoded
    }
}

/**
 * Creates an instance of [StringifiedNbt] configured from the optionally given [StringifiedNbt instance][from]
 * and adjusted with [builderAction].
 */
public fun StringifiedNbt(
    from: StringifiedNbt = StringifiedNbt.Default,
    builderAction: StringifiedNbtBuilder.() -> Unit,
): StringifiedNbt {
    val builder = StringifiedNbtBuilder(from)
    builder.builderAction()
    return builder.build()
}

/**
 * Builder of the [StringifiedNbt] instance provided by `StringifiedNbt { ... }` factory function.
 */
@NbtDslMarker
public class StringifiedNbtBuilder internal constructor(nbt: StringifiedNbt) : NbtFormatBuilder(nbt) {
    /**
     * Specifies whether resulting Stringified NBT should be pretty-printed.
     *  `false` by default.
     */
    public var prettyPrint: Boolean = nbt.configuration.prettyPrint

    /**
     * Specifies indent string to use with [prettyPrint] mode
     * 4 spaces by default.
     * Experimentality note: this API is experimental because
     * it is not clear whether this option has compelling use-cases.
     */
    @ExperimentalNbtApi
    public var prettyPrintIndent: String = nbt.configuration.prettyPrintIndent

    @OptIn(ExperimentalNbtApi::class)
    override fun build(): StringifiedNbt {
        if (!prettyPrint) {
            require(prettyPrintIndent == StringifiedNbt.configuration.prettyPrintIndent) {
                "Indent should not be specified when default printing mode is used"
            }
        } else if (prettyPrintIndent != StringifiedNbt.configuration.prettyPrintIndent) {
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
                prettyPrint = prettyPrint,
                prettyPrintIndent = prettyPrintIndent,
            ),
            serializersModule = serializersModule,
        )
    }
}
