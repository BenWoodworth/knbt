package net.benwoodworth.knbt

import java.io.EOFException
import java.io.InputStream

/**
 * Peek in the [stream] and detect what [NbtCompression] is used.
 *
 * The [stream] must support marking.
 *
 * @throws UnsupportedOperationException the [stream] does not support marking.
 */
public fun NbtCompression.Companion.detect(stream: InputStream): NbtCompression {
    if (!stream.markSupported()) throw UnsupportedOperationException("The stream must support marking")

    stream.mark(2)

    val firstByte = stream.read()
    if (firstByte == -1) throw EOFException()

    val secondByte = stream.read()
        .let { if (it != -1) it else 0 }

    stream.reset()

    return detect(firstByte.toByte(), secondByte.toByte())
}
