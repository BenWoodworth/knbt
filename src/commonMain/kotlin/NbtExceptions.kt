package net.benwoodworth.knbt

import kotlinx.serialization.SerializationException
import net.benwoodworth.knbt.internal.NbtPath

public sealed class NbtException(message: String, internal var path: NbtPath?) : SerializationException(message) {
    protected abstract val coding: String

    override val message: String?
        get() = path?.let { "Error while $coding '$path': ${super.message}" } ?: super.message
}

public class NbtEncodingException internal constructor(
    message: String,
    path: NbtPath? = null,
) : NbtException(message, path) {
    override val coding: String
        get() = "encoding"
}

public class NbtDecodingException internal constructor(
    message: String,
    path: NbtPath? = null,
) : NbtException(message, path) {
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
