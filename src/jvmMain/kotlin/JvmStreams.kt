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
 * *Note*: It is the caller's responsibility to close the [output].
 */
@OptIn(OkioApi::class)
public fun <T> Nbt.encodeToStream(serializer: SerializationStrategy<T>, value: T, output: OutputStream): Unit =
    encodeToSink(serializer, value, output.sink())

/**
 * Encode NBT to an [OutputStream].
 *
 * *Note*: It is the caller's responsibility to close the [outputStream].
 */
@OptIn(OkioApi::class)
@Deprecated(
    "Replaced with encodeToStream(...)",
    ReplaceWith("encodeToStream<T>(serializer, value, outputStream)", "net.benwoodworth.knbt.encodeToStream"),
    DeprecationLevel.ERROR,
)
@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Nbt.encodeTo(outputStream: OutputStream, serializer: SerializationStrategy<T>, value: T): Unit =
    encodeToStream(serializer, value, outputStream)

/**
 * Encode NBT to an [OutputStream].
 *
 * *Note*: It is the caller's responsibility to close the [output].
 */
public inline fun <reified T> Nbt.encodeToStream(value: T, output: OutputStream): Unit =
    encodeToStream(serializersModule.serializer(), value, output)

/**
 * Encode NBT to an [OutputStream].
 *
 * *Note*: It is the caller's responsibility to close the [outputStream].
 */
@Deprecated(
    "Replaced with encodeToStream(...)",
    ReplaceWith("encodeToStream<T>(value, outputStream)", "net.benwoodworth.knbt.encodeToStream"),
    DeprecationLevel.ERROR,
)
@Suppress("NOTHING_TO_INLINE")
public inline fun <reified T> Nbt.encodeTo(outputStream: OutputStream, value: T): Unit =
    encodeToStream(value, outputStream)

/**
 * Decode NBT from an [InputStream].
 *
 * *Note*: It is the caller's responsibility to close the [input].
 */
@OptIn(OkioApi::class)
public fun <T> Nbt.decodeFromStream(deserializer: DeserializationStrategy<T>, input: InputStream): T =
    decodeFromSource(deserializer, input.source())

/**
 * Decode NBT from an [InputStream].
 *
 * *Note*: It is the caller's responsibility to close the [inputStream].
 */
@Deprecated(
    "Replaced with decodeFromStream(...)",
    ReplaceWith("this.decodeFromStream<T>(serializer, inputStream)", "net.benwoodworth.knbt.decodeFromStream"),
    DeprecationLevel.ERROR,
)
@OptIn(OkioApi::class)
public fun <T> Nbt.decodeFrom(inputStream: InputStream, deserializer: DeserializationStrategy<T>): T =
    decodeFromStream(deserializer, inputStream)

/**
 * Decode NBT from an [InputStream].
 *
 * *Note*: It is the caller's responsibility to close the [input].
 */
public inline fun <reified T> Nbt.decodeFromStream(input: InputStream): T =
    decodeFromStream(serializersModule.serializer(), input)

/**
 * Decode NBT from an [InputStream].
 *
 * *Note*: It is the caller's responsibility to close the [inputStream].
 */
@Deprecated(
    "Replaced with decodeFromStream(...)",
    ReplaceWith("this.decodeFromStream<T>(inputStream)", "net.benwoodworth.knbt.decodeFromStream"),
    DeprecationLevel.ERROR,
)
public inline fun <reified T> Nbt.decodeFrom(inputStream: InputStream): T =
    decodeFromStream(inputStream)
