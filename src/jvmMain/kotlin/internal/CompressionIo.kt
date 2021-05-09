@file:JvmName("CompressionIoJvmKt")

package net.benwoodworth.knbt.internal

import okio.*
import java.util.zip.GZIPInputStream

internal actual fun Source.toGzipSource(): Source = this.gzip()

internal actual fun Sink.toGzipSink(): Sink = this.gzip()

internal actual fun Source.toZlibSource(): Source = GZIPInputStream(this.buffer().inputStream()).source()

internal actual fun Sink.toZlibSink(): Sink =
    throw UnsupportedOperationException("Zlib decompression is not currently supported for Kotlin/JVM")
