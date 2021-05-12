package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.pako.Inflate
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
    private companion object {
        private const val CHUNK: Int = 1024
    }

    private val inbuf = Uint8Array(CHUNK)
    private val outbuf = Buffer()
    private var inflateDone = false

    private val inflate = Inflate().apply {
        onData = ::onData
        onEnd = ::onEnd
    }

    override fun read(sink: Buffer, byteCount: Long): Long {
        if (!outbuf.exhausted()) return outbuf.read(sink, byteCount)
        if (inflateDone) return -1L

        var inbufCount = 0
        for (i in 0 until inbuf.byteLength) {
            if (source.exhausted()) break
            inbuf[i] = source.readByte()
            inbufCount++
        }

        if (inbufCount < inbuf.byteLength) {
            inflate.push(inbuf.subarray(0, inbufCount))
        } else {
            inflate.push(inbuf)
        }

        return outbuf.read(sink, byteCount)
    }

    private fun onData(data: Uint8Array) {
        for (i in 0 until data.byteLength) {
            outbuf.writeByte(data[i].toInt())
        }
    }

    private fun onEnd(status: Int) {
        inflateDone = true
        if (status != ZStatus.OK.status) throw IOException("Bad inflate status: $status (${inflate.msg})")
    }
}
