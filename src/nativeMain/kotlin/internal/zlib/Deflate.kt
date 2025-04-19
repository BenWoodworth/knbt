@file:OptIn(ExperimentalForeignApi::class)

package net.benwoodworth.knbt.internal.zlib

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.zlib.*

internal fun z_stream.deflateInit2(
    level: Int = Z_DEFAULT_COMPRESSION,
    method: Int = Z_DEFLATED,
    windowBits: Int = 15,
    memLevel: Int = 8,
    strategy: Int = Z_DEFAULT_STRATEGY,
) {
    val result = deflateInit2(
        strm = this.ptr,
        level = level,
        method = method,
        windowBits = windowBits,
        memLevel = memLevel,
        strategy = strategy,
    )

    if (result < 0) throw ZlibException(result, this)
}

internal fun z_stream.deflate(flush: Int = Z_NO_FLUSH): Int =
    deflate(this.ptr, flush)

internal fun z_stream.deflateEnd() {
    val result = deflateEnd(this.ptr)

    if (result < 0) throw ZlibException(result, this)
}
