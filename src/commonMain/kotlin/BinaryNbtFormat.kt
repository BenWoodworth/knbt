package net.benwoodworth.knbt

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.NbtCapabilities
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.Buffer

public class BinaryNbtFormat internal constructor(
    override val configuration: BinaryNbtFormatConfiguration,
    override val serializersModule: SerializersModule,
) : NbtFormat(), BinaryFormat, @Suppress("DEPRECATION") NbtDeprecations {
    override val name: String get() = configuration.variant.toString()
    override val capabilities: NbtCapabilities get() = configuration.variant.capabilities

    @OptIn(OkioApi::class)
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        Buffer().apply { encodeToBufferedSink(serializer, value, this) }.readByteArray()

    @OptIn(OkioApi::class)
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        decodeFromBufferedSource(deserializer, Buffer().apply { write(bytes) })
}

/**
 * Creates an instance of [BinaryNbtFormat] configured from the optionally given [BinaryNbtFormat instance][from]
 * and adjusted with [builderAction].
 *
 * [variant][BinaryNbtFormatBuilder.variant] and [compression][BinaryNbtFormatBuilder.compression] are required.
 */
public fun BinaryNbtFormat(
    from: BinaryNbtFormat? = null,
    builderAction: BinaryNbtFormatBuilder.() -> Unit
): BinaryNbtFormat {
    val builder = BinaryNbtFormatBuilder(from)
    builder.builderAction()
    return builder.build()
}
