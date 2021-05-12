package net.benwoodworth.knbt.internal.pako

import org.khronos.webgl.Uint8Array
import kotlin.js.json

@JsModule("pako")
@JsNonModule
private external object Pako {
    @JsName("constants")
    object Constants {
        // https://github.com/nodeca/pako/blob/master/lib/zlib/constants.js

        /* Allowed flush values; see deflate() and inflate() below for details */
        val Z_NO_FLUSH: Int
        val Z_PARTIAL_FLUSH: Int
        val Z_SYNC_FLUSH: Int
        val Z_FULL_FLUSH: Int
        val Z_FINISH: Int
        val Z_BLOCK: Int
        val Z_TREES: Int

        /* Return codes for the compression/decompression functions. Negative values
        * are errors, positive values are used for special but normal events.
        */
        val Z_OK: Int
        val Z_STREAM_END: Int
        val Z_NEED_DICT: Int
        val Z_ERRNO: Int
        val Z_STREAM_ERROR: Int
        val Z_DATA_ERROR: Int
        val Z_MEM_ERROR: Int
        val Z_BUF_ERROR: Int
        //val Z_VERSION_ERROR: Int

        /* compression levels */
        val Z_NO_COMPRESSION: Int
        val Z_BEST_SPEED: Int
        val Z_BEST_COMPRESSION: Int
        val Z_DEFAULT_COMPRESSION: Int

        val Z_FILTERED: Int
        val Z_HUFFMAN_ONLY: Int
        val Z_RLE: Int
        val Z_FIXED: Int
        val Z_DEFAULT_STRATEGY: Int

        /* Possible values of the data_type field (though see inflate()) */
        val Z_BINARY: Int
        val Z_TEXT: Int

        //val Z_ASCII: Int = 1 // = Z_TEXT (deprecated)
        val Z_UNKNOWN: Int

        /* The deflate compression method */
        val Z_DEFLATED: Int
        //val Z_NULL: Int = null // Use -1 or null inline, depending on var type
    }

    class Deflate(options: Any = definedExternally) {
        val err: Int
        val msg: String
        val result: Uint8Array

        var onData: (chunk: Uint8Array) -> Unit
        var onEnd: (status: Int) -> Unit

        fun push(data: Uint8Array, flush_mode: Int): Boolean
    }

    class Inflate(options: Any = definedExternally) {
        val err: Int
        val msg: String
        val result: Uint8Array

        var onData: (chunk: Uint8Array) -> Unit
        var onEnd: (status: Int) -> Unit

        fun push(data: Uint8Array, flush_mode: Int): Boolean
    }
}

internal enum class ZFlushMode(val flushMode: Int) {
    NO_FLUSH(0),
    PARTIAL_FLUSH(1),
    SYNC_FLUSH(2),
    FULL_FLUSH(3),
    FINISH(4),
    BLOCK(5),
    TREES(6),
}

internal enum class ZStatus(val status: Int) {
    VERSION_ERROR(-6),
    BUF_ERROR(-5),
    MEM_ERROR(-4),
    DATA_ERROR(-3),
    STREAM_ERROR(-2),
    ERRNO(-1),
    OK(0),
    STREAM_END(1),
    NEED_DICT(2),
    ;

    val isError: Boolean
        get() = status < 0

    companion object {
        fun fromCode(status: Int): ZStatus {
            require(status in -6..2) { "Invalid Status code: $status" }
            return enumValues<ZStatus>()[status - 6]
        }
    }
}

internal value class ZLevel(
    /** Between 0 (No compression) and 9 (Best compression), or -1 (Default compression) */
    val level: Int,
) {
    init {
        require(level in -1..9) { "Compression level must be in -1..9" }
    }

    companion object {
        val NO_COMPRESSION = ZLevel(Pako.Constants.Z_NO_COMPRESSION)
        val BEST_SPEED = ZLevel(Pako.Constants.Z_BEST_SPEED)
        val BEST_COMPRESSION = ZLevel(Pako.Constants.Z_BEST_COMPRESSION)
        val DEFAULT_COMPRESSION = ZLevel(Pako.Constants.Z_DEFAULT_COMPRESSION)
    }
}

internal enum class ZStrategy(val constant: Int) {
    FILTERED(Pako.Constants.Z_FILTERED),
    HUFFMAN_ONLY(Pako.Constants.Z_HUFFMAN_ONLY),
    RLE(Pako.Constants.Z_RLE),
    FIXED(Pako.Constants.Z_FIXED),
    DEFAULT_STRATEGY(Pako.Constants.Z_DEFAULT_STRATEGY),
}

internal enum class ZDataType(val dataType: Int) {
    BINARY(Pako.Constants.Z_BINARY),
    TEXT(Pako.Constants.Z_TEXT),
    UNKNOWN(Pako.Constants.Z_UNKNOWN),
}

internal class Deflate(options: Options? = null) {
    private val deflate = Pako.Deflate(
        json(
            "level" to options?.level?.level,
            "windowBits" to options?.windowBits,
            "memLevel" to options?.memLevel,
            "strategy" to options?.strategy?.constant,
        )
    )

    val err: ZStatus
        get() = ZStatus.fromCode(deflate.err)

    val msg: String by deflate::msg
    val result: Uint8Array by deflate::result

    @get:Deprecated("onData is write-only", level = DeprecationLevel.ERROR)
    var onData: (chunk: Uint8Array) -> Unit by deflate::onData

    @get:Deprecated("onEnd is write-only", level = DeprecationLevel.ERROR)
    var onEnd: (status: ZStatus) -> Unit
        get() = error("onEnd is write-only")
        set(value) {
            deflate.onEnd = { status -> value(ZStatus.fromCode(status)) }
        }

    fun push(data: Uint8Array, flushMode: ZFlushMode = ZFlushMode.NO_FLUSH): Boolean =
        deflate.push(data, flushMode.flushMode)

    class Options(
        var level: ZLevel? = null,
        var windowBits: Int? = null,
        var memLevel: Int? = null,
        var strategy: ZStrategy? = null,
    )
}

internal fun Deflate(builderAction: Deflate.Options.() -> Unit): Deflate =
    Deflate(Deflate.Options().apply(builderAction))

internal class Inflate(options: Options? = null) {
    private val inflate = Pako.Inflate(
        json(
            "windowBits" to options?.windowBits,
        )
    )

    val err: ZStatus
        get() = ZStatus.fromCode(inflate.err)

    val msg: String by inflate::msg
    val result: Uint8Array by inflate::result

    @get:Deprecated("onData is write-only", level = DeprecationLevel.ERROR)
    var onData: (chunk: Uint8Array) -> Unit by inflate::onData

    @get:Deprecated("onEnd is write-only", level = DeprecationLevel.ERROR)
    var onEnd: (status: Int) -> Unit
        get() = error("onEnd is write-only")
        set(value) {
            inflate.onEnd = value
        }

    fun push(data: Uint8Array, flushMode: ZFlushMode = ZFlushMode.NO_FLUSH): Boolean =
        inflate.push(data, flushMode.flushMode)

    class Options(
        var windowBits: Int? = null,
    )
}

internal fun Inflate(builderAction: Inflate.Options.() -> Unit): Inflate =
    Inflate(Inflate.Options().apply(builderAction))
