package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.internal.*
import net.benwoodworth.knbt.internal.BedrockNbtReader
import net.benwoodworth.knbt.internal.NbtCapabilities
import net.benwoodworth.knbt.internal.NbtContext
import okio.BufferedSink
import okio.BufferedSource

private val bedrockNbtCapabilities = NbtCapabilities(
    namedRoot = true,
    definiteLengthEncoding = true,
    rootTagTypes = NbtTagTypeSet(listOf(NbtTagType.TAG_List, NbtTagType.TAG_Compound)),
)

public class BedrockNbt internal constructor(
    override val configuration: BedrockNbtConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryNbtFormat() {
    override val name: String get() = "Bedrock"
    override val capabilities: NbtCapabilities get() = bedrockNbtCapabilities

    override fun getNbtReader(context: NbtContext, source: BufferedSource): BinaryNbtReader =
        BedrockNbtReader(context, source)

    override fun getNbtWriter(context: NbtContext, sink: BufferedSink): BinaryNbtWriter =
        BedrockNbtWriter(context, sink)

    /**
     * Serializes the given [value] into an equivalent named [NbtTag] using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to Bedrock NBT.
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtNamed<NbtTag> =
        encodeToNbtTagUnsafe(serializer, value)

    /**
     * Deserializes the given named [tag] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given NBT tag is not a valid Bedrock NBT input for the type [T].
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtNamed<NbtTag>): T =
        decodeFromNbtTagUnsafe(deserializer, tag)
}

/**
 * Creates an instance of [BedrockNbt] configured from the optionally given [BedrockNbt instance][from]
 * and adjusted with [builderAction].
 *
 * [compression][BedrockNbtBuilder.compression] is required.
 */
public fun BedrockNbt(
    from: BedrockNbt? = null,
    builderAction: BedrockNbtBuilder.() -> Unit
): BedrockNbt {
    val builder = BedrockNbtBuilder(from)
    builder.builderAction()
    return builder.build()
}

/**
 * Serializes the given [value] into an equivalent named [NbtTag] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to Bedrock NBT.
 */
public inline fun <reified T> BedrockNbt.encodeToNbtTag(value: T): NbtNamed<NbtTag> =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Deserializes the given named [tag] into a value of type [T] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given NBT tag is not a valid Bedrock NBT input for the type [T].
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
 */
public inline fun <reified T> BedrockNbt.decodeFromNbtTag(tag: NbtNamed<NbtTag>): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)
