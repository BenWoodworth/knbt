package net.benwoodworth.knbt.internal

import kotlinx.cinterop.*
import okio.*
import platform.zlib.*

internal actual fun Source.asGzipSource(): Source =
    ZlibSource(this.buffer())

internal actual fun Sink.asGzipSink(): Sink =
    throw UnsupportedOperationException("Gzip compression is not currently supported for Kotlin/Native")

internal actual fun Source.asZlibSource(): Source =
    ZlibSource(this.buffer())

internal actual fun Sink.asZlibSink(): Sink =
    throw UnsupportedOperationException("Zlib compression is not currently supported for Kotlin/Native")

private class ZlibError(errorNumber: Int, cause: Throwable? = null) : Error("zlib error: $errorNumber", cause)

// TODO Check this over. It worked 1st try after porting from C and I'm suspicious...
private class ZlibSource(private val source: BufferedSource) : Source by source {
    // https://www.zlib.net/manual.html
    // https://stackoverflow.com/questions/17285793/c-inflate-gzip-char-array

    private companion object {
        private const val CHUNK: UInt = 1024u
    }

    private var ret: Int
    private val strm: z_stream = nativeHeap.alloc()
    private val inbuf: CArrayPointer<UByteVar> = nativeHeap.allocArray(CHUNK.toLong())
    private val outbuf: CArrayPointer<uByteVar> = nativeHeap.allocArray(CHUNK.toLong())

    init {
        ret = inflateInit2(strm.ptr, 32) // 32 = detect Zlib/Gzip from header
        if (ret != Z_OK) throw ZlibError(ret)
    }

    private fun readSourceIn(byteCount: UInt): UInt {
        var count = 0u
        while (count < CHUNK && count < byteCount && !source.exhausted()) {
            inbuf[count.toLong()] = source.readByte().toUByte()
            count++
        }
        return count
    }

    override fun read(sink: Buffer, byteCount: Long): Long {
        // done when inflate() says it's done
        if (ret == Z_STREAM_END) return -1L

        try {
            val byteCountUInt = minOf(UInt.MAX_VALUE.toLong(), byteCount).toUInt()
            strm.avail_in = readSourceIn(byteCountUInt)
        } catch (t: Throwable) {
            inflateEnd(strm.ptr)
            throw ZlibError(Z_ERRNO)
        }

        if (strm.avail_in == 0u)
            return 0L

        strm.next_in = inbuf

        var bytesWritten = 0L

        // run inflate() on input until output buffer not full
        do {
            strm.avail_out = CHUNK
            strm.next_out = outbuf

            ret = inflate(strm.ptr, Z_NO_FLUSH)
            when (ret) {
                Z_STREAM_ERROR -> throw ZlibError(ret) // State not clobbered
                Z_NEED_DICT -> {
                    inflateEnd(strm.ptr)
                    throw ZlibError(Z_DATA_ERROR)
                }
                Z_DATA_ERROR,
                Z_MEM_ERROR -> {
                    inflateEnd(strm.ptr)
                    throw ZlibError(ret)
                }
            }

            try {
                val have = CHUNK - strm.avail_out
                bytesWritten += have.toLong()
                for (i in 0u until have) {
                    sink.writeByte(outbuf[i.toLong()].toInt())
                }
            } catch (t: Throwable) {
                inflateEnd(strm.ptr)
                throw ZlibError(Z_ERRNO)
            }
        } while (strm.avail_out == 0u)

        return bytesWritten
    }
}
