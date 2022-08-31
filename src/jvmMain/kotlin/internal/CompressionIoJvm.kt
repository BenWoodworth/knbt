package net.benwoodworth.knbt.internal

import okio.*
import java.util.zip.Deflater
import java.util.zip.Inflater

internal actual fun BufferedSource.asGzipSource(): BufferedSource =
    this.gzip().buffer()

internal actual fun BufferedSink.asGzipSink(level: Int): BufferedSink =
    this.gzip().apply { deflater.setLevel(level) }.buffer()

internal actual fun BufferedSource.asZlibSource(): BufferedSource =
    inflate(Inflater()).buffer()

internal actual fun BufferedSink.asZlibSink(level: Int): BufferedSink =
    deflate(Deflater(level, false)).buffer()
