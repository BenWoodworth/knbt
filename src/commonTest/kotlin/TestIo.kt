package net.benwoodworth.knbt

import okio.Buffer
import okio.Sink
import okio.Source

class TestSource(private val source: Source) : Source by source {
    var isClosed: Boolean = false
        private set

    var readPastEnd: Boolean = false
        private set

    override fun close() {
        isClosed = true
        source.close()
    }

    override fun read(sink: Buffer, byteCount: Long): Long {
        return source.read(sink, byteCount)
            .also { if (it == -1L) readPastEnd = true }
    }
}

class TestSink(private val sink: Sink) : Sink by sink {
    var isClosed: Boolean = false
        private set

    override fun close() {
        isClosed = true
        sink.close()
    }
}
