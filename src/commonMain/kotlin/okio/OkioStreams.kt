package net.benwoodworth.knbt.okio

import kotlinx.serialization.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.decodeFromNbtReader
import net.benwoodworth.knbt.encodeToNbtWriter
import net.benwoodworth.knbt.internal.BinaryNbtReader
import net.benwoodworth.knbt.internal.BinaryNbtWriter
import okio.BufferedSink
import okio.BufferedSource

// Code is re-purposed kotlinx.serialization's JSON okio implementation:
// https://github.com/Kotlin/kotlinx.serialization/blob/16a85df254f4f1e317554eb61ee1fbe914800aa4/formats/json-okio/commonMain/src/kotlinx/serialization/json/okio/OkioStreams.kt

/**
 * Serializes the [value] with [serializer] into a [target] using NBT format.
 *
 * @throws [SerializationException] if the given value cannot be serialized to NBT.
 * @throws [okio.IOException] If an I/O error occurs and sink can't be written to.
 */
@ExperimentalNbtApi
public fun <T> Nbt.encodeToBufferedSink(
    serializer: SerializationStrategy<T>,
    value: T,
    target: BufferedSink
): Unit =
    encodeToNbtWriter(BinaryNbtWriter(this, target), serializer, value)


/**
 * Serializes given [value] to a [target] using serializer retrieved from the reified type parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to NBT.
 * @throws [okio.IOException] If an I/O error occurs and sink can't be written to.
 */
@ExperimentalNbtApi
public inline fun <reified T> Nbt.encodeToBufferedSink(
    value: T,
    target: BufferedSink
): Unit =
    encodeToBufferedSink(serializersModule.serializer(), value, target)


/**
 * Deserializes NBT from [source] to a value of type [T] using [deserializer].
 *
 * Note that this functions expects that exactly one object would be present in the source
 * and throws an exception if there are any dangling bytes after an object.
 *
 * @throws [SerializationException] if the given NBT input cannot be deserialized to the value of type [T].
 * @throws [okio.IOException] If an I/O error occurs and source can't be read from.
 */
@ExperimentalNbtApi
public fun <T> Nbt.decodeFromBufferedSource(
    deserializer: DeserializationStrategy<T>,
    source: BufferedSource
): T =
    decodeFromNbtReader(BinaryNbtReader(this, source), deserializer)

/**
 * Deserializes the contents of given [source] to the value of type [T] using
 * deserializer retrieved from the reified type parameter.
 *
 * Note that this functions expects that exactly one object would be present in the stream
 * and throws an exception if there are any dangling bytes after an object.
 *
 * @throws [SerializationException] if the given NBT input cannot be deserialized to the value of type [T].
 * @throws [okio.IOException] If an I/O error occurs and source can't be read from.
 */
@ExperimentalNbtApi
public inline fun <reified T> Nbt.decodeFromBufferedSource(source: BufferedSource): T =
    decodeFromBufferedSource(serializersModule.serializer(), source)


///**
// * Transforms the given [source] into lazily deserialized sequence of elements of type [T] using [deserializer].
// * Unlike [decodeFromBufferedSource], [source] is allowed to have more than one element, separated as [format] declares.
// *
// * Elements must all be of type [T].
// * Elements are parsed lazily when resulting [Sequence] is evaluated.
// * Resulting sequence is tied to the stream and can be evaluated only once.
// *
// * **Resource caution:** this method neither closes the [source] when the parsing is finished nor provides a method to close it manually.
// * It is a caller responsibility to hold a reference to a source and close it. Moreover, because source is parsed lazily,
// * closing it before returned sequence is evaluated completely will result in [Exception] from decoder.
// *
// * @throws [SerializationException] if the given NBT input cannot be deserialized to the value of type [T].
// * @throws [okio.IOException] If an I/O error occurs and source can't be read from.
// */
//@ExperimentalSerializationApi
//public fun <T> Nbt.decodeBufferedSourceToSequence(
//    source: BufferedSource,
//    deserializer: DeserializationStrategy<T>,
//    format: DecodeSequenceMode = DecodeSequenceMode.AUTO_DETECT
//): Sequence<T> {
//    return decodeToSequenceByReader(OkioSerialReader(source), deserializer, format)
//}
//
///**
// * Transforms the given [source] into lazily deserialized sequence of elements of type [T] using UTF-8 encoding and deserializer retrieved from the reified type parameter.
// * Unlike [decodeFromBufferedSource], [source] is allowed to have more than one element, separated as [format] declares.
// *
// * Elements must all be of type [T].
// * Elements are parsed lazily when resulting [Sequence] is evaluated.
// * Resulting sequence is tied to the stream and constrained to be evaluated only once.
// *
// * **Resource caution:** this method does not close [source] when the parsing is finished neither provides method to close it manually.
// * It is a caller responsibility to hold a reference to a source and close it. Moreover, because source is parsed lazily,
// * closing it before returned sequence is evaluated fully would result in [Exception] from decoder.
// *
// * @throws [SerializationException] if the given NBT input cannot be deserialized to the value of type [T].
// * @throws [okio.IOException] If an I/O error occurs and source can't be read from.
// */
//@ExperimentalSerializationApi
//public inline fun <reified T> Nbt.decodeBufferedSourceToSequence(
//    source: BufferedSource,
//    format: DecodeSequenceMode = DecodeSequenceMode.AUTO_DETECT
//): Sequence<T> = decodeBufferedSourceToSequence(source, serializersModule.serializer(), format)
