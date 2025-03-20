package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.*
import kotlin.native.concurrent.ThreadLocal

private val stringifiedNbtCapabilities = NbtCapabilities(
    namedRoot = false,
    definiteLengthEncoding = false,
    rootTagTypes = NbtTagTypeSet(NbtTagType.entries - NbtTagType.TAG_End),
)

public open class StringifiedNbt internal constructor(
    override val configuration: StringifiedNbtConfiguration,
    override val serializersModule: SerializersModule,
) : NbtFormat(), StringFormat {
    override val name: String get() = "SNBT"
    override val capabilities: NbtCapabilities get() = stringifiedNbtCapabilities

    /**
     * The default instance of [StringifiedNbt] with default configuration.
     */
    @ThreadLocal
    public companion object Default : StringifiedNbt(
        configuration = StringifiedNbtConfiguration(
            encodeDefaults = NbtFormatDefaults.encodeDefaults,
            ignoreUnknownKeys = NbtFormatDefaults.ignoreUnknownKeys,
            lenientNbtNames = NbtFormatDefaults.lenientNbtNames,
            prettyPrint = StringifiedNbtDefaults.prettyPrint,
            prettyPrintIndent = StringifiedNbtDefaults.prettyPrintIndent,
        ),
        serializersModule = NbtFormatDefaults.serializersModule,
    )

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String =
        buildString {
            val context = SerializationNbtContext(this@StringifiedNbt)
            val writer = StringifiedNbtWriter(this@StringifiedNbt, this)
            val encoder = NbtWriterEncoder(this@StringifiedNbt, context, writer)

            encoder.encodeSerializableValue(serializer, value)
        }

    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        val context = SerializationNbtContext(this)
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

    /**
     * Serializes the given [value] into an equivalent [NbtTag] using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to SNBT.
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtTag =
        encodeToNbtTagUnsafe(serializer, value).value

    /**
     * Deserializes the given [tag] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given NBT tag is not a valid SNBT input for the type [T].
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag): T =
        decodeFromNbtTagUnsafe(deserializer, NbtNamed("", tag))
}

/**
 * Creates an instance of [StringifiedNbt] configured from the optionally given [StringifiedNbt instance][from]
 * and adjusted with [builderAction].
 */
public fun StringifiedNbt(
    from: StringifiedNbt? = null,
    builderAction: StringifiedNbtBuilder.() -> Unit,
): StringifiedNbt {
    val builder = StringifiedNbtBuilder(from)
    builder.builderAction()
    return builder.build()
}

/**
 * Serializes the given [value] into an equivalent [NbtTag] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to SNBT.
 */
public inline fun <reified T> StringifiedNbt.encodeToNbtTag(value: T): NbtTag =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Deserializes the given [tag] into a value of type [T] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given NBT tag is not a valid SNBT input for the type [T].
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
 */
public inline fun <reified T> StringifiedNbt.decodeFromNbtTag(tag: NbtTag): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)
