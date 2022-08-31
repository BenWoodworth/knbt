package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public abstract class NbtCompression private constructor() {
    internal abstract fun decompress(source: BufferedSource): BufferedSource
    internal abstract fun compress(sink: BufferedSink, level: Int?): BufferedSink

    public companion object;

    public object None : NbtCompression() {
        override fun decompress(source: BufferedSource): BufferedSource = source
        override fun compress(sink: BufferedSink, level: Int?): BufferedSink = sink

        override fun toString(): String = "None"
    }

    public object Gzip : NbtCompression() {
        override fun decompress(source: BufferedSource): BufferedSource = source.asGzipSource()
        override fun compress(sink: BufferedSink, level: Int?): BufferedSink = sink.asGzipSink(level ?: -1)

        override fun toString(): String = "Gzip"
    }

    public object Zlib : NbtCompression() {
        override fun decompress(source: BufferedSource): BufferedSource = source.asZlibSource()
        override fun compress(sink: BufferedSink, level: Int?): BufferedSink = sink.asZlibSink(level ?: -1)

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
@ExperimentalNbtApi
public fun NbtCompression.Companion.detect(source: BufferedSource): NbtCompression =
    detect(source.peek().readByte())

/**
 * Peek in the [byteArray] and detect what [NbtCompression] is used.
 *
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
public fun NbtCompression.Companion.detect(byteArray: ByteArray): NbtCompression =
    detect(byteArray[0])
