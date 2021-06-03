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
public fun NbtCompression.Companion.detect(stream: InputStream): NbtCompression? {
    if (!stream.markSupported()) throw UnsupportedOperationException("The stream must support marking")

    stream.mark(1)
    val firstByte = stream.read()
    if (firstByte == -1) throw EOFException()
    stream.reset()

    return detect(firstByte.toByte())
}
