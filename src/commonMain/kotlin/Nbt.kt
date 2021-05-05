package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.internal.*
import net.benwoodworth.knbt.tag.NbtTag
import okio.Buffer
import okio.Sink
import okio.Source
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
    public companion object Default : Nbt(NbtConfiguration(), EmptySerializersModule)

    private class NbtImpl(configuration: NbtConfiguration, module: SerializersModule) : Nbt(configuration, module)

    /**
     * Encode NBT to a [Sink].
     */
    @OkioApi
    public fun <T> encodeTo(sink: Sink, serializer: SerializationStrategy<T>, value: T): Unit =
        DefaultNbtEncoder(this, BinaryNbtWriter(sink)).encodeSerializableValue(serializer, value)

    /**
     * Decode NBT from a [Source].
     */
    @OkioApi
    public fun <T> decodeFrom(source: Source, deserializer: DeserializationStrategy<T>): T =
        DefaultNbtDecoder(this, BinaryNbtReader(source)).decodeSerializableValue(deserializer)

    /**
     * Encode NBT to a [ByteArray].
     */
    @OptIn(OkioApi::class)
    public fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        Buffer().apply { encodeTo(this, serializer, value) }.readByteArray()

    /**
     * Decode NBT from a [ByteArray].
     */
    @OptIn(OkioApi::class)
    public fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, byteArray: ByteArray): T =
        decodeFrom(Buffer().apply { write(byteArray) }, deserializer)

    /**
     * Encode to Stringified NBT.
     */
    public fun <T> encodeToStringifiedNbt(serializer: SerializationStrategy<T>, value: T): String = buildString {
        DefaultNbtEncoder(this@Nbt, StringifiedNbtWriter(this)).encodeSerializableValue(serializer, value)
    }

    /**
     * Encode to [NbtTag].
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtTag {
        var result: NbtTag? = null
        DefaultNbtEncoder(this, TreeNbtWriter { result = it }).encodeSerializableValue(serializer, value)
        return result!!
    }

    /**
     * Decode from [NbtTag].
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag): T =
        DefaultNbtDecoder(this, TreeNbtReader(tag)).decodeSerializableValue(deserializer)
}

/**
 * Creates an instance of [Nbt] configured from the optionally given [Nbt instance][from]
 * and adjusted with [builderAction].
 */
public fun Nbt(from: Nbt = Nbt.Default, builderAction: NbtBuilder.() -> Unit): Nbt {
    val builder = NbtBuilder(from)
    builder.builderAction()
    val conf = builder.build()
    return NbtImpl(conf, builder.serializersModule)
}

/**
 * Builder of the [Nbt] instance provided by `Nbt { ... }` factory function.
 */
@Suppress("unused", "DeprecatedCallableAddReplaceWith")
@OptIn(ExperimentalSerializationApi::class)
public class NbtBuilder internal constructor(Nbt: Nbt) {
    /**
     * Specifies whether default values of Kotlin properties should be encoded.
     * `false` by default.
     */
    public var encodeDefaults: Boolean = Nbt.configuration.encodeDefaults

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [Nbt] instance.
     */
    public var serializersModule: SerializersModule = Nbt.serializersModule

    @OptIn(ExperimentalSerializationApi::class)
    internal fun build(): NbtConfiguration {
        return NbtConfiguration(
            encodeDefaults = encodeDefaults,
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
private class NbtImpl(configuration: NbtConfiguration, module: SerializersModule) : Nbt(configuration, module)

/**
 * Encode NBT to a [Sink].
 */
@OkioApi
public inline fun <reified T> Nbt.encodeTo(sink: Sink, value: T): Unit =
    encodeTo(sink, serializer(), value)

/**
 * Decode NBT from a [Source].
 */
@OkioApi
public inline fun <reified T> Nbt.decodeFrom(source: Source): T =
    decodeFrom(source, serializer())

/**
 * Encode NBT to a [ByteArray].
 */
public inline fun <reified T> Nbt.encodeToByteArray(value: T): ByteArray =
    encodeToByteArray(serializer(), value)

/**
 * Decode NBT from a [ByteArray].
 */
public inline fun <reified T> Nbt.decodeFromByteArray(byteArray: ByteArray): T =
    decodeFromByteArray(serializer(), byteArray)

/**
 * Encode to Stringified NBT.
 */
public inline fun <reified T> Nbt.encodeToStringifiedNbt(value: T): String =
    encodeToStringifiedNbt(serializer(), value)

/**
 * Encode to [NbtTag].
 */
public inline fun <reified T> Nbt.encodeToNbtTag(value: T): NbtTag =
    encodeToNbtTag(serializer(), value)

/**
 * Decode from [NbtTag].
 */
public inline fun <reified T> Nbt.decodeFromNbtTag(tag: NbtTag): T =
    decodeFromNbtTag(serializer(), tag)

