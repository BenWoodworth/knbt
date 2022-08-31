package net.benwoodworth.knbt.internal

import okio.BufferedSink
import okio.BufferedSource

internal expect fun BufferedSource.asGzipSource(): BufferedSource
internal expect fun BufferedSink.asGzipSink(level: Int): BufferedSink

internal expect fun BufferedSource.asZlibSource(): BufferedSource
internal expect fun BufferedSink.asZlibSink(level: Int): BufferedSink
