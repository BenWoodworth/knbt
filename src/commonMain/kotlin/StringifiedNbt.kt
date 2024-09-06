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
    definiteLengthEncoding = false,
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
