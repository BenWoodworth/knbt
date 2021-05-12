package net.benwoodworth.knbt.internal

import okio.*
import java.util.zip.Deflater
import java.util.zip.GZIPInputStream
import java.util.zip.Inflater

internal actual fun Source.asGzipSource(): Source = this.gzip()

internal actual fun Sink.asGzipSink(): Sink = this.gzip()

internal actual fun Source.asZlibSource(): Source = inflate(Inflater())

internal actual fun Sink.asZlibSink(): Sink = deflate(Deflater(9, false))
