package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

private val bedrockNetworkNbtCapabilities = NbtCapabilities(
    namedRoot = false,
    definiteLengthEncoding = true,
    rootTagTypes = NbtTypeSet(listOf(NbtType.TAG_List, NbtType.TAG_Compound)),
)

public class BedrockNetworkNbt internal constructor(
    override val configuration: BedrockNetworkNbtConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryNbtFormat() {
    override val name: String get() = "BedrockNetwork(v${configuration.protocolVersion})"
    override val capabilities: NbtCapabilities get() = bedrockNetworkNbtCapabilities

    override fun getNbtReader(context: NbtContext, source: BufferedSource): BinaryNbtReader =
        BedrockNbtReader(context, source)

    override fun getNbtWriter(context: NbtContext, sink: BufferedSink): BinaryNbtWriter =
        BedrockNbtWriter(context, sink)

    /**
     * Serializes the given [value] into an equivalent [NbtTag] using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to Bedrock network NBT.
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtTag =
        encodeToNbtTagUnsafe(serializer, value).value

    /**
     * Deserializes the given [tag] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given NBT tag is not a valid Bedrock network NBT input for the type [T].
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag): T =
        decodeFromNbtTagUnsafe(deserializer, NbtNamed("", tag))
}

/**
 * Creates an instance of [BedrockNetworkNbt] configured from the optionally given [BedrockNetworkNbt instance][from]
 * and adjusted with [builderAction].
 *
 * [protocolVersion][JavaNetworkNbtBuilder.protocolVersion] and [compression][BedrockNetworkNbtBuilder.compression] are
 * required.
 */
public fun BedrockNetworkNbt(
    from: BedrockNetworkNbt? = null,
    builderAction: BedrockNetworkNbtBuilder.() -> Unit
): BedrockNetworkNbt {
    val builder = BedrockNetworkNbtBuilder(from)
    builder.builderAction()
    return builder.build()
}

/**
 * Serializes the given [value] into an equivalent [NbtTag] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to Bedrock network NBT.
 */
public inline fun <reified T> BedrockNetworkNbt.encodeToNbtTag(value: T): NbtTag =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Deserializes the given [tag] into a value of type [T] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given NBT tag is not a valid Bedrock network NBT input for the type [T].
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
 */
public inline fun <reified T> BedrockNetworkNbt.decodeFromNbtTag(tag: NbtTag): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)
