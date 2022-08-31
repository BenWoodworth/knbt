package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

/**
 * Encode NBT to an [OutputStream].
 *
 * *Note*: It is the caller's responsibility to close the [output].
 */
@OptIn(ExperimentalNbtApi::class)
public fun <T> Nbt.encodeToStream(serializer: SerializationStrategy<T>, value: T, output: OutputStream): Unit =
    encodeToBufferedSink(serializer, value, output.sink().buffer())

/**
 * Encode NBT to an [OutputStream].
 *
 * *Note*: It is the caller's responsibility to close the [output].
 */
public inline fun <reified T> Nbt.encodeToStream(value: T, output: OutputStream): Unit =
    encodeToStream(serializersModule.serializer(), value, output)

/**
 * Decode NBT from an [InputStream].
 *
 * *Note*: It is the caller's responsibility to close the [input].
 */
@OptIn(ExperimentalNbtApi::class)
public fun <T> Nbt.decodeFromStream(deserializer: DeserializationStrategy<T>, input: InputStream): T =
    this.decodeFromBufferedSource(deserializer, input.source().buffer())

/**
 * Decode NBT from an [InputStream].
 *
 * *Note*: It is the caller's responsibility to close the [input].
 */
public inline fun <reified T> Nbt.decodeFromStream(input: InputStream): T =
    decodeFromStream(serializersModule.serializer(), input)
