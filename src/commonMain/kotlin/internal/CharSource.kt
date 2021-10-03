package net.benwoodworth.knbt.internal

import okio.BufferedSource
import okio.EOFException

internal interface CharSource {
    fun close()

    fun peek(): CharSource

    fun exhausted(): Boolean

    fun readChar(): Char
}

internal fun CharSource(string: String): CharSource = StringCharSource(string)
internal fun CharSource(source: BufferedSource): CharSource = SourceCharSource(source)


private class StringCharSource private constructor(
    private val string: String,
    private var position: Int,
) : CharSource {
    constructor(string: String) : this(string, 0)

    override fun close() {
    }

    override fun peek(): CharSource =
        StringCharSource(string, position)

    override fun exhausted(): Boolean =
        position > string.lastIndex

    override fun readChar(): Char =
        if (exhausted()) {
            throw EOFException()
        } else {
            string[position++]
        }
}

private class SourceCharSource private constructor(
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
        SourceCharSource(source.peek(), lowSurrogate)

    override fun exhausted(): Boolean =
        lowSurrogate != NO_LOW_SURROGATE && source.exhausted()

    override fun readChar(): Char =
        if (lowSurrogate != NO_LOW_SURROGATE) {
            lowSurrogate
                .also { lowSurrogate = NO_LOW_SURROGATE }
        } else {
            val codePoint = source.readUtf8CodePoint()
            if (codePoint ushr 16 == 0) {
                Char(codePoint.toUShort())
            } else {
                lowSurrogate = Char(((codePoint and 0x3ff) + Char.MIN_LOW_SURROGATE.code).toUShort())

                Char((codePoint ushr 10) + (Char.MIN_HIGH_SURROGATE.code - (MIN_SUPPLEMENTARY_CODE_POINT ushr 10)))
            }
        }
}
