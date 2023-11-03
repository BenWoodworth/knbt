package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.Sink
import okio.Source

public abstract class NbtCompression private constructor() {
    internal abstract fun decompress(source: Source): Source
    internal abstract fun compress(sink: Sink): Sink

    public companion object;

    public object None : NbtCompression() {
        override fun decompress(source: Source): Source = source
        override fun compress(sink: Sink): Sink = sink

        override fun equals(other: Any?): Boolean =
            this === other || other is None

        override fun hashCode(): Int = 0

        override fun toString(): String = "None"
    }

    /**
     * Configures gzip compression with the given compression [level]:
     * - `0` gives no compression at all
     * - `1` gives the best speed
     * - `9` gives the best compression.
     * - `null` requests a default compromise between speed and compression.
     */
    public class Gzip(
        public val level: Int? = null
    ) : NbtCompression() {
        init {
            require(level == null || level in 0..9) { "Compression level must be in 0..9 or null, but is $level" }
        }

        override fun decompress(source: Source): Source = source.asGzipSource()
        override fun compress(sink: Sink): Sink = sink.asGzipSink(level ?: -1)

        override fun equals(other: Any?): Boolean =
            this === other || other is Gzip && level == other.level

        override fun hashCode(): Int = (level ?: -1) + 2

        override fun toString(): String = "Gzip(level = ${level ?: "default"})"

        @Deprecated(
            "Now constructed with NbtCompression.Gzip(level = ...)",
            ReplaceWith(
                "NbtCompression.Gzip()",
                "net.benwoodworth.knbt.NbtCompression"
            ),
            DeprecationLevel.ERROR
        )
        public companion object
    }

    /**
     * Configures zlib compression with the given compression [level]:
     * - `0` gives no compression at all
     * - `1` gives the best speed
     * - `9` gives the best compression.
     * - `null` requests a default compromise between speed and compression.
     */
    public class Zlib(
        public val level: Int? = null
    ) : NbtCompression() {
        init {
            require(level == null || level in 0..9) { "Compression level must be in 0..9 or null, but is $level" }
        }

        override fun decompress(source: Source): Source = source.asZlibSource()
        override fun compress(sink: Sink): Sink = sink.asZlibSink(level ?: -1)

        override fun equals(other: Any?): Boolean =
            this === other || other is Zlib && level == other.level

        override fun hashCode(): Int = (level ?: -1) + 13

        override fun toString(): String = "Zlib(level = ${level ?: "default"})"

        @Deprecated(
            "Now constructed with NbtCompression.Zlib(level = ...)",
            ReplaceWith(
                "NbtCompression.Zlib()",
                "net.benwoodworth.knbt.NbtCompression"
            ),
            DeprecationLevel.ERROR
        )
        public companion object
    }
}

/**
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
internal fun NbtCompression.Companion.detect(firstByte: Byte, secondByteOrZero: Byte): NbtCompression =
    when (firstByte) {
        // NBT Tag type IDs
        in 0..12 -> NbtCompression.None

        // Gzip header: 0x1F8B
        0x1F.toByte() -> NbtCompression.Gzip()

        // Zlib header: 0x78. Detect level in second byte with FLEVEL: https://www.rfc-editor.org/rfc/rfc1950
        0x78.toByte() -> when (secondByteOrZero.toInt() and 0b11000000 shr 6) {
            0b00 -> NbtCompression.Zlib(1)
            0b01 -> NbtCompression.Zlib(3)
            0b10 -> NbtCompression.Zlib(null)
            else -> NbtCompression.Zlib(9)
        }

        else -> throw NbtDecodingException(
            "Unable to detect NbtCompression. Unexpected first byte: 0x${firstByte.toHex()}"
        )
    }

/**
 * Peek in the [byteArray] and detect what [NbtCompression] is used.
 *
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
public fun NbtCompression.Companion.detect(byteArray: ByteArray): NbtCompression =
    detect(byteArray[0], byteArray.getOrElse(1) { 0 })
