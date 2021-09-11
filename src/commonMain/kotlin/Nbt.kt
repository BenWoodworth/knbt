package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.NbtVariant.*
import net.benwoodworth.knbt.internal.BinaryNbtReader
import net.benwoodworth.knbt.internal.BinaryNbtWriter
import okio.Buffer
import okio.Sink
import okio.Source
import okio.use
import kotlin.native.concurrent.ThreadLocal

private const val requireConfig = "Configuring Nbt is now required"
private const val nbtTodo = "Nbt {\n variant = NbtVariant.TODO\n compression = NbtCompression.TODO\n }"
private const val knbt = "net.benwoodworth.knbt"
private const val kxs = "kotlinx.serialization"

@OptIn(ExperimentalSerializationApi::class)
public sealed class Nbt constructor(
    @Suppress("EXPERIMENTAL_OVERRIDE")
    @ExperimentalNbtApi
    override val configuration: NbtConfiguration,

    override val serializersModule: SerializersModule,
) : NbtFormat, BinaryFormat {
    /**
     * The default instance of [Nbt] with default configuration.
     */
    @ThreadLocal
    @Deprecated(
        "Configuring Nbt is now required",
        ReplaceWith(
            nbtTodo,
            "net.benwoodworth.knbt.Nbt",
            "net.benwoodworth.knbt.NbtVariant",
            "net.benwoodworth.knbt.NbtCompression",
        ),
        level = DeprecationLevel.ERROR,
    )
    public companion object Default {
        //region deprecated methods
        /**
         * Serializes and encodes the given [value] to the [sink] using the given [serializer].
         *
         * *Note*: It is the caller's responsibility to close the [sink].
         */
        @OkioApi
        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.encodeToSink<T>(serializer, value, sink)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression",
            ),
            DeprecationLevel.ERROR
        )
        public fun <T> encodeToSink(serializer: SerializationStrategy<T>, value: T, sink: Sink): Unit =
            error(requireConfig)

        /**
         * Serializes and encodes the given [value] to the [sink] using the given [serializer].
         *
         * *Note*: It is the caller's responsibility to close the [sink].
         */
        @OkioApi
        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.encodeToSink<T>(value, sink)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression", "$knbt.encodeToSink",
            ),
            DeprecationLevel.ERROR
        )
        public fun <T> encodeToSink(value: T, sink: Sink): Unit =
            error(requireConfig)

        /**
         * Decodes and deserializes from the given [source] to a value of type [T] using the given [deserializer].
         *
         * *Note*: It is the caller's responsibility to close the [source].
         */
        @OkioApi
        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.decodeFromSource<T>(deserializer, source)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression", "$knbt.decodeFromSource",
            ),
            DeprecationLevel.ERROR
        )
        public fun <T> decodeFromSource(deserializer: DeserializationStrategy<T>, source: Source): T =
            error(requireConfig)

        /**
         * Decodes and deserializes from the given [source] to a value of type [T] using
         * serializer retrieved from the reified type parameter.
         *
         * *Note*: It is the caller's responsibility to close the [source].
         */
        @OkioApi
        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.decodeFromSource<T>(source)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression",
            ),
            DeprecationLevel.ERROR
        )
        public fun <T> decodeFromSource(source: Source): T =
            error(requireConfig)

        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.encodeToByteArray<T>(serializer, value)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression",
            ),
            DeprecationLevel.ERROR
        )
        public fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
            error(requireConfig)

        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.encodeToByteArray<T>(value)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression", "$kxs.encodeToByteArray",
            ),
            DeprecationLevel.ERROR
        )
        public fun <T> encodeToByteArray(value: T): ByteArray =
            error(requireConfig)

        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.decodeFromByteArray<T>(deserializer, bytes)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression",
            ),
            DeprecationLevel.ERROR
        )
        @OptIn(OkioApi::class)
        public fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
            error(requireConfig)

        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.decodeFromByteArray<T>(bytes)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression", "$kxs.decodeFromByteArray",
            ),
            DeprecationLevel.ERROR
        )
        @OptIn(OkioApi::class)
        public fun <T> decodeFromByteArray(bytes: ByteArray): T =
            error(requireConfig)

        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.encodeToHexString<T>(serializer, value)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression", "$kxs.encodeToHexString",
            ),
            DeprecationLevel.ERROR
        )
        @OptIn(OkioApi::class)
        public fun <T> encodeToHexString(serializer: SerializationStrategy<T>, value: T): String =
            error(requireConfig)

        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.encodeToHexString<T>(value)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression", "$kxs.encodeToHexString",
            ),
            DeprecationLevel.ERROR
        )
        @OptIn(OkioApi::class)
        public fun <T> encodeToHexString(value: T): String =
            error(requireConfig)

        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.decodeFromHexString<T>(deserializer, hex)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression", "$kxs.decodeFromHexString",
            ),
            DeprecationLevel.ERROR
        )
        @OptIn(OkioApi::class)
        public fun <T> decodeFromHexString(deserializer: DeserializationStrategy<T>, hex: String): String =
            error(requireConfig)

        @Deprecated(
            requireConfig,
            ReplaceWith(
                "$nbtTodo.decodeFromHexString<T>(hex)",
                "$knbt.Nbt", "$knbt.NbtVariant", "$knbt.NbtCompression", "$kxs.decodeFromHexString",
            ),
            DeprecationLevel.ERROR
        )
        @OptIn(OkioApi::class)
        public fun <T> decodeFromHexString(hex: String): String =
            error(requireConfig)

        @Deprecated(
            "Use StringifiedNbt instead",
            ReplaceWith(
                "StringifiedNbt.encodeToString<T>(serializer, value)",
                "$knbt.StringifiedNbt",
            ),
            DeprecationLevel.ERROR,
        )
        public fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String =
            error("Use StringifiedNbt instead")

        @Deprecated(
            "Use StringifiedNbt instead",
            ReplaceWith(
                "StringifiedNbt.encodeToString<T>(value)",
                "$knbt.StringifiedNbt", "$kxs.encodeToString",
            ),
            DeprecationLevel.ERROR,
        )
        public fun <T> encodeToString(value: T): String =
            error("Use StringifiedNbt instead")
        //endregion
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
    //endregion

    //region SNBT
    @Deprecated(
        "Use StringifiedNbt instead",
        ReplaceWith(
            "StringifiedNbt {}.encodeToString<T>(serializer, value)",
            "net.benwoodworth.knbt.StringifiedNbt",
        ),
        DeprecationLevel.ERROR,
    )
    public open fun <T> encodeToString(serializer: SerializationStrategy<T>, value: T): String =
        error("Use StringifiedNbt instead")

    @Deprecated(
        "Use StringifiedNbt instead",
        ReplaceWith(
            "StringifiedNbt {}.encodeToString<T>(value)",
            "net.benwoodworth.knbt.StringifiedNbt",
            "net.benwoodworth.knbt.encodeToString",
        ),
        DeprecationLevel.ERROR,
    )
    public open fun <T> encodeToString(value: T): String =
        error("Use StringifiedNbt instead")
}

