package net.benwoodworth.knbt.internal

import kotlinx.cinterop.*
import net.benwoodworth.knbt.internal.zlib.ZlibException
import net.benwoodworth.knbt.internal.zlib.deflate
import net.benwoodworth.knbt.internal.zlib.deflateEnd
import net.benwoodworth.knbt.internal.zlib.deflateInit2
import okio.*
import platform.posix.free
import platform.zlib.*

internal actual fun Source.asGzipSource(): Source =
    ZlibSource(this.buffer())

internal actual fun Sink.asGzipSink(level: Int): Sink =
    ZlibSink(this.buffer(), true, level)

internal actual fun Source.asZlibSource(): Source =
    ZlibSource(this.buffer())

internal actual fun Sink.asZlibSink(level: Int): Sink =
    ZlibSink(this.buffer(), false, level)

// TODO Check this over. It worked 1st try after porting from C and I'm suspicious...
private class ZlibSource(private val source: BufferedSource) : Source by source {
    // https://www.zlib.net/manual.html
    // https://stackoverflow.com/questions/17285793/c-inflate-gzip-char-array

    private companion object {
        private const val inputBufferSize: UInt = 1u
        private const val outputBufferSize: UInt = 1024u
    }

    private var ret: Int
    private val strm: z_stream = nativeHeap.alloc()
    private val inbuf: CArrayPointer<UByteVar> = nativeHeap.allocArray(inputBufferSize.toLong())
    private val outbuf: CArrayPointer<uByteVar> = nativeHeap.allocArray(outputBufferSize.toLong())
    private var closed = false

    init {
        ret = inflateInit2(strm.ptr, 32) // 32 = detect Zlib/Gzip from header
        if (ret != Z_OK) throw ZlibException(ret, strm)
    }

    private fun readSourceIn(byteCount: UInt): UInt {
        var count = 0u
        while (count < inputBufferSize && count < byteCount && !source.exhausted()) {
            inbuf[count.toLong()] = source.readByte().toUByte()
            count++
        }
        return count
    }

    override fun read(sink: Buffer, byteCount: Long): Long {
        if (closed) throw IOException("Source is closed")

        // done when inflate() says it's done
        if (ret == Z_STREAM_END) return -1L

        try {
            val byteCountUInt = minOf(UInt.MAX_VALUE.toLong(), byteCount).toUInt()
            strm.avail_in = readSourceIn(byteCountUInt)
        } catch (t: Throwable) {
            inflateEnd(strm.ptr)
            throw ZlibException(Z_ERRNO, strm)
        }

        if (strm.avail_in == 0u)
            return 0L

        strm.next_in = inbuf

        var bytesWritten = 0L

        // run inflate() on input until output buffer not full
        do {
            strm.avail_out = outputBufferSize
            strm.next_out = outbuf

            ret = inflate(strm.ptr, Z_NO_FLUSH)
            when (ret) {
                Z_STREAM_ERROR -> throw ZlibException(ret, strm) // State not clobbered
                Z_NEED_DICT -> {
                    inflateEnd(strm.ptr)
                    throw ZlibException(Z_DATA_ERROR, strm)
                }
                Z_DATA_ERROR,
                Z_MEM_ERROR -> {
                    inflateEnd(strm.ptr)
                    throw ZlibException(ret, strm)
                }
            }

            try {
                val have = outputBufferSize - strm.avail_out
                bytesWritten += have.toLong()
                for (i in 0u until have) {
                    sink.writeByte(outbuf[i.toLong()].toInt())
                }
            } catch (t: Throwable) {
                inflateEnd(strm.ptr)
                throw ZlibException(Z_ERRNO, strm)
            }
        } while (strm.avail_out == 0u)

        return bytesWritten
    }

    override fun close() {
        if (!closed) {
            free(strm.ptr)
            free(inbuf)
            free(outbuf)

            closed = true
        }
        source.close()
    }
}

private class ZlibSink(private val sink: BufferedSink, gzip: Boolean, level: Int) : Sink by sink {
    private companion object {
        const val outputBufferSize = 1024u
    }

    private val inputBuffer = nativeHeap.allocArray<UByteVar>(1)
    private val outputBuffer = nativeHeap.allocArray<UByteVar>(outputBufferSize.toInt())
    private var closed = false

    private val stream = nativeHeap.alloc<z_stream>().apply {
        zalloc = null
        zfree = null
        opaque = null

        next_in = inputBuffer
        avail_in = 0u

        next_out = outputBuffer
        avail_out = outputBufferSize

        deflateInit2(
            level = level,
            windowBits = 15 + (if (gzip) 16 else 0),
        )

        flush()
    }

    private fun deflateClearingBuffer(flush: Int = Z_NO_FLUSH) {
        if (stream.avail_out == 0u) error("Empty output before deflate")

        var result = stream.deflate(flush)
        while (result == Z_OK && stream.avail_out == 0u) {
            clearBuffer()
            result = stream.deflate(flush)
        }
    }

    private fun clearBuffer() {
        val byteCount = outputBufferSize - stream.avail_out
        for (i in 0u until byteCount) {
            sink.writeByte(outputBuffer[i.toLong()].toInt())
        }

        stream.next_out = outputBuffer
        stream.avail_out = outputBufferSize
    }

    override fun write(source: Buffer, byteCount: Long) {
        if (closed) throw IOException("Sink is closed")

        for (i in 0 until byteCount) {
            if (source.exhausted()) throw EOFException("End of source")

            stream.next_in = inputBuffer
            stream.avail_in = 1u

            inputBuffer[0] = source.readByte().toUByte()
            deflateClearingBuffer()
        }
    }

    override fun close() {
        if (!closed) {
            deflateClearingBuffer(Z_FINISH)
            clearBuffer()

            stream.deflateEnd()
            free(stream.ptr)
            free(inputBuffer)
            free(outputBuffer)

            closed = true
        }
        sink.close()
    }
}
