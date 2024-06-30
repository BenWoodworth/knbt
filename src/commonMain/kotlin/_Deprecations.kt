@file:Suppress("UNUSED_PARAMETER")

package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.internal.EmptyNbtContext
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.*

@Deprecated("For organizing deprecations")
public sealed interface NbtDeprecations {
    /**
     * Serializes and encodes the given [value] to the [sink] using the given [serializer].
     *
     * *Note*: It is the caller's responsibility to close the [sink].
     */
    @OkioApi
    @Deprecated(
        "Replaced by encodeToBufferedSink()",
        ReplaceWith(
            "this.encodeToBufferedSink<T>(serializer, value, sink.buffer())",
            "net.benwoodworth.knbt.okio.encodeToBufferedSink",
            "okio.buffer"
        ),
        DeprecationLevel.ERROR
    )
    public fun <T> encodeToSink(serializer: SerializationStrategy<T>, value: T, sink: Sink): Unit =
        (this as Nbt).encodeToBufferedSink(serializer, value, sink.buffer())

    /**
     * Serializes and encodes the given [value] to the [sink] using the given [serializer].
     *
     * *Note*: It is the caller's responsibility to close the [sink].
     */
    @OkioApi
    @Deprecated(
        "Replaced by encodeToBufferedSink()",
        ReplaceWith(
            "this.encodeToBufferedSink<T>(serializer, value, sink)",
            "net.benwoodworth.knbt.okio.encodeToBufferedSink"
        ),
        DeprecationLevel.ERROR
    )
    public fun <T> encodeToSink(serializer: SerializationStrategy<T>, value: T, sink: BufferedSink): Unit =
        (this as Nbt).encodeToBufferedSink(serializer, value, sink)

    /**
     * Decodes and deserializes from the given [source] to a value of type [T] using the given [deserializer].
     *
     * *Note*: It is the caller's responsibility to close the [source].
     */
    @OkioApi
    @Deprecated(
        "Replaced by decodeFromBufferedSource()",
        ReplaceWith(
            "this.decodeFromBufferedSource<T>(deserializer, source.buffer())",
            "net.benwoodworth.knbt.okio.decodeFromBufferedSource",
            "okio.buffer"
        ),
        DeprecationLevel.ERROR
    )
    public fun <T> decodeFromSource(deserializer: DeserializationStrategy<T>, source: Source): T =
        (this as Nbt).decodeFromBufferedSource(deserializer, source.buffer())

    /**
     * Decodes and deserializes from the given [source] to a value of type [T] using the given [deserializer].
     *
     * *Note*: It is the caller's responsibility to close the [source].
     */
    @OkioApi
    @Deprecated(
        "Replaced by decodeFromBufferedSource()",
        ReplaceWith(
            "this.decodeFromBufferedSource<T>(deserializer, source)",
            "net.benwoodworth.knbt.okio.decodeFromBufferedSource"
        ),
        DeprecationLevel.ERROR
    )
    public fun <T> decodeFromSource(deserializer: DeserializationStrategy<T>, source: BufferedSource): T =
        (this as Nbt).decodeFromBufferedSource(deserializer, source)
}

/**
 * Encode NBT to a [Sink].
 *
 * *Note*: It is the caller's responsibility to close the [sink].
 */
@OkioApi
@Deprecated(
    "Replaced by encodeToBufferedSink()",
    ReplaceWith(
        "this.encodeToBufferedSink<T>(value, sink.buffer())",
        "net.benwoodworth.knbt.okio.encodeToBufferedSink",
        "okio.buffer"
    ),
    DeprecationLevel.ERROR
)
public inline fun <reified T> Nbt.encodeToSink(value: T, sink: Sink): Unit =
    encodeToBufferedSink(value, sink.buffer())

/**
 * Encode NBT to a [Sink].
 *
 * *Note*: It is the caller's responsibility to close the [sink].
 */
