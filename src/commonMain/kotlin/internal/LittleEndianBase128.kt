package net.benwoodworth.knbt.internal

import okio.BufferedSink
import okio.BufferedSource

/**
 * @throws NbtDecodingException if the value is longer than [maxBytes].
 */
internal fun BufferedSource.readLEB128(context: NbtContext, maxBytes: Int): ULong {
    require(maxBytes in 1..10) { "maxBytes must be in 1..10, but is $maxBytes" }

    var readCount = 0
    var result = 0L

    do {
        val byte = readByte().toInt()
        val value = (byte and 0b01111111).toLong()
        result = result or (value shl (7 * readCount))

        if (++readCount > maxBytes) {
            throw NbtDecodingException(context, "LEB128 value is too big. Byte length should be in 1..$maxBytes.")
        }
    } while (byte and 0b10000000 != 0)

    return result.toULong()
}

internal fun BufferedSink.writeLEB128(n: ULong): BufferedSink {
    var value = n.toLong()

    do {
        var temp = value and 127L

        value = value ushr 7
        if (value != 0L) {
            temp = temp or 128L
        }

        writeByte(temp.toInt())
    } while (value != 0L)

    return this
}

internal fun Long.zigZagEncode(): ULong =
    ((this shl 1) xor (this shr 63)).toULong()

internal fun ULong.zigZagDecode(): Long =
    toLong().let { n -> (n ushr 1) xor -(n and 1) }
