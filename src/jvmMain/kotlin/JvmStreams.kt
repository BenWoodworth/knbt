package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.*
import java.io.InputStream
import java.io.OutputStream

/**
 * Encode NBT to an [OutputStream].
 *
 * *Note*: It is the caller's responsibility to close the [output].
 */
@OptIn(OkioApi::class)
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
@OptIn(OkioApi::class)
public fun <T> Nbt.decodeFromStream(deserializer: DeserializationStrategy<T>, input: InputStream): T {
    val source = input.source()

    val restrictedSource = object : Source by source {
        // Restrict to reading 1 byte at a time, since the BufferedSource will pull a whole segment, and the caller
        // won't have access to the extra bytes anymore in the case that the input needs to be decoded from again.
        override fun read(sink: Buffer, byteCount: Long): Long =
            source.read(sink, if (byteCount > 1) 1 else byteCount)
    }

    return decodeFromBufferedSource(deserializer, restrictedSource.buffer())
}

/**
 * Decode NBT from an [InputStream].
 *
 * *Note*: It is the caller's responsibility to close the [input].
 */
public inline fun <reified T> Nbt.decodeFromStream(input: InputStream): T =
    decodeFromStream(serializersModule.serializer(), input)
