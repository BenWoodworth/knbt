package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSource
import okio.Sink
import okio.Source
import kotlin.jvm.JvmOverloads

public abstract class NbtCompression private constructor() {
    internal abstract fun decompress(source: Source): Source
    internal abstract fun compress(sink: Sink, level: Int?): Sink

    public companion object;

    public object None : NbtCompression() {
        override fun decompress(source: Source): Source = source
        override fun compress(sink: Sink, level: Int?): Sink = sink

        override fun toString(): String = "None"
    }

    public object Gzip : NbtCompression() {
        override fun decompress(source: Source): Source = source.asGzipSource()
        override fun compress(sink: Sink, level: Int?): Sink = sink.asGzipSink(level ?: -1)

        override fun toString(): String = "Gzip"
    }

    public object Zlib : NbtCompression() {
        override fun decompress(source: Source): Source = source.asZlibSource()
        override fun compress(sink: Sink, level: Int?): Sink = sink.asZlibSink(level ?: -1)

        override fun toString(): String = "Zlib"
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

        else -> throw NbtDecodingException(
            "Unable to detect NbtCompression. Unexpected first byte: 0x${firstByte.toHex()}"
        )
    }

/**
 * Peek in the [source] and detect what [NbtCompression] is used.
 *
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
@OkioApi
@Deprecated(
    "Moved to okio package",
    ReplaceWith(
        "this.detect(source)",
        "net.benwoodworth.knbt.okio.detect"
    ),
    DeprecationLevel.ERROR
)
@Suppress("UNUSED_PARAMETER") // The `deprecated` parameter lowers the overload precedence so the relocated function takes priority when replaced
public fun NbtCompression.Companion.detect(source: BufferedSource, deprecated: Nothing? = null): NbtCompression =
    detect(source.peek().readByte())

/**
 * Peek in the [byteArray] and detect what [NbtCompression] is used.
 *
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
public fun NbtCompression.Companion.detect(byteArray: ByteArray): NbtCompression =
    detect(byteArray[0])