@OptIn(ExperimentalNbtApi::class, ExperimentalSerializationApi::class)
private object DefaultNbt : Nbt(
    configuration = NbtConfiguration(
        variant = Companion.Java, // Will be ignored by NbtBuilder
        compression = NbtCompression.None, // Will be ignored by NbtBuilder
        compressionLevel = null,
        encodeDefaults = false,
        ignoreUnknownKeys = false,
    ),
    serializersModule = EmptySerializersModule,
)

/**
 * Creates an instance of [Nbt] configured from the optionally given [Nbt instance][from]
 * and adjusted with [builderAction].
 *
 * [variant][NbtBuilder.variant] and [compression][NbtBuilder.compression] are required.
 */
public fun Nbt(from: Nbt = DefaultNbt, builderAction: NbtBuilder.() -> Unit): Nbt {
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
     * The variant of NBT binary format to use. Required.
     *
     * Java Edition only uses [BigEndian].
     *
     * Bedrock Edition uses:
     * - [LittleEndian] for save files.
     * - [BigEndian] for resource files.
     * - [LittleEndianBase128] for network transport.
     */
    public var variant: NbtVariant? =
        if (nbt === DefaultNbt) null else nbt.configuration.variant

    /**
     * The compression method to use when writing NBT binary. Required.
     */
    public var compression: NbtCompression? =
        if (nbt === DefaultNbt) null else nbt.configuration.compression

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
    @Deprecated("Use StringifiedNbt instead", level = DeprecationLevel.ERROR)
    public var prettyPrint: Boolean
        get() = error("Use StringifiedNbt instead")
        set(_) = error("Use StringifiedNbt instead")

    /**
     * Specifies indent string to use with [prettyPrint] mode
     * 4 spaces by default.
     * Experimentality note: this API is experimental because
     * it is not clear whether this option has compelling use-cases.
     */
    @Deprecated("Use StringifiedNbt instead", level = DeprecationLevel.ERROR)
    @ExperimentalNbtApi
    public var prettyPrintIndent: String
        get() = error("Use StringifiedNbt instead")
        set(_) = error("Use StringifiedNbt instead")

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [Nbt] instance.
     */
    public var serializersModule: SerializersModule = nbt.serializersModule

    @OptIn(ExperimentalSerializationApi::class, ExperimentalNbtApi::class)
    internal fun build(): Nbt {
        val variant = variant
        val compression = compression

        require(variant != null && compression != null) {
            when {
                variant == null && compression == null -> "Variant and compression are required but are null"
                variant == null -> "Variant is required but is null"
                else -> "Compression is required but is null"
            }
        }

        return NbtImpl(
            configuration = NbtConfiguration(
                variant = variant,
                compression = compression,
                compressionLevel = compressionLevel,
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
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
public inline fun <reified T> Nbt.encodeToSink(value: T, sink: Sink): Unit =
    encodeToSink(serializersModule.serializer(), value, sink)

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
public inline fun <reified T> Nbt.decodeFromSource(source: Source): T =
    decodeFromSource(serializersModule.serializer(), source)
