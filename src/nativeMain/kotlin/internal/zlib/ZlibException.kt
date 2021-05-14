package net.benwoodworth.knbt.internal.zlib

import kotlinx.cinterop.toKString
import platform.zlib.*

internal class ZlibException(code: Int, stream: z_stream) : Exception(
    run {
        val message = stream.msg?.toKString()

        val codeName = when (code) {
            Z_OK -> "Z_OK"
            Z_STREAM_END -> "Z_STREAM_END"
            Z_NEED_DICT -> "Z_NEED_DICT"
            Z_ERRNO -> "Z_ERRNO"
            Z_STREAM_ERROR -> "Z_STREAM_ERROR"
            Z_DATA_ERROR -> "Z_DATA_ERROR"
            Z_MEM_ERROR -> "Z_MEM_ERROR"
            Z_BUF_ERROR -> "Z_BUF_ERROR"
            Z_VERSION_ERROR -> "Z_VERSION_ERROR"
            else -> ""
        }

        if (message == null) {
            "Zlib error $codeName($code)"
        } else {
            "Zlib error $codeName($code): $message"
        }
    }
)
