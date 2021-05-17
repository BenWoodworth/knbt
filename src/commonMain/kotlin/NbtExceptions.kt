package net.benwoodworth.knbt

import kotlinx.serialization.SerializationException

public open class NbtException(message: String, cause: Throwable? = null) : SerializationException(message, cause)

public class NbtEncodingException(message: String, cause: Throwable? = null) : NbtException(message, cause)

public class NbtDecodingException(message: String, cause: Throwable? = null) : NbtException(message, cause)
