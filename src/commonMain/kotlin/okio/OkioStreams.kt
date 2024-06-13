package net.benwoodworth.knbt.okio

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource
import okio.buffer
import okio.use

/**
 * Serializes the [value] with [serializer] into a [sink] using NBT format.
 *
 * @throws [SerializationException] if the given value cannot be serialized to NBT.
 * @throws [okio.IOException] If an I/O error occurs and sink can't be written to.
 */
@OkioApi
public fun <T> Nbt.encodeToBufferedSink(serializer: SerializationStrategy<T>, value: T, sink: BufferedSink) {
    val context = SerializationNbtContext()

    val compressingSink = configuration.compression
        .compress(NonClosingSink(sink), configuration.compressionLevel)
        .buffer()

    compressingSink.use {
        val writer = configuration.variant.getNbtWriter(context, compressingSink)
        val encoder = NbtWriterEncoder(this, context, writer)

        encoder.encodeSerializableValue(serializer, value)
    }
}

/**
 * Serializes given [value] to a [sink] using serializer retrieved from the reified type parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to NBT.
 * @throws [okio.IOException] If an I/O error occurs and sink can't be written to.
 */
@OkioApi
public inline fun <reified T> Nbt.encodeToBufferedSink(value: T, sink: BufferedSink): Unit =
    encodeToBufferedSink(serializersModule.serializer(), value, sink)

/**
 * Deserializes NBT from [source] to a value of type [T] using [deserializer].
 *
 * @throws [SerializationException] if the given NBT input cannot be deserialized to the value of type [T].
 * @throws [okio.IOException] If an I/O error occurs and source can't be read from.
 */
@OkioApi
public fun <T> Nbt.decodeFromBufferedSource(deserializer: DeserializationStrategy<T>, source: BufferedSource): T {
    val context = SerializationNbtContext()
    val variant = configuration.variant
    val compression = configuration.compression

    val nonClosingSource = NonClosingSource(source).buffer()

    val detectedCompression = try {
        NbtCompression.detect(nonClosingSource)
    } catch (e: NbtDecodingException) {
        null
    }

    if (detectedCompression != null && compression != detectedCompression) {
        throw NbtDecodingException(context, "Expected compression to be $compression, but was $detectedCompression")
    }

    val decompressingSource = compression.decompress(nonClosingSource).buffer()

    return decompressingSource.use {
        val reader = variant.getNbtReader(context, decompressingSource)
        val decoder = NbtReaderDecoder(this, context, reader)

        decoder.decodeSerializableValue(deserializer)
    }
}

/**
 * Deserializes the contents of given [source] to the value of type [T] using
 * deserializer retrieved from the reified type parameter.
 *
 * @throws [SerializationException] if the given NBT input cannot be deserialized to the value of type [T].
 * @throws [okio.IOException] If an I/O error occurs and source can't be read from.
 */
@OkioApi
public inline fun <reified T> Nbt.decodeFromBufferedSource(source: BufferedSource): T =
    decodeFromBufferedSource(serializersModule.serializer(), source)

@OkioApi
public fun NbtCompression.Companion.detect(source: BufferedSource): NbtCompression =
    detect(EmptyNbtContext, source.peek().readByte())
