package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

private val javaNetworkNbtCapabilities = NbtCapabilities(
    namedRoot = false,
    definiteLengthEncoding = true,
)

public class JavaNetworkNbt internal constructor(
    override val configuration: JavaNetworkNbtConfiguration,
    override val serializersModule: SerializersModule,
    private val protocolType: ProtocolType,
) : BinaryNbtFormat() {
    override val name: String get() = "JavaNetwork(v${configuration.protocolVersion})"
    override val capabilities: NbtCapabilities get() = javaNetworkNbtCapabilities

    internal enum class ProtocolType { EmptyNamedRoot, UnnamedRoot }

    override fun getNbtReader(context: NbtContext, source: BufferedSource): BinaryNbtReader =
        when (protocolType) {
            ProtocolType.EmptyNamedRoot -> JavaNetworkNbtReader.EmptyNamedRoot(context, source)
            ProtocolType.UnnamedRoot -> JavaNetworkNbtReader.UnnamedRoot(context, source)
        }

    override fun getNbtWriter(context: NbtContext, sink: BufferedSink): BinaryNbtWriter =
        when (protocolType) {
            ProtocolType.EmptyNamedRoot -> JavaNetworkNbtWriter.EmptyNamedRoot(context, sink)
            ProtocolType.UnnamedRoot -> JavaNetworkNbtWriter.UnnamedRoot(context, sink)
        }

    /**
     * Serializes the given [value] into an equivalent [NbtTag] using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to Java network NBT.
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtTag =
        encodeToNbtTagUnsafe(serializer, value).value

    /**
     * Deserializes the given [tag] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given NBT tag is not a valid Java network NBT input for the type [T].
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtTag): T =
        decodeFromNbtTagUnsafe(deserializer, NbtNamed("", tag))
}

/**
 * Creates an instance of [JavaNetworkNbt] configured from the optionally given [JavaNetworkNbt instance][from]
 * and adjusted with [builderAction].
 *
 * [protocolVersion][JavaNetworkNbtBuilder.protocolVersion] and [compression][JavaNetworkNbtBuilder.compression] are
 * required.
 */
public fun JavaNetworkNbt(
    from: JavaNetworkNbt? = null,
    builderAction: JavaNetworkNbtBuilder.() -> Unit
): JavaNetworkNbt {
    val builder = JavaNetworkNbtBuilder(from)
    builder.builderAction()
    return builder.build()
}

/**
 * Serializes the given [value] into an equivalent [NbtTag] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to Java network NBT.
 */
public inline fun <reified T> JavaNetworkNbt.encodeToNbtTag(value: T): NbtTag =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Deserializes the given [tag] into a value of type [T] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given NBT tag is not a valid Java network NBT input for the type [T].
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
 */
public inline fun <reified T> JavaNetworkNbt.decodeFromNbtTag(tag: NbtTag): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)
