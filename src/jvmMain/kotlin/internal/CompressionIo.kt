@file:JvmName("CompressionIoJvmKt")

package net.benwoodworth.knbt.internal

import okio.*
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream
import java.util.zip.Inflater

internal actual fun Source.toGzipSource(): Source = this.gzip()

internal actual fun Sink.toGzipSink(): Sink = this.gzip()

internal actual fun Source.toZlibSource(): Source = inflate(Inflater())

internal actual fun Sink.toZlibSink(): Sink = deflate(Deflater(9, false))
