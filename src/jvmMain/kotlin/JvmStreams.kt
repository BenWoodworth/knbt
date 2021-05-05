package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

@Suppress("DEPRECATION")
public fun <T> Nbt.encodeToOutputStream(serializer: SerializationStrategy<T>, value: T, outputStream: OutputStream) {
    encodeToSink(serializer, value, outputStream.sink())
}

public inline fun <reified T> Nbt.encodeToOutputStream(value: T, outputStream: OutputStream): Unit =
    encodeToOutputStream(serializer(), value, outputStream)

@Suppress("DEPRECATION")
public fun <T> Nbt.decodeFromInputStream(deserializer: DeserializationStrategy<T>, inputStream: InputStream): T =
    decodeFromSource(deserializer, inputStream.source())

public inline fun <reified T> Nbt.decodeFromInputStream(inputStream: InputStream): T =
    decodeFromInputStream(serializer(), inputStream)
