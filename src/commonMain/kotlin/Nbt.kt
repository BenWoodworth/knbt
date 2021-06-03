package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.NbtVariant.*
import net.benwoodworth.knbt.internal.*
import net.benwoodworth.knbt.tag.NbtTag
import okio.Buffer
import okio.Sink
import okio.Source
import okio.use
import kotlin.native.concurrent.ThreadLocal

@OptIn(ExperimentalSerializationApi::class)
public sealed class Nbt constructor(
    public val configuration: NbtConfiguration,
    public val serializersModule: SerializersModule,
) {
    /**
     * The default instance of [Nbt] with default configuration.
     */
    @ThreadLocal
    public companion object Default : Nbt(
        configuration = NbtConfiguration(
            variant = null,
            compression = null,
            encodeDefaults = false,
            ignoreUnknownKeys = false,
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

    @OptIn(ExperimentalSerializationApi::class, ExperimentalNbtApi::class)
    internal fun build(): Nbt {
        return NbtImpl(
            configuration = NbtConfiguration(
                variant = variant,
                compression = compression,
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
public fun <T> Nbt.encodeTo(sink: Sink, serializer: SerializationStrategy<T>, value: T): Unit =
    BinaryNbtWriter(this, sink).use { writer ->
        encodeToNbtWriter(writer, serializer, value)
    }

/**
 * Encode NBT to a [Sink].
 *
 * *Note*: It is the caller's responsibility to close the [sink].
 */
@OkioApi
public inline fun <reified T> Nbt.encodeTo(sink: Sink, value: T): Unit =
    encodeTo(sink, serializersModule.serializer(), value)

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
public fun <T> Nbt.decodeFrom(source: Source, deserializer: DeserializationStrategy<T>): T =
    BinaryNbtReader(this, source).use { reader ->
        decodeFromNbtReader(reader, deserializer)
    }

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
public inline fun <reified T> Nbt.decodeFrom(source: Source): T =
    decodeFrom(source, serializersModule.serializer())

/**
 * Encode NBT to a [ByteArray].
 */
@OptIn(OkioApi::class)
public fun <T> Nbt.encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
    Buffer().apply { encodeTo(this, serializer, value) }.readByteArray()

/**
 * Encode NBT to a [ByteArray].
 */
public inline fun <reified T> Nbt.encodeToByteArray(value: T): ByteArray =
    encodeToByteArray(serializersModule.serializer(), value)

/**
 * Decode NBT from a [ByteArray].
 */
@OptIn(OkioApi::class)
public fun <T> Nbt.decodeFromByteArray(deserializer: DeserializationStrategy<T>, byteArray: ByteArray): T =
    decodeFrom(Buffer().apply { write(byteArray) }, deserializer)

/**
 * Decode NBT from a [ByteArray].
 */
public inline fun <reified T> Nbt.decodeFromByteArray(byteArray: ByteArray): T =
    decodeFromByteArray(serializersModule.serializer(), byteArray)

/**
 * Encode to Stringified NBT.
 */
@ExperimentalNbtApi
public fun <T> Nbt.encodeToStringifiedNbt(serializer: SerializationStrategy<T>, value: T): String =
    buildString {
        encodeToNbtWriter(StringifiedNbtWriter(this), serializer, value)
    }

/**
 * Encode to Stringified NBT.
 */
@ExperimentalNbtApi
public inline fun <reified T> Nbt.encodeToStringifiedNbt(value: T): String =
    encodeToStringifiedNbt(serializersModule.serializer(), value)

/**
 * Encode to [NbtTag].
 */
public fun <T> Nbt.encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtTag {
    lateinit var result: NbtTag
    encodeToNbtWriter(TreeNbtWriter { result = it }, serializer, value)
    return result
}

/**
 * Encode to [NbtTag].
 */
public inline fun <reified T> Nbt.encodeToNbtTag(value: T): NbtTag =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Decode from [NbtTag].
 */
public fun <T> Nbt.decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag): T =
    decodeFromNbtReader(TreeNbtReader(tag), deserializer)

/**
 * Decode from [NbtTag].
 */
public inline fun <reified T> Nbt.decodeFromNbtTag(tag: NbtTag): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)

