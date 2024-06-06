package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.*

public abstract class NbtFormat internal constructor() : SerialFormat {
    public abstract val configuration: NbtFormatConfiguration

    /**
     * Serializes the given [value] into an equivalent [NbtTag] using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to NBT
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtTag {
        lateinit var result: NbtTag
        encodeToNbtWriter(TreeNbtWriter { result = it }, serializer, value)
        return result
    }

    /**
     * Deserializes the given [tag] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given NBT tag is not a valid NBT input for the type [T]
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T]
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag): T =
        decodeFromNbtReader(TreeNbtReader(tag), deserializer)
}

public sealed interface NbtFormatBuilder {
    /**
     * Specifies whether default values of Kotlin properties should be encoded.
     * `false` by default.
     */
    public var encodeDefaults: Boolean

    /**
     * Specifies whether encounters of unknown properties in the input NBT
     * should be ignored instead of throwing [SerializationException].
     * `false` by default.
     */
    public var ignoreUnknownKeys: Boolean

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [NbtFormat] instance.
     */
    public var serializersModule: SerializersModule
}

/**
 * Serializes the given [value] into an equivalent [NbtTag] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to NBT
 */
public inline fun <reified T> NbtFormat.encodeToNbtTag(value: T): NbtTag =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Deserializes the given [tag] into a value of type [T] using a serializer retrieved from the reified type parameter.
 *
 * @throws [SerializationException] if the given NBT tag is not a valid NBT input for the type [T]
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T]
 */
public inline fun <reified T> NbtFormat.decodeFromNbtTag(tag: NbtTag): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)

internal fun <T> NbtFormat.encodeToNbtWriter(writer: NbtWriter, serializer: SerializationStrategy<T>, value: T): Unit =
    tryOrRethrowWithNbtPath {
        NbtWriterEncoder(this, writer).encodeSerializableValue(serializer, value)
    }

internal fun <T> NbtFormat.decodeFromNbtReader(reader: NbtReader, deserializer: DeserializationStrategy<T>): T =
    tryOrRethrowWithNbtPath {
        NbtReaderDecoder(this, reader).decodeSerializableValue(deserializer)
    }
