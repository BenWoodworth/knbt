package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.asGzipSink
import net.benwoodworth.knbt.internal.asGzipSource
import net.benwoodworth.knbt.internal.asZlibSink
import net.benwoodworth.knbt.internal.asZlibSource
import okio.BufferedSource
import okio.Sink
import okio.Source

public abstract class NbtCompression private constructor() {
    internal abstract fun getUncompressedSource(source: Source): Source
    internal abstract fun getCompressingSink(sink: Sink, level: Int?): Sink

    public companion object {
        @Suppress("DEPRECATION", "DeprecatedCallableAddReplaceWith", "UNUSED_PARAMETER")
        @Deprecated("Compression level moved to NbtConfigureation.compressionLevel", level = DeprecationLevel.ERROR)
        public fun Gzip(from: Gzip = Gzip, builderAction: Gzip.Builder.() -> Unit): Gzip =
            throw UnsupportedOperationException("Compression level moved to NbtConfigureation.compressionLevel")

        @Suppress("DEPRECATION", "DeprecatedCallableAddReplaceWith", "UNUSED_PARAMETER")
        @Deprecated("Compression level moved to NbtConfigureation.compressionLevel", level = DeprecationLevel.ERROR)
        public inline fun Zlib(from: Zlib = Zlib, builderAction: Gzip.Builder.() -> Unit): Zlib =
            throw UnsupportedOperationException("Compression level moved to NbtConfigureation.compressionLevel")
    }

    public object None : NbtCompression() {
        override fun getUncompressedSource(source: Source): Source = source
        override fun getCompressingSink(sink: Sink, level: Int?): Sink = sink

        override fun toString(): String = "None"
    }

    public object Gzip : NbtCompression() {
        override fun getUncompressedSource(source: Source): Source = source.asGzipSource()
        override fun getCompressingSink(sink: Sink, level: Int?): Sink = sink.asGzipSink(level ?: -1)

        override fun toString(): String = "Gzip"

        @Deprecated("Compression level moved to NbtConfigureation.compressionLevel")
        public class Builder {
            @Deprecated("Compression level moved to NbtConfiguration.compressionLevel", level = DeprecationLevel.ERROR)
            public var level: Int? = null
        }
    }

    public object Zlib : NbtCompression() {
        override fun getUncompressedSource(source: Source): Source = source.asZlibSource()
        override fun getCompressingSink(sink: Sink, level: Int?): Sink = sink.asZlibSink(level ?: -1)

        override fun toString(): String = "Zlib"

        @Deprecated("Compression level moved to NbtConfigureation.compressionLevel")
        public class Builder {
            @Deprecated("Compression level moved to NbtConfiguration.compressionLevel", level = DeprecationLevel.ERROR)
            public var level: Int? = null
        }
    }
}

/**
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
internal fun NbtCompression.Companion.detect(firstByte: Byte): NbtCompression =
    when (firstByte) {
        // NBT Tag type IDs
        in 0..12 -> NbtCompression.None

        // Gzip header: 0x1F8B
        0x1F.toByte() -> NbtCompression.Gzip

        // Zlib headers: 0x7801, 0x789C, and 0x78DA
        0x78.toByte() -> NbtCompression.Zlib

        else -> {
            val byteStr = firstByte.toUByte().toString(16).uppercase().padStart(2, '0')
            throw NbtDecodingException("Unable to detect NbtCompression. Unexpected first byte: 0x$byteStr")
        }
    }

/**
 * Peek in the [source] and detect what [NbtCompression] is used.
 *
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
@OkioApi
public fun NbtCompression.Companion.detect(source: BufferedSource): NbtCompression =
    detect(source.peek().readByte())

/**
 * Peek in the [byteArray] and detect what [NbtCompression] is used.
 *
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
public fun NbtCompression.Companion.detect(byteArray: ByteArray): NbtCompression =
    detect(byteArray[0])
