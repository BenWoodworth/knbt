package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.*

public sealed class Nbt constructor(
    override val configuration: NbtConfiguration,
    override val serializersModule: SerializersModule,
) : NbtFormat, BinaryFormat {
    /**
     * Serializes and encodes the given [value] to the [sink] using the given [serializer].
     *
     * *Note*: It is the caller's responsibility to close the [sink].
     */
    @OkioApi
    @Deprecated(
        "Replaced by encodeToBufferedSink()",
        ReplaceWith(
            "this.encodeToBufferedSink<T>(serializer, value, sink.buffer())",
            "net.benwoodworth.knbt.okio.encodeToBufferedSink",
            "okio.buffer"
        ),
        DeprecationLevel.ERROR
    )
    public fun <T> encodeToSink(serializer: SerializationStrategy<T>, value: T, sink: Sink): Unit =
        encodeToBufferedSink(serializer, value, sink.buffer())

    /**
     * Serializes and encodes the given [value] to the [sink] using the given [serializer].
     *
     * *Note*: It is the caller's responsibility to close the [sink].
     */
    @OkioApi
    @Deprecated(
        "Replaced by encodeToBufferedSink()",
        ReplaceWith(
            "this.encodeToBufferedSink<T>(serializer, value, sink)",
            "net.benwoodworth.knbt.okio.encodeToBufferedSink"
        ),
        DeprecationLevel.ERROR
    )
    public fun <T> encodeToSink(serializer: SerializationStrategy<T>, value: T, sink: BufferedSink): Unit =
        encodeToBufferedSink(serializer, value, sink)

    /**
     * Decodes and deserializes from the given [source] to a value of type [T] using the given [deserializer].
     *
     * *Note*: It is the caller's responsibility to close the [source].
     */
    @OkioApi
    @Deprecated(
        "Replaced by decodeFromBufferedSource()",
        ReplaceWith(
            "this.decodeFromBufferedSource<T>(deserializer, source.buffer())",
            "net.benwoodworth.knbt.okio.decodeFromBufferedSource",
            "okio.buffer"
        ),
        DeprecationLevel.ERROR
    )
    public fun <T> decodeFromSource(deserializer: DeserializationStrategy<T>, source: Source): T =
        decodeFromBufferedSource(deserializer, source.buffer())

    /**
     * Decodes and deserializes from the given [source] to a value of type [T] using the given [deserializer].
     *
     * *Note*: It is the caller's responsibility to close the [source].
     */
    @OkioApi
    @Deprecated(
        "Replaced by decodeFromBufferedSource()",
        ReplaceWith(
            "this.decodeFromBufferedSource<T>(deserializer, source)",
            "net.benwoodworth.knbt.okio.decodeFromBufferedSource"
        ),
        DeprecationLevel.ERROR
    )
    public fun <T> decodeFromSource(deserializer: DeserializationStrategy<T>, source: BufferedSource): T =
        decodeFromBufferedSource(deserializer, source)

    @OptIn(OkioApi::class)
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        Buffer().apply { encodeToBufferedSink(serializer, value, this) }.readByteArray()

    @OptIn(OkioApi::class)
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        decodeFromBufferedSource(deserializer, Buffer().apply { write(bytes) })
}

private object DefaultNbt : Nbt(
    configuration = NbtConfiguration(
        variant = NbtVariant.Java, // Will be ignored by NbtBuilder
        compression = NbtCompression.None, // Will be ignored by NbtBuilder
        compressionLevel = null,
        encodeDefaults = false,
        ignoreUnknownKeys = false,
    ),
    serializersModule = EmptySerializersModule(),
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
public class NbtBuilder internal constructor(nbt: Nbt) {
    /**
     * The variant of NBT binary format to use. Required.
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
     * Module with contextual and polymorphic serializers to be used in the resulting [Nbt] instance.
     */
    public var serializersModule: SerializersModule = nbt.serializersModule

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
    "Replaced by encodeToBufferedSink()",
    ReplaceWith(
        "this.encodeToBufferedSink<T>(value, sink.buffer())",
        "net.benwoodworth.knbt.okio.encodeToBufferedSink",
        "okio.buffer"
    ),
    DeprecationLevel.ERROR
)
public inline fun <reified T> Nbt.encodeToSink(value: T, sink: Sink): Unit =
    encodeToBufferedSink(value, sink.buffer())

/**
 * Encode NBT to a [Sink].
 *
 * *Note*: It is the caller's responsibility to close the [sink].
 */
@OkioApi
@Deprecated(
    "Replaced by encodeToBufferedSink()",
    ReplaceWith(
        "this.encodeToBufferedSink<T>(value, sink)",
        "net.benwoodworth.knbt.okio.encodeToBufferedSink"
    ),
    DeprecationLevel.ERROR
)
public inline fun <reified T> Nbt.encodeToSink(value: T, sink: BufferedSink): Unit =
    encodeToBufferedSink(value, sink)

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
@Deprecated(
    "Replaced by decodeFromBufferedSource()",
    ReplaceWith(
        "this.decodeFromBufferedSource<T>(source.buffer())",
        "net.benwoodworth.knbt.okio.decodeFromBufferedSource",
        "okio.buffer"
    ),
    DeprecationLevel.ERROR
)
public inline fun <reified T> Nbt.decodeFromSource(source: Source): T =
    decodeFromBufferedSource(source.buffer())

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
@Deprecated(
    "Replaced by decodeFromBufferedSource()",
    ReplaceWith(
        "this.decodeFromBufferedSource<T>(source)",
        "net.benwoodworth.knbt.okio.decodeFromBufferedSource"
    ),
    DeprecationLevel.ERROR
)
public inline fun <reified T> Nbt.decodeFromSource(source: BufferedSource): T =
    decodeFromBufferedSource(source)
