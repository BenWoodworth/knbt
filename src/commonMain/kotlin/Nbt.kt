package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.NbtVariant.*
import net.benwoodworth.knbt.internal.*
import okio.Buffer
import okio.Sink
import okio.Source
import okio.use
import kotlin.native.concurrent.ThreadLocal

@OptIn(ExperimentalSerializationApi::class)
public sealed class Nbt constructor(
    @ExperimentalNbtApi
    public val configuration: NbtConfiguration,

    override val serializersModule: SerializersModule,
) : BinaryFormat, StringFormat {
    /**
     * The default instance of [Nbt] with default configuration.
     */
    @ThreadLocal
    public companion object Default : Nbt(
        configuration = NbtConfiguration(
            variant = null,
            compression = null,
            compressionLevel = null,
            encodeDefaults = false,
            ignoreUnknownKeys = false,
            prettyPrint = false,
            prettyPrintIndent = "    ",
        ),
        serializersModule = EmptySerializersModule,
    )

    @OptIn(ExperimentalNbtApi::class)
    internal fun <T> encodeToNbtWriter(writer: NbtWriter, serializer: SerializationStrategy<T>, value: T) {
        val nbtRoot = serializer.descriptor.annotations
            .firstOrNull { it is NbtRoot } as NbtRoot?

        val rootSerializer = if (nbtRoot == null) {
            serializer
        } else {
            NbtRootSerializer(nbtRoot, serializer)
        }

        return DefaultNbtEncoder(this, writer).encodeSerializableValue(rootSerializer, value)
    }

    @OptIn(ExperimentalNbtApi::class)
    internal fun <T> decodeFromNbtReader(reader: NbtReader, deserializer: DeserializationStrategy<T>): T {
        val nbtRoot = deserializer.descriptor.annotations
            .firstOrNull { it is NbtRoot } as NbtRoot?

        val rootDeserializer = if (nbtRoot == null) {
            deserializer
        } else {
            NbtRootDeserializer(nbtRoot, deserializer)
        }

        return NbtDecoder(this, reader).decodeSerializableValue(rootDeserializer)
    }

    /**
     * Serializes and encodes the given [value] to the [sink] using the given [serializer].
     *
     * *Note*: It is the caller's responsibility to close the [sink].
     */
    @OkioApi
    public fun <T> encodeToSink(serializer: SerializationStrategy<T>, value: T, sink: Sink): Unit =
        BinaryNbtWriter(this, sink).use { writer ->
            encodeToNbtWriter(writer, serializer, value)
        }

    /**
     * Decodes and deserializes from the given [source] to a value of type [T] using the given [deserializer].
     *
     * *Note*: It is the caller's responsibility to close the [source].
     */
    @OkioApi
    public fun <T> decodeFromSource(deserializer: DeserializationStrategy<T>, source: Source): T =
        BinaryNbtReader(this, source).use { reader ->
            decodeFromNbtReader(reader, deserializer)
        }

    @OptIn(OkioApi::class)
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        Buffer().apply { encodeToSink(serializer, value, this) }.readByteArray()

    @OptIn(OkioApi::class)
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        decodeFromSource(deserializer, Buffer().apply { write(bytes) })

    override fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String =
        buildString {
            encodeToNbtWriter(StringifiedNbtWriter(this@Nbt, this), serializer, value)
        }

    @Deprecated("Decoding from Stringified NBT is not yet supported", level = DeprecationLevel.HIDDEN)
    override fun <T> decodeFromString(deserializer: DeserializationStrategy<T>, string: String): T {
        TODO("Decoding from Stringified NBT is not yet supported")
    }

    /**
     * Serializes and encodes the given [value] to an [NbtTag] using the given [serializer].
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtTag {
        lateinit var result: NbtTag
        encodeToNbtWriter(TreeNbtWriter { result = it }, serializer, value)
        return result
    }

    /**
     * Decodes and deserializes the given [tag] to a value of type [T] using the given [deserializer].
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag): T =
        decodeFromNbtReader(TreeNbtReader(tag), deserializer)
}

/**
 * Creates an instance of [Nbt] configured from the optionally given [Nbt instance][from]
 * and adjusted with [builderAction].
 */
public fun Nbt(from: Nbt = Nbt.Default, builderAction: NbtBuilder.() -> Unit): Nbt {
    val builder = NbtBuilder(from)
    builder.builderAction()
    return builder.build()
}

/**
 * Builder of the [Nbt] instance provided by `Nbt { ... }` factory function.
 */
@NbtDslMarker
@OptIn(ExperimentalNbtApi::class)
public class NbtBuilder internal constructor(nbt: Nbt) {
    /**
     * The variant of NBT binary format to use. Must not be `null` when serializing binary.
     * `null` by default.
     *
     * Java Edition only uses [BigEndian].

     * Bedrock Edition uses:
     * - [LittleEndian] for save files.
     * - [BigEndian] for resource files.
     * - [LittleEndianBase128] for network transport.
     */
    public var variant: NbtVariant? = nbt.configuration.variant

    /**
     * The compression method to use when writing NBT binary.
     * `null` by default.
     */
    public var compression: NbtCompression? = nbt.configuration.compression

    /**
     * The compression level, in `0..9` or `null`.
     * `null` by default.
     *
     * - `0` gives no compression at all
     * - `1` gives the best speed
     * - `9` gives the best compression.
     * - `null` requests a compromise between speed and compression.
     */
    public var compressionLevel: Int? = nbt.configuration.compressionLevel
        set(value) {
            require(value == null || value in 0..9) { "Compression level must be in 0..9 or null." }
            field = value
        }

    /**
     * Specifies whether default values of Kotlin properties should be encoded.
     * `false` by default.
     */
    public var encodeDefaults: Boolean = nbt.configuration.encodeDefaults

    /**
     * Specifies whether encounters of unknown properties in the input NBT
     * should be ignored instead of throwing [SerializationException].
     * `false` by default.
     */
    public var ignoreUnknownKeys: Boolean = nbt.configuration.ignoreUnknownKeys

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

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [Nbt] instance.
     */
    public var serializersModule: SerializersModule = nbt.serializersModule

    @OptIn(ExperimentalSerializationApi::class, ExperimentalNbtApi::class)
    internal fun build(): Nbt {
        if (!prettyPrint) {
            require(prettyPrintIndent == Nbt.configuration.prettyPrintIndent) {
                "Indent should not be specified when default printing mode is used"
            }
        } else if (prettyPrintIndent != Nbt.configuration.prettyPrintIndent) {
            // Values allowed by JSON specification as whitespaces
            val allWhitespaces = prettyPrintIndent.all { it == ' ' || it == '\t' || it == '\r' || it == '\n' }
            require(allWhitespaces) {
                "Only whitespace, tab, newline and carriage return are allowed as pretty print symbols. Had $prettyPrintIndent"
            }
        }

        return NbtImpl(
            configuration = NbtConfiguration(
                variant = variant,
                compression = compression,
                compressionLevel = compressionLevel,
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
                prettyPrint = prettyPrint,
                prettyPrintIndent = prettyPrintIndent,
            ),
            serializersModule = serializersModule,
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
private class NbtImpl(
    configuration: NbtConfiguration,
    serializersModule: SerializersModule,
) : Nbt(configuration, serializersModule)

/**
 * Encode NBT to a [Sink].
 *
 * *Note*: It is the caller's responsibility to close the [sink].
 */
@OkioApi
@Deprecated(
    "Replaced with encodeToSink(...)",
    ReplaceWith("this.encodeToSink<T>(serializer, value, sink)", "net.benwoodworth.knbt.encodeToSink"),
)
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Nbt.encodeTo(sink: Sink, serializer: SerializationStrategy<T>, value: T): Unit =
    encodeToSink(serializer, value, sink)

/**
 * Encode NBT to a [Sink].
 *
 * *Note*: It is the caller's responsibility to close the [sink].
 */
@OkioApi
public inline fun <reified T> Nbt.encodeToSink(value: T, sink: Sink): Unit =
    encodeToSink(serializersModule.serializer(), value, sink)

/**
 * Encode NBT to a [Sink].
 *
 * *Note*: It is the caller's responsibility to close the [sink].
 */
@OkioApi
@Deprecated(
    "Replaced with encodeToSink(...)",
    ReplaceWith("this.encodeToSink<T>(value, sink)", "net.benwoodworth.knbt.encodeToSink"),
)
public inline fun <reified T> Nbt.encodeTo(sink: Sink, value: T): Unit =
    encodeToSink(value, sink)

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
@Deprecated(
    "Replaced with decodeFromSource(...)",
    ReplaceWith("this.decodeFromSource<T>(deserializer, source)", "net.benwoodworth.knbt.decodeFromSource"),
)
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Nbt.decodeFrom(source: Source, deserializer: DeserializationStrategy<T>): T =
    decodeFromSource(deserializer, source)

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
public inline fun <reified T> Nbt.decodeFromSource(source: Source): T =
    decodeFromSource(serializersModule.serializer(), source)

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
@Deprecated(
    "Replaced with decodeFromSource(...)",
    ReplaceWith("this.decodeFromSource<T>(source)", "net.benwoodworth.knbt.decodeFromSource"),
)
public inline fun <reified T> Nbt.decodeFrom(source: Source): T =
    decodeFromSource(source)

/**
 * Encode NBT to a [ByteArray].
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
@Deprecated(
    "Use NBT member function instead",
    ReplaceWith("this.encodeToByteArray<T>(serializer, value)"),
)
public fun <T> Nbt.encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
    encodeToByteArray(serializer, value)

/**
 * Encode NBT to a [ByteArray].
 */
public inline fun <reified T> Nbt.encodeToByteArray(value: T): ByteArray =
    encodeToByteArray(serializersModule.serializer(), value)

/**
 * Decode NBT from a [ByteArray].
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "NOTHING_TO_INLINE")
@Deprecated(
    "Use NBT member function instead",
    ReplaceWith(
        "this.decodeFromByteArray<T>(serializer, byteArray)",
        "kotlinx.serialization.BinaryFormat",
    ),
)
public inline fun <T> Nbt.decodeFromByteArray(deserializer: DeserializationStrategy<T>, byteArray: ByteArray): T =
    decodeFromByteArray(deserializer, byteArray)

/**
 * Decode NBT from a [ByteArray].
 */
@OptIn(ExperimentalSerializationApi::class)
@Deprecated(
    "Use kotlinx.serialization function instead",
    ReplaceWith(
        "this.decodeFromByteArray<T>(serializer, byteArray)",
        "kotlinx.serialization.decodeFromByteArray",
    ),
)
public inline fun <reified T> Nbt.decodeFromByteArray(byteArray: ByteArray): T =
    (this as BinaryFormat).decodeFromByteArray(byteArray)

/**
 * Encode to Stringified NBT.
 */
@ExperimentalNbtApi
@Deprecated(
    "Replaced with encodeToString(...)",
    ReplaceWith("this.encodeToString<T>(serializer, value)"),
)
public fun <T> Nbt.encodeToStringifiedNbt(serializer: SerializationStrategy<T>, value: T): String =
    encodeToString(serializer, value)

/**
 * Encode to Stringified NBT.
 */
@ExperimentalNbtApi
@Deprecated(
    "Replaced with encodeToString(...)",
    ReplaceWith("this.encodeToString<T>(value)", "kotlinx.serialization.encodeToString"),
)
public inline fun <reified T> Nbt.encodeToStringifiedNbt(value: T): String =
    encodeToString(value)

/**
 * Encode to [NbtTag].
 */
public inline fun <reified T> Nbt.encodeToNbtTag(value: T): NbtTag =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Decode from [NbtTag].
 */
public inline fun <reified T> Nbt.decodeFromNbtTag(tag: NbtTag): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)
