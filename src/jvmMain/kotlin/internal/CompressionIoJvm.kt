package net.benwoodworth.knbt.internal

import okio.*
import java.util.zip.Deflater
import java.util.zip.Inflater

internal actual fun Source.asGzipSource(): Source = this.gzip()

internal actual fun Sink.asGzipSink(level: Int): Sink = this.gzip().apply { deflater.setLevel(level) }

internal actual fun Source.asZlibSource(): Source = inflate(Inflater())

internal actual fun Sink.asZlibSink(level: Int): Sink = deflate(Deflater(level, false))
