package net.benwoodworth.knbt.internal

import kotlinx.serialization.SerializationException

internal sealed class NbtException(
    message: String,
    internal var path: NbtPath?,
    cause: Throwable? = null,
) : SerializationException(message, cause) {
    protected abstract val coding: String

    // Causes problems with Kotlin JS IR: https://youtrack.jetbrains.com/issue/KT-43490
    //override val message: String?
    //    get() = path?.let { "Error while $coding '$path': ${super.message}" } ?: super.message
}

internal class NbtEncodingException(
    message: String,
    path: NbtPath? = null,
    cause: Throwable? = null,
) : NbtException(message, path, cause) {
    override val coding: String
        get() = "encoding"
}

internal class NbtDecodingException(
    message: String,
    path: NbtPath? = null,
    cause: Throwable? = null,
) : NbtException(message, path, cause) {
    override val coding: String
        get() = "decoding"
}

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
        }
    }
}
