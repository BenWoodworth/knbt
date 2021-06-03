package net.benwoodworth.knbt.internal

import okio.Sink
import okio.Source

internal expect fun Source.asGzipSource(): Source
internal expect fun Sink.asGzipSink(level: Int): Sink

internal expect fun Source.asZlibSource(): Source
internal expect fun Sink.asZlibSink(level: Int): Sink
