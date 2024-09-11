package net.benwoodworth.knbt

import kotlinx.serialization.*
import net.benwoodworth.knbt.internal.*

public abstract class NbtFormat internal constructor() : SerialFormat {
    internal abstract val name: String
    internal abstract val capabilities: NbtCapabilities

    public abstract val configuration: NbtFormatConfiguration

    /**
     * Serializes the given [value] into an equivalent [NbtTag] using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to NBT.
     */
    protected fun <T> encodeToNbtTagUnsafe(serializer: SerializationStrategy<T>, value: T): NbtNamed<NbtTag> {
        lateinit var result: NbtNamed<NbtTag>
        val context = SerializationNbtContext()
        val writer = TreeNbtWriter { result = it }
        val encoder = NbtWriterEncoder(this, context, writer)

        encoder.encodeSerializableValue(serializer, value)
        return result
    }

    /**
     * Deserializes the given [tag] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given NBT tag is not a valid NBT input for the type [T].
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
     */
    protected fun <T> decodeFromNbtTagUnsafe(deserializer: DeserializationStrategy<T>, tag: NbtNamed<NbtTag>): T {
        val context = SerializationNbtContext()
        val reader = TreeNbtReader(tag)
        val decoder = NbtReaderDecoder(this, context, reader)

        return decoder.decodeSerializableValue(deserializer)
    }
}
