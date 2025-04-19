package net.benwoodworth.knbt.internal

import kotlinx.serialization.SerializationException

internal open class NbtException(
    context: NbtContext,
    message: String,
    cause: Throwable? = null,
) : SerializationException(message, cause) {
    internal var path = context.getPath()
}

internal class NbtEncodingException(
    context: NbtContext,
    message: String,
    cause: Throwable? = null,
) : NbtException(context, message, cause)

internal class NbtDecodingException(
    context: NbtContext,
    message: String,
    cause: Throwable? = null,
) : NbtException(context, message, cause)
