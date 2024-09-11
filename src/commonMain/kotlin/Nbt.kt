package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.internal.NbtCapabilities

private val nbtCapabilities = NbtCapabilities(
    namedRoot = true,
    definiteLengthEncoding = true,
)

public open class Nbt internal constructor(
    override val configuration: NbtConfiguration,
    override val serializersModule: SerializersModule,
) : NbtFormat() {
    override val name: String get() = "NbtTag"
    override val capabilities: NbtCapabilities get() = nbtCapabilities

    public companion object Default : Nbt(
        configuration = NbtConfiguration(
            encodeDefaults = NbtFormatDefaults.encodeDefaults,
            ignoreUnknownKeys = NbtFormatDefaults.ignoreUnknownKeys,
        ),
        serializersModule = NbtFormatDefaults.serializersModule,
    )

    /**
     * Serializes the given [value] into an equivalent named [NbtTag] using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to NBT.
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtNamed<NbtTag> =
        encodeToNbtTagUnsafe(serializer, value)

    /**
     * Deserializes the given named [tag] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given NBT tag is not a valid NBT input for the type [T].
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtNamed<NbtTag>): T =
        decodeFromNbtTagUnsafe(deserializer, tag)
}

/**
 * Creates an instance of [Nbt] configured from the optionally given [Nbt instance][from]
 * and adjusted with [builderAction].
 */
public fun Nbt(
    from: Nbt? = null,
    builderAction: NbtBuilder.() -> Unit,
): Nbt {
    val builder = NbtBuilder(from)
    builder.builderAction()
    return builder.build()
}

/**
 * Serializes the given [value] into an equivalent named [NbtTag] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to NBT.
 */
public inline fun <reified T> Nbt.encodeToNbtTag(value: T): NbtNamed<NbtTag> =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Deserializes the given named [tag] into a value of type [T] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given NBT tag is not a valid NBT input for the type [T].
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
 */
public inline fun <reified T> Nbt.decodeFromNbtTag(tag: NbtNamed<NbtTag>): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)
