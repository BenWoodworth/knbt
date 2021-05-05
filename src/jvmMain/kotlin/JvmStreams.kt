package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

@Suppress("DEPRECATION")
public fun <T> Nbt.encodeTo(outputStream: OutputStream, serializer: SerializationStrategy<T>, value: T): Unit =
    encodeTo(outputStream.sink(), serializer, value)

public inline fun <reified T> Nbt.encodeTo(outputStream: OutputStream, value: T): Unit =
    encodeTo(outputStream, serializer(), value)

@Suppress("DEPRECATION")
public fun <T> Nbt.decodeFrom(inputStream: InputStream, deserializer: DeserializationStrategy<T>): T =
    decodeFrom(inputStream.source(), deserializer)

public inline fun <reified T> Nbt.decodeFrom(inputStream: InputStream): T =
    decodeFrom(inputStream, serializer())
