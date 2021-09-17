package net.benwoodworth.knbt.internal

import kotlinx.serialization.SerializationException

public sealed class NbtException(
    message: String,
    internal var path: NbtPath?,
    cause: Throwable? = null,
) : SerializationException(message, cause) {
    protected abstract val coding: String

    override val message: String?
        get() = path?.let { "Error while $coding '$path': ${super.message}" } ?: super.message
}

public class NbtEncodingException internal constructor(
    message: String,
    path: NbtPath? = null,
    cause: Throwable? = null,
) : NbtException(message, path, cause) {
    override val coding: String
        get() = "encoding"
}

public class NbtDecodingException internal constructor(
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
