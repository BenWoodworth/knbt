package net.benwoodworth.knbt

import kotlinx.serialization.SerializationException

@Deprecated(
    "NbtException is now internal",
    ReplaceWith("SerializationException", "kotlinx.serialization.SerializationException"),
    DeprecationLevel.ERROR,
)
public typealias NbtException = SerializationException

@Deprecated(
    "NbtEncodingException is now internal",
    ReplaceWith("SerializationException", "kotlinx.serialization.SerializationException"),
    DeprecationLevel.ERROR,
)
public typealias NbtEncodingException = SerializationException

@Deprecated(
    "NbtDecodingException is now internal",
    ReplaceWith("SerializationException", "kotlinx.serialization.SerializationException"),
    DeprecationLevel.ERROR,
)
public typealias NbtDecodingException = SerializationException
