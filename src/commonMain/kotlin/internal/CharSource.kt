package net.benwoodworth.knbt.internal

import okio.BufferedSource
import okio.EOFException
import kotlin.jvm.JvmInline

internal interface CharSource {
    fun close()

    fun peek(): CharSource

    fun read(): ReadResult

    @JvmInline
    value class ReadResult private constructor(private val code: Int) {
        companion object {
            private const val EOF_CODE = -1
            val EOF = ReadResult(EOF_CODE)
        }

        constructor(char: Char) : this(char.code)

        override fun toString(): String =
            if (code == EOF_CODE) "EOF" else Char(code.toUShort()).toString()

        fun toChar(): Char =
            if (code == EOF_CODE) {
                error("Character is EOF")
            } else {
                Char(code.toUShort())
            }
    }
}

internal fun CharSource(string: String): CharSource = StringCharSource(string)
internal fun CharSource(source: BufferedSource): CharSource = OkioCharSource(source)

private class StringCharSource private constructor(
    private val string: String,
    private var position: Int,
) : CharSource {
    constructor(string: String) : this(string, 0)

    override fun close() {
    }

    override fun peek(): CharSource =
        StringCharSource(string, position)

    override fun read(): CharSource.ReadResult =
        if (position > string.lastIndex) {
            CharSource.ReadResult.EOF
        } else {
            CharSource.ReadResult(string[position++])
        }
}

private class OkioCharSource private constructor(
    private val source: BufferedSource,
    private var lowSurrogate: Char,
) : CharSource {
    private companion object {
        val NO_LOW_SURROGATE = Char(0u)
        val MIN_SUPPLEMENTARY_CODE_POINT = 0x010000
    }

    constructor(source: BufferedSource) : this(source, NO_LOW_SURROGATE)

    override fun close(): Unit = source.close()

    override fun peek(): CharSource =
        OkioCharSource(source.peek(), lowSurrogate)

    override fun read(): CharSource.ReadResult =
        if (lowSurrogate != NO_LOW_SURROGATE) {
            CharSource.ReadResult(lowSurrogate)
                .also { lowSurrogate = NO_LOW_SURROGATE }
        } else if (source.exhausted()) {
            CharSource.ReadResult.EOF
        } else {
            try {
                val codePoint = source.readUtf8CodePoint()
                if (codePoint ushr 16 == 0) {
                    CharSource.ReadResult(Char(codePoint.toUShort()))
                } else {
                    val highSurrogate = (codePoint ushr 10) +
                            (Char.MIN_HIGH_SURROGATE.code - (MIN_SUPPLEMENTARY_CODE_POINT ushr 10))

                    lowSurrogate = Char(((codePoint and 0x3ff) + Char.MIN_LOW_SURROGATE.code).toUShort())

                    CharSource.ReadResult(Char(highSurrogate))
                }
            } catch (e: EOFException) {
                CharSource.ReadResult.EOF
            }
        }
}
