package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.asGzipSink
import net.benwoodworth.knbt.internal.asGzipSource
import net.benwoodworth.knbt.internal.asZlibSink
import net.benwoodworth.knbt.internal.asZlibSource
import okio.Sink
import okio.Source

public sealed class NbtCompression {
    internal abstract fun getUncompressedSource(source: Source): Source
    internal abstract fun getCompressingSink(sink: Sink): Sink

    public companion object {
        @Deprecated("Use null instead.", ReplaceWith("null"))
        public val None: Nothing? = null
    }

    public object Gzip : NbtCompression() {
        override fun getUncompressedSource(source: Source): Source = source.asGzipSource()
        override fun getCompressingSink(sink: Sink): Sink = sink.asGzipSink()

        override fun toString(): String = "Gzip"
    }

    public object Zlib : NbtCompression() {
        override fun getUncompressedSource(source: Source): Source = source.asZlibSource()
        override fun getCompressingSink(sink: Sink): Sink = sink.asZlibSink()

        override fun toString(): String = "Zlib"
    }
}
