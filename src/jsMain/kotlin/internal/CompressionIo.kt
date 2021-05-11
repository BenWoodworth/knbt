package net.benwoodworth.knbt.internal

import okio.Sink
import okio.Source

internal actual fun Source.asGzipSource(): Source =
    throw UnsupportedOperationException("Gzip compression is not currently supported for Kotlin/JS")

internal actual fun Sink.asGzipSink(): Sink =
    throw UnsupportedOperationException("Gzip compression is not currently supported for Kotlin/JS")

internal actual fun Source.asZlibSource(): Source =
    throw UnsupportedOperationException("Zlib compression is not currently supported for Kotlin/JS")

internal actual fun Sink.asZlibSink(): Sink =
    throw UnsupportedOperationException("Zlib compression is not currently supported for Kotlin/JS")
