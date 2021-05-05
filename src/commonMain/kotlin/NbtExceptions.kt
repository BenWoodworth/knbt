package net.benwoodworth.knbt

import kotlinx.serialization.SerializationException

public open class NbtException(message: String) : SerializationException(message)

public class NbtEncodingException(message: String) : NbtException(message)

public class NbtDecodingException(message: String) : NbtException(message)
