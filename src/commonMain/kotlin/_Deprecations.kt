@file:Suppress("UNUSED_PARAMETER")

package net.benwoodworth.knbt

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.NbtEncodingException

@Deprecated("For organizing deprecations")
public sealed interface NbtEncoderDeprecations : Encoder, CompositeEncoder {
    @Deprecated(
        "Removed in favor of `encodeSerializableValue()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableValue(NbtByteArray.serializer(), NbtByteArray(value))",
            "net.benwoodworth.knbt.NbtByteArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun encodeByteArray(value: ByteArray, deprecated: Nothing? = null): Unit =
        encodeSerializableValue(NbtByteArray.serializer(), NbtByteArray(value))

    @Deprecated(
        "Removed in favor of `encodeSerializableValue()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableValue(NbtIntArray.serializer(), NbtIntArray(value))",
            "net.benwoodworth.knbt.NbtIntArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun encodeIntArray(value: IntArray, deprecated: Nothing? = null): Unit =
        encodeSerializableValue(NbtIntArray.serializer(), NbtIntArray(value))

    @Deprecated(
        "Removed in favor of `encodeSerializableValue()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableValue(NbtLongArray.serializer(), NbtLongArray(value))",
            "net.benwoodworth.knbt.NbtLongArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun encodeLongArray(value: LongArray, deprecated: Nothing? = null): Unit =
        encodeSerializableValue(NbtLongArray.serializer(), NbtLongArray(value))

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
        encodeSerializableElement(descriptor, index, NbtByteArray.serializer(), NbtByteArray(value))

    @Deprecated(
        "Removed in favor of `encodeSerializableElement()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableElement(descriptor, index, NbtIntArray.serializer(), NbtIntArray(value))",
            "net.benwoodworth.knbt.NbtIntArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun encodeIntArrayElement(descriptor: SerialDescriptor, index: Int, value: IntArray): Unit =
        encodeSerializableElement(descriptor, index, NbtIntArray.serializer(), NbtIntArray(value))

    @Deprecated(
        "Removed in favor of `encodeSerializableElement()`, or `beginCollection()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "encodeSerializableElement(descriptor, index, NbtLongArray.serializer(), NbtLongArray(value))",
            "net.benwoodworth.knbt.NbtLongArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun encodeLongArrayElement(descriptor: SerialDescriptor, index: Int, value: LongArray): Unit =
        encodeSerializableElement(descriptor, index, NbtLongArray.serializer(), NbtLongArray(value))

    @Deprecated(
        "Removed in favor of `encodeSerializableElement()` or `encodeNbtTag()`",
        ReplaceWith(
            "encodeSerializableElement(descriptor, index, NbtTag.serializer(), value)",
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
    this as? NbtEncoder ?: throw NbtEncodingException(
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
            "decodeSerializableValue(NbtByteArray.serializer()).toByteArray()",
            "net.benwoodworth.knbt.NbtByteArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun decodeByteArray(deprecated: Nothing? = null): ByteArray =
        decodeSerializableValue(NbtByteArray.serializer()).toByteArray()

    @Deprecated(
        "Removed in favor of `decodeSerializableValue()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableValue(NbtIntArray.serializer()).toIntArray()",
            "net.benwoodworth.knbt.NbIntArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun decodeIntArray(deprecated: Nothing? = null): IntArray =
        decodeSerializableValue(NbtIntArray.serializer()).toIntArray()

    @Deprecated(
        "Removed in favor of `decodeSerializableValue()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableValue(NbtLongArray.serializer()).toLongArray()",
            "net.benwoodworth.knbt.NbtLongArray"
        ),
        DeprecationLevel.ERROR,
    )
    public fun decodeLongArray(deprecated: Nothing? = null): LongArray =
        decodeSerializableValue(NbtLongArray.serializer()).toLongArray()

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
            "decodeSerializableElement(descriptor, index, NbtByteArray.serializer()).toByteArray()",
            "net.benwoodworth.knbt.NbtByteArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun decodeByteArrayElement(descriptor: SerialDescriptor, index: Int): ByteArray =
        decodeSerializableElement(descriptor, index, NbtByteArray.serializer()).toByteArray()

    @Deprecated(
        "Removed in favor of `decodeSerializableElement()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableElement(descriptor, index, NbtIntArray.serializer()).toIntArray()",
            "net.benwoodworth.knbt.NbtIntArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun decodeIntArrayElement(descriptor: SerialDescriptor, index: Int): IntArray =
        decodeSerializableElement(descriptor, index, NbtIntArray.serializer()).toIntArray()

    @Deprecated(
        "Removed in favor of `decodeSerializableElement()`, or `beginStructure()` with `@NbtArray` in the serial descriptor",
        ReplaceWith(
            "decodeSerializableElement(descriptor, index, NbtLongArray.serializer()).toLongArray()",
            "net.benwoodworth.knbt.NbtLongArray"
        ),
        DeprecationLevel.ERROR
    )
    public fun decodeLongArrayElement(descriptor: SerialDescriptor, index: Int): LongArray =
        decodeSerializableElement(descriptor, index, NbtLongArray.serializer()).toLongArray()

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
    this as? NbtDecoder ?: throw NbtDecodingException(
        "This serializer can be used only with NBT format. Expected Decoder to be NbtDecoder, got ${this::class}"
    )

@Deprecated(
    "Merged into `NbtDecoder` in order to match kotlinx-serialization-json's API",
    ReplaceWith("NbtDecoder", "net.benwoodworth.knbt.NbtDecoder"),
    DeprecationLevel.ERROR
)
public typealias CompositeNbtDecoder = NbtDecoder
