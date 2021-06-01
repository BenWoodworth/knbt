package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

/**
 * Encode NBT to an [OutputStream].
 *
 * *Note*: It is the caller's responsibility to close the [outputStream].
 */
@OptIn(OkioApi::class)
public fun <T> Nbt.encodeTo(outputStream: OutputStream, serializer: SerializationStrategy<T>, value: T): Unit =
    encodeTo(outputStream.sink(), serializer, value)

/**
 * Encode NBT to an [OutputStream].
 *
 * *Note*: It is the caller's responsibility to close the [outputStream].
 */
public inline fun <reified T> Nbt.encodeTo(outputStream: OutputStream, value: T): Unit =
    encodeTo(outputStream, serializer(), value)

/**
 * Decode NBT from an [InputStream].
 *
 * *Note*: It is the caller's responsibility to close the [inputStream].
 */
@OptIn(OkioApi::class)
public fun <T> Nbt.decodeFrom(inputStream: InputStream, deserializer: DeserializationStrategy<T>): T =
    decodeFrom(inputStream.source(), deserializer)

/**
 * Decode NBT from an [InputStream].
 *
 * *Note*: It is the caller's responsibility to close the [inputStream].
 */
public inline fun <reified T> Nbt.decodeFrom(inputStream: InputStream): T =
    decodeFrom(inputStream, serializer())
