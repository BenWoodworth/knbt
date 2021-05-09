package net.benwoodworth.knbt.internal

import okio.Sink
import okio.Source

internal actual fun Source.toGzipSource(): Source =
    throw UnsupportedOperationException("Gzip compression is not currently supported for Kotlin/Native")

internal actual fun Sink.toGzipSink(): Sink =
    throw UnsupportedOperationException("Gzip compression is not currently supported for Kotlin/Native")

internal actual fun Source.toZlibSource(): Source =
    throw UnsupportedOperationException("Zlib compression is not currently supported for Kotlin/Native")

internal actual fun Sink.toZlibSink(): Sink =
    throw UnsupportedOperationException("Zlib compression is not currently supported for Kotlin/Native")