@OkioApi
@Deprecated(
    "Replaced by encodeToBufferedSink()",
    ReplaceWith(
        "this.encodeToBufferedSink<T>(value, sink)",
        "net.benwoodworth.knbt.okio.encodeToBufferedSink"
    ),
    DeprecationLevel.ERROR
)
public inline fun <reified T> Nbt.encodeToSink(value: T, sink: BufferedSink): Unit =
    encodeToBufferedSink(value, sink)

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
@Deprecated(
    "Replaced by decodeFromBufferedSource()",
    ReplaceWith(
        "this.decodeFromBufferedSource<T>(source.buffer())",
        "net.benwoodworth.knbt.okio.decodeFromBufferedSource",
        "okio.buffer"
    ),
    DeprecationLevel.ERROR
)
public inline fun <reified T> Nbt.decodeFromSource(source: Source): T =
    decodeFromBufferedSource(source.buffer())

/**
 * Decode NBT from a [Source].
 *
 * *Note*: It is the caller's responsibility to close the [source].
 */
@OkioApi
@Deprecated(
    "Replaced by decodeFromBufferedSource()",
    ReplaceWith(
        "this.decodeFromBufferedSource<T>(source)",
        "net.benwoodworth.knbt.okio.decodeFromBufferedSource"
    ),
    DeprecationLevel.ERROR
)
public inline fun <reified T> Nbt.decodeFromSource(source: BufferedSource): T =
    decodeFromBufferedSource(source)


/**
 * Peek in the [source] and detect what [NbtCompression] is used.
 *
 * @throws NbtDecodingException when unable to detect NbtCompression.
 */
@OkioApi
@Deprecated(
    "Moved to okio package",
    ReplaceWith(
        "this.detect(source)",
        "net.benwoodworth.knbt.okio.detect"
    ),
    DeprecationLevel.ERROR
)
@Suppress("UNUSED_PARAMETER") // The `deprecated` parameter lowers the overload precedence so the relocated function takes priority when replaced
public fun NbtCompression.Companion.detect(source: BufferedSource, deprecated: Nothing? = null): NbtCompression =
    detect(EmptyNbtContext, source.peek().readByte())


