package net.benwoodworth.knbt

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import net.benwoodworth.knbt.internal.NbtCapabilities
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.Buffer

public abstract class BinaryNbtFormat internal constructor() :
    NbtFormat(), BinaryFormat, @Suppress("DEPRECATION") NbtDeprecations {

    abstract override val configuration: BinaryNbtFormatConfiguration
    internal abstract val variant: NbtVariant

    final override val name: String get() = variant.toString()
    final override val capabilities: NbtCapabilities get() = variant.capabilities

    @OptIn(OkioApi::class)
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        Buffer().apply { encodeToBufferedSink(serializer, value, this) }.readByteArray()

    @OptIn(OkioApi::class)
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        decodeFromBufferedSource(deserializer, Buffer().apply { write(bytes) })
}
