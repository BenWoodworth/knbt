package net.benwoodworth.knbt

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.Buffer

public open class Nbt internal constructor(
    override val configuration: NbtConfiguration,
    serializersModule: SerializersModule,
) : NbtFormat(
    configuration.variant.toString(),
    configuration,
    serializersModule,
    configuration.variant.capabilities
), BinaryFormat, @Suppress("DEPRECATION") NbtDeprecations {
    @OptIn(OkioApi::class)
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        Buffer().apply { encodeToBufferedSink(serializer, value, this) }.readByteArray()

    @OptIn(OkioApi::class)
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        decodeFromBufferedSource(deserializer, Buffer().apply { write(bytes) })
}

internal object DefaultNbt : Nbt(
    configuration = NbtConfiguration(
        variant = NbtVariant.Java, // Will be ignored by NbtBuilder
        compression = NbtCompression.None, // Will be ignored by NbtBuilder
        compressionLevel = null,
        encodeDefaults = NbtFormat.configuration.encodeDefaults,
        ignoreUnknownKeys = NbtFormat.configuration.ignoreUnknownKeys,
    ),
    serializersModule = EmptySerializersModule(),
)

/**
 * Creates an instance of [Nbt] configured from the optionally given [Nbt instance][from]
 * and adjusted with [builderAction].
 *
 * [variant][NbtBuilder.variant] and [compression][NbtBuilder.compression] are required.
 */
public fun Nbt(from: Nbt = DefaultNbt, builderAction: NbtBuilder.() -> Unit): Nbt {
    val builder = NbtBuilder(from)
    builder.builderAction()
    return builder.build()
}
