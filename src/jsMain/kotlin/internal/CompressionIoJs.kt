package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.pako.Deflate
import net.benwoodworth.knbt.internal.pako.Inflate
import net.benwoodworth.knbt.internal.pako.ZFlushMode
import net.benwoodworth.knbt.internal.pako.ZLevel
import okio.*
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

internal actual fun BufferedSource.asGzipSource(): BufferedSource =
    ZlibSource(this).buffer()

internal actual fun BufferedSink.asGzipSink(level: Int): BufferedSink =
    ZlibSink(this, true, level).buffer()

internal actual fun BufferedSource.asZlibSource(): BufferedSource =
    ZlibSource(this).buffer()

internal actual fun BufferedSink.asZlibSink(level: Int): BufferedSink =
    ZlibSink(this, false, level).buffer()

private class ZlibSource(private val source: BufferedSource) : Source by source {
    private val inbuf = Uint8Array(1)
    private val outbuf = Uint8ArrayBuffer()
    private var inflateDone = false

    private val inflate = Inflate().apply {
        onData = { data -> outbuf.reset(data) }
        onEnd = { inflateDone = true }
    }

    override fun read(sink: Buffer, byteCount: Long): Long {
        while (!inflateDone && outbuf.exhausted()) {
            inbuf[0] = source.readByte()
            inflate.push(inbuf, ZFlushMode.SYNC_FLUSH)
        }

        return outbuf.read(sink, byteCount)
    }

    private class Uint8ArrayBuffer {
        private lateinit var buffer: Uint8Array
        private var position: Int = 0
        private var exhausted = true

        fun reset(buffer: Uint8Array) {
            if (!exhausted) error("Buffer not exhausted")

            this.buffer = buffer
            position = 0
            exhausted = buffer.byteLength == 0
        }

        fun read(sink: Buffer, byteCount: Long): Long {
            if (exhausted) return -1

            val remaining = buffer.byteLength - position
            val readBytes = minOf(remaining.toLong(), byteCount)

            repeat(readBytes.toInt()) {
                sink.writeByte(buffer[position++].toInt())
            }

            if (position == buffer.length) exhausted = true
            return readBytes
        }

        fun exhausted(): Boolean = exhausted
    }
}

private class ZlibSink(private val sink: BufferedSink, gzip: Boolean, level: Int) : Sink by sink {
    private val inbuf = Uint8Array(1)

    private val deflate = Deflate(
        level = ZLevel(level),
        windowBits = 15 + (if (gzip) 16 else 0),
    ).apply {
        onData = { data ->
            for (i in 0 until data.byteLength) {
                sink.writeByte(data[i].toInt())
            }
        }
    }

    override fun write(source: Buffer, byteCount: Long) {
        for (i in 0 until byteCount) {
            inbuf[0] = source.readByte()
            deflate.push(inbuf)
        }
    }

    override fun close() {
        deflate.push(Uint8Array(0), ZFlushMode.FINISH)
        sink.close()
    }
}
