package net.benwoodworth.knbt.internal

import kotlinx.serialization.SerializationException

internal sealed class NbtException(
    message: String,
    internal var path: NbtPath?,
    cause: Throwable? = null,
) : SerializationException(message, cause) {
    protected abstract val coding: String

    // On Kotlin/JS, `super.message` seems to be calling `this.message` instead, resulting in infinite recursion
    private val superMessage = message

    override val message: String?
        get() = path?.let { "Error while $coding '$path': $superMessage" } ?: superMessage
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
