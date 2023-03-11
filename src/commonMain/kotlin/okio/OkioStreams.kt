package net.benwoodworth.knbt.okio

import kotlinx.serialization.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.encodeToNbtWriter
import net.benwoodworth.knbt.internal.BinaryNbtReader
import net.benwoodworth.knbt.internal.BinaryNbtWriter
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.NonClosingSink
import net.benwoodworth.knbt.internal.NonClosingSource
import okio.*

/**
 * Serializes the [value] with [serializer] into a [sink] using NBT format.
 *
 * @throws [SerializationException] if the given value cannot be serialized to NBT.
 * @throws [okio.IOException] If an I/O error occurs and sink can't be written to.
 */
@OkioApi
public fun <T> Nbt.encodeToBufferedSink(
    serializer: SerializationStrategy<T>, value: T, sink: BufferedSink
) {
    val binarySink = configuration.variant.getBinarySink(
        configuration.compression.compress(NonClosingSink(sink), configuration.compressionLevel).buffer()
    )

    BinaryNbtWriter(binarySink).use { writer ->
        encodeToNbtWriter(writer, serializer, value)
    }
}

/**
 * Serializes given [value] to a [sink] using serializer retrieved from the reified type parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to NBT.
 * @throws [okio.IOException] If an I/O error occurs and sink can't be written to.
 */
@OkioApi
public inline fun <reified T> Nbt.encodeToBufferedSink(
    value: T, sink: BufferedSink
): Unit = encodeToBufferedSink(serializersModule.serializer(), value, sink)

/**
 * Deserializes NBT from [source] to a value of type [T] using [deserializer].
 *
 * @throws [SerializationException] if the given NBT input cannot be deserialized to the value of type [T].
 * @throws [okio.IOException] If an I/O error occurs and source can't be read from.
 */
@OkioApi
public fun <T> Nbt.decodeFromBufferedSource(
    deserializer: DeserializationStrategy<T>, source: BufferedSource
): T {
    val variant = configuration.variant
    val compression = configuration.compression

    val nonClosingSource = NonClosingSource(source).buffer()

    val detectedCompression = try {
        NbtCompression.detect(nonClosingSource)
    } catch (e: NbtDecodingException) {
        null
    }

    if (detectedCompression != null && compression != detectedCompression) {
        throw NbtDecodingException("Expected compression to be $compression, but was $detectedCompression")
    }

    val binarySource = variant.getBinarySource(
        compression.decompress(nonClosingSource).buffer()
    )

    return BinaryNbtReader(binarySource).use { reader ->
        decodeFromNbtReader(reader, deserializer)
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
