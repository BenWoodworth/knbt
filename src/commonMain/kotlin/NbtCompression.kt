package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.asGzipSink
import net.benwoodworth.knbt.internal.asGzipSource
import net.benwoodworth.knbt.internal.asZlibSink
import net.benwoodworth.knbt.internal.asZlibSource
import okio.BufferedSource
import okio.Sink
import okio.Source

public sealed class NbtCompression {
    internal abstract fun getUncompressedSource(source: Source): Source
    internal abstract fun getCompressingSink(sink: Sink): Sink

    public companion object {
        @Deprecated("Use null instead.", ReplaceWith("null"))
        public inline val None: NbtCompression?
            get() = null

        public inline fun Gzip(from: Gzip = Gzip.Default, builderAction: Gzip.Builder.() -> Unit): Gzip =
            Gzip.Builder(from).apply(builderAction).build()

        public inline fun Zlib(from: Zlib = Zlib.Default, builderAction: Zlib.Builder.() -> Unit): Zlib =
            Zlib.Builder(from).apply(builderAction).build()
    }

    public open class Gzip private constructor(
        public val level: Int?,
    ) : NbtCompression() {
        override fun getUncompressedSource(source: Source): Source = source.asGzipSource()
        override fun getCompressingSink(sink: Sink): Sink = sink.asGzipSink(level ?: -1)

        override fun toString(): String = "Gzip(level = $level)"

        public companion object Default : Gzip(level = null)

        @NbtDslMarker
        public class Builder @PublishedApi internal constructor(gzip: Gzip) {
            /**
             * The compression level, in `0..9` or `null`.
             *
             * `0` gives no compression at all, `1` gives the best speed, and `9` gives the best compression.
             *
             * `null` by default, which requests a compromise between speed and compression.
             */
            public var level: Int? = gzip.level
                set(value) {
                    require(value == null || value in 0..9) { "Must be in 0..9 or null." }
                    field = value
                }

            @PublishedApi
            internal fun build(): Gzip = Gzip(
                level = level,
            )
        }
    }

    public open class Zlib private constructor(
        public val level: Int?,
    ) : NbtCompression() {
        override fun getUncompressedSource(source: Source): Source = source.asZlibSource()
        override fun getCompressingSink(sink: Sink): Sink = sink.asZlibSink(level ?: -1)

        override fun toString(): String = "Zlib(level = $level)"

        public companion object Default : Zlib(level = null)

        @NbtDslMarker
        public class Builder @PublishedApi internal constructor(zlib: Zlib) {
            /**
             * The compression level, in `0..9` or `null`.
             *
             * `0` gives no compression at all, `1` gives the best speed, and `9` gives the best compression.
             *
             * `null` by default, which requests a compromise between speed and compression.
             */
            public var level: Int? = zlib.level
                set(value) {
                    require(value == null || value in 0..9) { "Must be in 0..9 or null." }
                    field = value
                }

            @PublishedApi
            internal fun build(): Zlib = Zlib(
                level = level,
            )
        }
    }
}

/**
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
internal fun NbtCompression.Companion.detect(firstByte: Byte): NbtCompression? =
    when (firstByte) {
        // NBT Tag type IDs
        in 0..12 -> null

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
public fun NbtCompression.Companion.detect(source: BufferedSource): NbtCompression? =
    detect(source.peek().readByte())

/**
 * Peek in the [byteArray] and detect what [NbtCompression] is used.
 *
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
public fun NbtCompression.Companion.detect(byteArray: ByteArray): NbtCompression? =
    detect(byteArray[0])
