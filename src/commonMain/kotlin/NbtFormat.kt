package net.benwoodworth.knbt

import kotlinx.serialization.*
import net.benwoodworth.knbt.internal.*

@OptIn(ExperimentalSerializationApi::class)
public sealed interface NbtFormat : SerialFormat {
    @ExperimentalNbtApi
    public val configuration: NbtFormatConfiguration

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
 * Serializes and encodes the given [value] to an [NbtTag] using
 * serializer retrieved from the reified type parameter.
 */
public inline fun <reified T> NbtFormat.encodeToNbtTag(value: T): NbtTag =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Decodes and deserializes the given [tag] to a value of type [T] using
 * serializer retrieved from the reified type parameter.
 */
public inline fun <reified T> NbtFormat.decodeFromNbtTag(tag: NbtTag): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)

@OptIn(ExperimentalNbtApi::class, ExperimentalSerializationApi::class)
internal fun <T> NbtFormat.encodeToNbtWriter(writer: NbtWriter, serializer: SerializationStrategy<T>, value: T) {
    val nbtRoot = serializer.descriptor.annotations
        .firstOrNull { it is NbtRoot } as NbtRoot?

    val rootSerializer = if (nbtRoot == null) {
        serializer
    } else {
        NbtRootSerializer(nbtRoot, serializer)
    }

    return DefaultNbtEncoder(this, writer)
        .encodeSerializableValue(rootSerializer, value)
}

@OptIn(ExperimentalNbtApi::class, ExperimentalSerializationApi::class)
internal fun <T> NbtFormat.decodeFromNbtReader(reader: NbtReader, deserializer: DeserializationStrategy<T>): T {
    val nbtRoot = deserializer.descriptor.annotations
        .firstOrNull { it is NbtRoot } as NbtRoot?

    val rootDeserializer = if (nbtRoot == null) {
        deserializer
    } else {
        NbtRootDeserializer(nbtRoot, deserializer)
    }

    return NbtDecoder(this, reader).decodeSerializableValue(rootDeserializer)
}
