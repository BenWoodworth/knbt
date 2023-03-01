package net.benwoodworth.knbt.internal

import kotlinx.serialization.SerializationException

internal open class NbtException(
    message: String,
    internal var path: NbtPath? = null,
    cause: Throwable? = null,
) : SerializationException(message, cause)

internal class NbtEncodingException(
    message: String,
    path: NbtPath? = null,
    cause: Throwable? = null,
) : NbtException(message, path, cause)

internal class NbtDecodingException(
    message: String,
    path: NbtPath? = null,
    cause: Throwable? = null,
) : NbtException(message, path, cause)

internal inline fun <R> tryWithPath(path: () -> NbtPath, block: () -> R): R {
    try {
        return block()
    } catch (e: NbtException) {
        if (e.path == null) e.path = path()
        throw e
    }
}

internal inline fun <R> tryOrRethrowWithNbtPath(block: () -> R): R {
    try {
        return block()
    } catch (e: NbtException) {
        if (e.path == null) throw e

        throw when (e) {
            is NbtEncodingException -> NbtEncodingException("Error while encoding '${e.path}'", e.path, e)
            is NbtDecodingException -> NbtDecodingException("Error while decoding '${e.path}'", e.path, e)
            else -> NbtException("Error at '${e.path}'", e.path, e)
        }
    }
}
