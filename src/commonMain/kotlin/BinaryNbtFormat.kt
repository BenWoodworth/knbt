package net.benwoodworth.knbt

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import net.benwoodworth.knbt.internal.BinaryNbtReader
import net.benwoodworth.knbt.internal.BinaryNbtWriter
import net.benwoodworth.knbt.internal.NbtContext
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource

public abstract class BinaryNbtFormat internal constructor() :
    NbtFormat(), BinaryFormat, @Suppress("DEPRECATION") NbtDeprecations {

    abstract override val configuration: BinaryNbtFormatConfiguration

    internal abstract fun getNbtReader(context: NbtContext, source: BufferedSource): BinaryNbtReader
    internal abstract fun getNbtWriter(context: NbtContext, sink: BufferedSink): BinaryNbtWriter

    @OptIn(OkioApi::class)
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        Buffer().apply { encodeToBufferedSink(serializer, value, this) }.readByteArray()

    @OptIn(OkioApi::class)
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        decodeFromBufferedSource(deserializer, Buffer().apply { write(bytes) })
}
