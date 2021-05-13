package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.pako.Inflate
import net.benwoodworth.knbt.internal.pako.ZFlushMode
import net.benwoodworth.knbt.internal.pako.ZStatus
import okio.*
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

internal actual fun Source.asGzipSource(): Source = ZlibSource(this.buffer())

internal actual fun Sink.asGzipSink(): Sink =
    throw UnsupportedOperationException("Gzip compression is not currently supported for Kotlin/JS")

internal actual fun Source.asZlibSource(): Source = ZlibSource(this.buffer())

internal actual fun Sink.asZlibSink(): Sink =
    throw UnsupportedOperationException("Zlib compression is not currently supported for Kotlin/JS")

private class ZlibSource(private val source: BufferedSource) : Source by source {
    private val inbuf = Uint8Array(1)
    private val outbuf = Uint8ArrayBuffer()
    private var inflateDone = false

    private val inflate = Inflate().apply {
        onData = ::onData
        onEnd = ::onEnd
    }

    override fun read(sink: Buffer, byteCount: Long): Long {
        while (!inflateDone && outbuf.exhausted()) {
            inbuf[0] = source.readByte()
            inflate.push(inbuf, ZFlushMode.BLOCK)
        }

        return outbuf.read(sink, byteCount)
    }

    private fun onData(data: Uint8Array): Unit =
        outbuf.reset(data)

    private fun onEnd(status: Int) {
        inflateDone = true
        if (status != ZStatus.OK.status) throw IOException("Bad inflate status: $status (${inflate.msg})")
    }
}

private class Uint8ArrayBuffer {
    private lateinit var buffer: Uint8Array
    private var position: Int = -1

    fun reset(buffer: Uint8Array) {
        if (!exhausted()) error("Buffer not exhausted")

        this.buffer = buffer
        position = 0
    }

    fun read(sink: Buffer, byteCount: Long): Long {
        if (exhausted()) return -1

        val remaining = buffer.byteLength - position
        val readBytes = minOf(remaining.toLong(), byteCount)

        repeat(readBytes.toInt()) {
            sink.writeByte(buffer[position++].toInt())
        }

        return readBytes
    }

    fun exhausted(): Boolean =
        position == -1 || position == buffer.byteLength
}
