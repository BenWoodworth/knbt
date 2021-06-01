package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.ExperimentalNbtApi
import net.benwoodworth.knbt.NbtCompression
import net.benwoodworth.knbt.NbtDecodingException
import okio.BufferedSource
import okio.Sink
import okio.Source

@OptIn(ExperimentalNbtApi::class)
internal fun BufferedSource.peekNbtCompression(): NbtCompression? =
    when (val byte = peek().readByte()) {
        // NBT Tag type IDs
        in 0..12 -> null

        // Gzip header: 0x1F8B
        0x1F.toByte() -> NbtCompression.Gzip

        // Zlib headers: 0x7801, 0x789C, and 0x78DA
        0x78.toByte() -> NbtCompression.Zlib

        else -> {
            val byteStr = byte.toUByte().toString(16).uppercase().padStart(2, '0')
            throw NbtDecodingException("Unexpected first byte: 0x$byteStr")
        }
    }

internal expect fun Source.asGzipSource(): Source
internal expect fun Sink.asGzipSink(level: Int): Sink

internal expect fun Source.asZlibSource(): Source
internal expect fun Sink.asZlibSink(level: Int): Sink
