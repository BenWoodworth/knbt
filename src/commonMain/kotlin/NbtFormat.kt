package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.StructureKind
import net.benwoodworth.knbt.internal.*

public sealed interface NbtFormat : SerialFormat {
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

@OptIn(ExperimentalSerializationApi::class)
internal fun <T> NbtFormat.encodeToNbtWriter(writer: NbtWriter, serializer: SerializationStrategy<T>, value: T) {
    val rootSerializer = if (serializer.descriptor.kind == StructureKind.CLASS && serializer !== NbtTag.serializer()) {
        RootClassSerializer(serializer)
    } else {
        serializer
    }

    return tryOrRethrowWithNbtPath {
        NbtWriterEncoder(this, writer).encodeSerializableValue(rootSerializer, value)
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal fun <T> NbtFormat.decodeFromNbtReader(reader: NbtReader, deserializer: DeserializationStrategy<T>): T {
    val rootDeserializer =
        if (deserializer.descriptor.kind == StructureKind.CLASS && deserializer !== NbtTag.serializer()) {
            RootClassDeserializer(deserializer)
        } else {
            deserializer
        }

    return tryOrRethrowWithNbtPath {
        NbtReaderDecoder(this, reader).decodeSerializableValue(rootDeserializer)
    }
}
