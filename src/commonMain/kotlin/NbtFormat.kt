package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.*

public open class NbtFormat internal constructor(
    internal val name: String,
    public open val configuration: NbtFormatConfiguration,
    final override val serializersModule: SerializersModule,
    internal val capabilities: NbtCapabilities
) : SerialFormat {
    public companion object Default : NbtFormat(
        "NbtTag",
        configuration = NbtFormatConfiguration(
            encodeDefaults = false,
            ignoreUnknownKeys = false,
        ),
        serializersModule = EmptySerializersModule(),
        capabilities = NbtCapabilities(namedRoot = true, definiteLengthEncoding = true)
    )

    /**
     * Serializes the given [value] into an equivalent [NbtTag] using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to NBT
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtTag {
        lateinit var result: NbtNamed<NbtTag>
        val context = SerializationNbtContext()
        val writer = TreeNbtWriter { result = it }
        val encoder = NbtWriterEncoder(this, context, writer)

        encoder.encodeSerializableValue(serializer, value)
        return result.value
    }

    /**
     * Deserializes the given [tag] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given NBT tag is not a valid NBT input for the type [T]
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T]
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag): T {
        val context = SerializationNbtContext()
        val reader = TreeNbtReader(NbtNamed(deserializer.descriptor.nbtName ?: "", tag)) // TODO Propagate NbtNamed
        val decoder = NbtReaderDecoder(this, context, reader)

        return decoder.decodeSerializableValue(deserializer)
    }
}

/**
 * Creates an instance of [NbtFormat] configured from the optionally given [NbtFormat instance][from]
 * and adjusted with [builderAction].
 */
public fun NbtFormat(
    from: NbtFormat = NbtFormat.Default,
    builderAction: NbtFormatBuilder.() -> Unit,
): NbtFormat {
    val builder = NbtFormatBuilder(from)
    builder.builderAction()
    return builder.build()
}

public open class NbtFormatBuilder internal constructor(nbt: NbtFormat) {
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
     * Module with contextual and polymorphic serializers to be used in the resulting [NbtFormat] instance.
     */
    public var serializersModule: SerializersModule = nbt.serializersModule

    internal open fun build(): NbtFormat {
        return NbtFormat(
            NbtFormat.name,
            configuration = NbtFormatConfiguration(
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
            ),
            serializersModule = serializersModule,
            capabilities = NbtFormat.capabilities,
        )
    }
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
