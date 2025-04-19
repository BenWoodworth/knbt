package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import net.benwoodworth.knbt.internal.*

public abstract class NbtFormat internal constructor() : SerialFormat {
    internal abstract val name: String
    internal abstract val capabilities: NbtCapabilities

    public abstract val configuration: NbtFormatConfiguration

    /**
     * Serializes the given [value] into an equivalent [NbtTag] using the given [serializer].
     *
     * This method is considered unsafe because it returns a named [NbtTag], even though this [NbtFormat]
     * may not have a root name or might only return a more specific [NbtTag] type.
     *
     * [NbtFormat]s should restrict the returned [NbtTag] type to only those that are supported.
     * [NbtFormat]s with unnamed root tags should discard the returned [NbtNamed.name][name].
     *
     * @throws [SerializationException] if the given value cannot be serialized to NBT.
     */
    protected fun <T> encodeToNbtTagUnsafe(serializer: SerializationStrategy<T>, value: T): NbtNamed<NbtTag> {
        lateinit var result: NbtNamed<NbtTag>
        val context = SerializationNbtContext(this)
        val writer = TreeNbtWriter { result = it }
        val encoder = NbtWriterEncoder(this, context, writer)

        encoder.encodeSerializableValue(serializer, value)
        return result
    }

    /**
     * Deserializes the given [tag] into a value of type [T] using the given [deserializer].
     *
     * This method is considered unsafe because it accepts a named [NbtTag] of any type, even though this [NbtFormat]
     * may not have a root name or might only accept a more specific [NbtTag] type.
     *
     * [NbtFormat]s should restrict the provided [tag] type to only those that are supported.
     * [NbtFormat]s with unnamed root tags should call this method with an empty name.
     *
     * @throws [SerializationException] if the given NBT tag is not a valid NBT input for the type [T].
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
     */
    protected fun <T> decodeFromNbtTagUnsafe(deserializer: DeserializationStrategy<T>, tag: NbtNamed<NbtTag>): T {
        val context = SerializationNbtContext(this)
        val reader = TreeNbtReader(tag)
        val decoder = NbtReaderDecoder(this, context, reader)

        return decoder.decodeSerializableValue(deserializer)
    }
}