@Deprecated("For organizing deprecations")
public sealed interface NbtEncoderDeprecations : Encoder, CompositeEncoder {
    @Deprecated(
        "Removed in favor of `encodeSerializableValue()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableValue(NbtByteArray.serializer(), NbtByteArray(value.asList()))",
            "net.benwoodworth.knbt.NbtByteArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun encodeByteArray(value: ByteArray, deprecated: Nothing? = null): Unit =
        encodeSerializableValue(NbtByteArray.serializer(), NbtByteArray(value.asList()))

    @Deprecated(
        "Removed in favor of `encodeSerializableValue()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableValue(NbtIntArray.serializer(), NbtIntArray(value.asList()))",
            "net.benwoodworth.knbt.NbtIntArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun encodeIntArray(value: IntArray, deprecated: Nothing? = null): Unit =
        encodeSerializableValue(NbtIntArray.serializer(), NbtIntArray(value.asList()))

    @Deprecated(
        "Removed in favor of `encodeSerializableValue()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableValue(NbtLongArray.serializer(), NbtLongArray(value.asList()))",
            "net.benwoodworth.knbt.NbtLongArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun encodeLongArray(value: LongArray, deprecated: Nothing? = null): Unit =
        encodeSerializableValue(NbtLongArray.serializer(), NbtLongArray(value.asList()))

    @Deprecated(
        "Removed in favor of `beginStructure()`",
        ReplaceWith(
            "(beginStructure(descriptor) as NbtEncoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtEncoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginCompound(descriptor: SerialDescriptor, deprecated: Nothing? = null): NbtEncoder =
        beginStructure(descriptor) as NbtEncoder

    @Deprecated(
        "Removed in favor of `beginCollection()`",
        ReplaceWith(
            "(beginCollection(descriptor, size) as NbtEncoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtEncoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginList(descriptor: SerialDescriptor, size: Int, deprecated: Nothing? = null): NbtEncoder =
        beginCollection(descriptor, size) as NbtEncoder

    @Deprecated(
        "Removed in favor of `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "(beginCollection(descriptor /* TODO: add @NbtArray */, size) as NbtEncoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtEncoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginByteArray(descriptor: SerialDescriptor, size: Int, deprecated: Nothing? = null): NbtEncoder =
        beginCollection(descriptor, size) as NbtEncoder

    @Deprecated(
        "Removed in favor of `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "(beginCollection(descriptor /* TODO: add @NbtArray */, size) as NbtEncoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtEncoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginIntArray(descriptor: SerialDescriptor, size: Int, deprecated: Nothing? = null): NbtEncoder =
        beginCollection(descriptor, size) as NbtEncoder

    @Deprecated(
        "Removed in favor of `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "(beginCollection(descriptor /* TODO: add @NbtArray */, size) as NbtEncoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtEncoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginLongArray(descriptor: SerialDescriptor, size: Int, deprecated: Nothing? = null): NbtEncoder =
        beginCollection(descriptor, size) as NbtEncoder

    @Deprecated(
        "Removed in favor of `encodeSerializableElement()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableElement(descriptor, index, NbtByteArraySerializer, NbtByteArray(value))",
            "net.benwoodworth.knbt.NbtByteArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun encodeByteArrayElement(descriptor: SerialDescriptor, index: Int, value: ByteArray): Unit =
        encodeSerializableElement(descriptor, index, NbtByteArray.serializer(), NbtByteArray(value.asList()))

    @Deprecated(
        "Removed in favor of `encodeSerializableElement()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableElement(descriptor, index, NbtIntArray.serializer(), NbtIntArray(value.asList()))",
            "net.benwoodworth.knbt.NbtIntArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun encodeIntArrayElement(descriptor: SerialDescriptor, index: Int, value: IntArray): Unit =
        encodeSerializableElement(descriptor, index, NbtIntArray.serializer(), NbtIntArray(value.asList()))

    @Deprecated(
        "Removed in favor of `encodeSerializableElement()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableElement(descriptor, index, NbtLongArray.serializer(), NbtLongArray(value.asList()))",
            "net.benwoodworth.knbt.NbtLongArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun encodeLongArrayElement(descriptor: SerialDescriptor, index: Int, value: LongArray): Unit =
        encodeSerializableElement(descriptor, index, NbtLongArray.serializer(), NbtLongArray(value.asList()))

    @Deprecated(
        "Removed in favor of `encodeSerializableElement()` or `encodeNbtTag()`",
        ReplaceWith(
            "encodeSerializableElement(descriptor, index, NbtTag.serializer(), value.asList())",
            "net.benwoodworth.knbt.NbtTag"
        ),
        DeprecationLevel.ERROR
    )
    public fun encodeNbtTagElement(descriptor: SerialDescriptor, index: Int, value: NbtTag): Unit =
        encodeSerializableElement(descriptor, index, NbtTag.serializer(), value)
}

@Deprecated(
    "Removed in order to match kotlinx-serialization-json's API",
    ReplaceWith(
        "this as NbtEncoder",
        "net.benwoodworth.knbt.NbtEncoder",
    ),
    DeprecationLevel.ERROR
)
public fun Encoder.asNbtEncoder(deprecated: Nothing? = null): NbtEncoder =
    this as? NbtEncoder ?: throw IllegalArgumentException(
        "This serializer can be used only with NBT format. Expected Encoder to be NbtEncoder, got ${this::class}"
    )

@Deprecated(
    "Merged into `NbtEncoder` in order to match kotlinx-serialization-json's API",
    ReplaceWith("NbtEncoder", "net.benwoodworth.knbt.NbtEncoder"),
    DeprecationLevel.ERROR
)
public typealias CompositeNbtEncoder = NbtEncoder


@Deprecated("For organizing deprecations")
public sealed interface NbtDecoderDeprecations : Decoder, CompositeDecoder {
    @Deprecated(
        "Removed in favor of `decodeSerializableValue()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableValue(NbtByteArray.serializer()).content.toByteArray()",
            "net.benwoodworth.knbt.NbtByteArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun decodeByteArray(deprecated: Nothing? = null): ByteArray =
        decodeSerializableValue(NbtByteArray.serializer()).content.toByteArray()

    @Deprecated(
        "Removed in favor of `decodeSerializableValue()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableValue(NbtIntArray.serializer()).content.toIntArray()",
            "net.benwoodworth.knbt.NbIntArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun decodeIntArray(deprecated: Nothing? = null): IntArray =
        decodeSerializableValue(NbtIntArray.serializer()).content.toIntArray()

    @Deprecated(
        "Removed in favor of `decodeSerializableValue()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableValue(NbtLongArray.serializer()).content.toLongArray()",
            "net.benwoodworth.knbt.NbtLongArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun decodeLongArray(deprecated: Nothing? = null): LongArray =
        decodeSerializableValue(NbtLongArray.serializer()).content.toLongArray()

    @Deprecated(
        "Removed in favor of `beginStructure()`",
        ReplaceWith(
            "(beginStructure(descriptor) as NbtDecoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtDecoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginCompound(descriptor: SerialDescriptor, deprecated: Nothing? = null): NbtDecoder =
        beginStructure(descriptor) as NbtDecoder

    @Deprecated(
        "Removed in favor of `beginStructure()`",
        ReplaceWith(
            "(beginStructure(descriptor) as NbtDecoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtDecoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginList(descriptor: SerialDescriptor, deprecated: Nothing? = null): NbtDecoder =
        beginStructure(descriptor) as NbtDecoder

    @Deprecated(
        "Removed in favor of `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "(beginStructure(descriptor /* TODO: add @NbtArray */) as NbtDecoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtDecoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginByteArray(descriptor: SerialDescriptor, deprecated: Nothing? = null): NbtDecoder =
        beginStructure(descriptor) as NbtDecoder

    @Deprecated(
        "Removed in favor of `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "(beginStructure(descriptor /* TODO: add @NbtArray */) as NbtDecoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtDecoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginIntArray(descriptor: SerialDescriptor, deprecated: Nothing? = null): NbtDecoder =
        beginStructure(descriptor) as NbtDecoder

    @Deprecated(
        "Removed in favor of `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "(beginStructure(descriptor /* TODO: add @NbtArray */) as NbtDecoder /* TODO: remove cast */)",
            "net.benwoodworth.knbt.NbtDecoder"
        ),
        DeprecationLevel.ERROR
    )
    public fun beginLongArray(descriptor: SerialDescriptor, deprecated: Nothing? = null): NbtDecoder =
        beginStructure(descriptor) as NbtDecoder

    @Deprecated(
        "Removed in favor of `decodeSerializableElement()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableElement(descriptor, index, NbtByteArray.serializer()).content.toByteArray()",
            "net.benwoodworth.knbt.NbtByteArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int): ByteArray =
        decodeSerializableElement(descriptor, index, NbtByteArray.serializer()).content.toByteArray()

    @Deprecated(
        "Removed in favor of `decodeSerializableElement()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableElement(descriptor, index, NbtIntArray.serializer()).content.toIntArray()",
            "net.benwoodworth.knbt.NbtIntArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun decodeIntArrayElement(descriptor: SerialDescriptor, index: Int): IntArray =
        decodeSerializableElement(descriptor, index, NbtIntArray.serializer()).content.toIntArray()

    @Deprecated(
        "Removed in favor of `decodeSerializableElement()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableElement(descriptor, index, NbtLongArray.serializer()).content.toLongArray()",
            "net.benwoodworth.knbt.NbtLongArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun decodeLongArrayElement(descriptor: SerialDescriptor, index: Int): LongArray =
        decodeSerializableElement(descriptor, index, NbtLongArray.serializer()).content.toLongArray()

    @Deprecated(
        "Removed in favor of `decodeSerializableElement()` or `encodeNbtTag()`",
        ReplaceWith(
            "decodeSerializableElement(descriptor, index, NbtTag.serializer())",
            "net.benwoodworth.knbt.NbtTag"
        ),
        DeprecationLevel.ERROR
    )
    public fun decodeNbtTagElement(descriptor: SerialDescriptor, index: Int): NbtTag =
        decodeSerializableElement(descriptor, index, NbtTag.serializer())
}

@Deprecated(
    "Removed in order to match kotlinx-serialization-json's API",
    ReplaceWith(
        "this as NbtDecoder",
        "net.benwoodworth.knbt.NbtDecoder",
    ),
    DeprecationLevel.ERROR
)
public fun Decoder.asNbtDecoder(deprecated: Nothing? = null): NbtDecoder =
    this as? NbtDecoder ?: throw IllegalArgumentException(
        "This serializer can be used only with NBT format. Expected Decoder to be NbtDecoder, got ${this::class}"
    )

@Deprecated(
    "Merged into `NbtDecoder` in order to match kotlinx-serialization-json's API",
    ReplaceWith("NbtDecoder", "net.benwoodworth.knbt.NbtDecoder"),
    DeprecationLevel.ERROR
)
public typealias CompositeNbtDecoder = NbtDecoder


/**
 * Create an [NbtByte] containing a [Boolean]: `false = 0b`, `true = 1b`
 */
@Deprecated(
    "Replaced by NbtByte.fromBoolean(...)",
    ReplaceWith(
        "NbtByte.fromBoolean(booleanValue)",
        "net.benwoodworth.knbt.NbtByte",
        "net.benwoodworth.knbt.fromBoolean"
    ),
    DeprecationLevel.ERROR
)
public fun NbtByte(booleanValue: Boolean): NbtByte =
    NbtByte(if (booleanValue) 1 else 0)

@Deprecated("For organizing deprecations")
public sealed interface NbtByteDeprecations {
    /**
     * Get an [NbtByte] as a [Boolean]: `0b = false`, `1b = true`
     * @throws IllegalArgumentException if this is not `0b` or `1b`
     */
    @Deprecated(
        "Replaced by NbtByte.toBoolean(), which more leniently converts NbtByte values",
        ReplaceWith(
            "this.toBoolean()",
            "net.benwoodworth.knbt.toBoolean"
        ),
        DeprecationLevel.ERROR
    )
    public val booleanValue: Boolean
        get() = when ((this as NbtByte).value) {
            0.toByte() -> false
            1.toByte() -> true
            else -> throw IllegalArgumentException("Expected value to be a boolean (0 or 1), but was $value")
        }
}

@Deprecated(
    "Content is now a `List<Byte>` instead of a `ByteArray`",
    ReplaceWith("NbtByteArray(content.asList())", "net.benwoodworth.knbt.NbtByteArray"),
    DeprecationLevel.ERROR
)
public fun NbtByteArray(content: ByteArray): NbtByteArray =
    NbtByteArray(content.asList())

@Deprecated(
    "Content is now a `List<Int>` instead of a `IntArray`",
    ReplaceWith("NbtIntArray(content.asList())", "net.benwoodworth.knbt.NbtIntArray"),
    DeprecationLevel.ERROR
)
public fun NbtIntArray(content: IntArray): NbtIntArray =
    NbtIntArray(content.asList())

@Deprecated(
    "Content is now a `List<Long>` instead of a `LongArray`",
    ReplaceWith("NbtLongArray(content.asList())", "net.benwoodworth.knbt.NbtLongArray"),
    DeprecationLevel.ERROR
)
public fun NbtLongArray(content: LongArray): NbtLongArray =
    NbtLongArray(content.asList())
