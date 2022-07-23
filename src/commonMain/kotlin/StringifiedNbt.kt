package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.CharSource
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.StringifiedNbtReader
import net.benwoodworth.knbt.internal.StringifiedNbtWriter
import kotlin.native.concurrent.ThreadLocal

public sealed class StringifiedNbt constructor(
    override val configuration: StringifiedNbtConfiguration,
    override val serializersModule: SerializersModule,
) : NbtFormat, StringFormat {
    /**
     * The default instance of [StringifiedNbt] with default configuration.
     */
    @ThreadLocal
    public companion object Default : StringifiedNbt(
        configuration = StringifiedNbtConfiguration(
            encodeDefaults = false,
            ignoreUnknownKeys = false,
            prettyPrint = false,
            prettyPrintIndent = "    ",
        ),
        serializersModule = EmptySerializersModule(),
    )

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String =
        buildString {
            encodeToNbtWriter(StringifiedNbtWriter(this@StringifiedNbt, this), serializer, value)
        }

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        val source = CharSource(string)
        val decoded = decodeFromNbtReader(StringifiedNbtReader(source), deserializer)

        var char = source.read()
        while (char != CharSource.ReadResult.EOF) {
            if (!char.toChar().isWhitespace()) {
                throw NbtDecodingException("Expected only whitespace after value, but got '$char'")
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
public class StringifiedNbtBuilder internal constructor(stringifiedNbt: StringifiedNbt) {
    /**
     * Specifies whether default values of Kotlin properties should be encoded.
     * `false` by default.
     */
    public var encodeDefaults: Boolean = stringifiedNbt.configuration.encodeDefaults

    /**
     * Specifies whether encounters of unknown properties in the input NBT
     * should be ignored instead of throwing [SerializationException].
     * `false` by default.
     */
    public var ignoreUnknownKeys: Boolean = stringifiedNbt.configuration.ignoreUnknownKeys

    /**
     * Specifies whether resulting Stringified NBT should be pretty-printed.
     *  `false` by default.
     */
    public var prettyPrint: Boolean = stringifiedNbt.configuration.prettyPrint

    /**
     * Specifies indent string to use with [prettyPrint] mode
     * 4 spaces by default.
     * Experimentality note: this API is experimental because
     * it is not clear whether this option has compelling use-cases.
     */
    @ExperimentalNbtApi
    public var prettyPrintIndent: String = stringifiedNbt.configuration.prettyPrintIndent

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [StringifiedNbt] instance.
     */
    public var serializersModule: SerializersModule = stringifiedNbt.serializersModule

    @OptIn(ExperimentalNbtApi::class)
    internal fun build(): StringifiedNbt {
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

        return StringifiedNbtImpl(
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

private class StringifiedNbtImpl(
    configuration: StringifiedNbtConfiguration,
    serializersModule: SerializersModule,
) : StringifiedNbt(configuration, serializersModule)
