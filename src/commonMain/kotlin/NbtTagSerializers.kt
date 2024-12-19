package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object NbtTagSerializer : KSerializer<NbtTag> {
    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("net.benwoodworth.knbt.NbtTag", PolymorphicKind.SEALED) {
            // Resolve cyclic dependency in descriptors by late binding
            element("NbtByteSerializer", defer { NbtByteSerializer.descriptor })
            element("NbtShortSerializer", defer { NbtShortSerializer.descriptor })
            element("NbtIntSerializer", defer { NbtIntSerializer.descriptor })
            element("NbtLongSerializer", defer { NbtLongSerializer.descriptor })
            element("NbtFloatSerializer", defer { NbtFloatSerializer.descriptor })
            element("NbtDoubleSerializer", defer { NbtDoubleSerializer.descriptor })
            element("NbtByteArraySerializer", defer { NbtByteArraySerializer.descriptor })
            element("NbtStringSerializer", defer { NbtStringSerializer.descriptor })
            element("NbtListSerializer", defer { NbtListSerializer(NbtTagSerializer).descriptor })
            element("NbtCompoundSerializer", defer { NbtCompoundSerializer.descriptor })
            element("NbtIntArraySerializer", defer { NbtIntArraySerializer.descriptor })
            element("NbtLongArraySerializer", defer { NbtLongArraySerializer.descriptor })
        }

    override fun serialize(encoder: Encoder, value: NbtTag): Unit = when (value) {
        is NbtByte -> encoder.asNbtEncoder().encodeSerializableValue(NbtByteSerializer, value)
        is NbtShort -> encoder.asNbtEncoder().encodeSerializableValue(NbtShortSerializer, value)
        is NbtInt -> encoder.asNbtEncoder().encodeSerializableValue(NbtIntSerializer, value)
        is NbtLong -> encoder.asNbtEncoder().encodeSerializableValue(NbtLongSerializer, value)
        is NbtFloat -> encoder.asNbtEncoder().encodeSerializableValue(NbtFloatSerializer, value)
        is NbtDouble -> encoder.asNbtEncoder().encodeSerializableValue(NbtDoubleSerializer, value)
        is NbtByteArray -> encoder.asNbtEncoder().encodeSerializableValue(NbtByteArraySerializer, value)
        is NbtString -> encoder.asNbtEncoder().encodeSerializableValue(NbtStringSerializer, value)
        is NbtList<*> -> encoder.asNbtEncoder().encodeSerializableValue(NbtListSerializer(this), value)
        is NbtCompound -> encoder.asNbtEncoder().encodeSerializableValue(NbtCompoundSerializer, value)
        is NbtIntArray -> encoder.asNbtEncoder().encodeSerializableValue(NbtIntArraySerializer, value)
        is NbtLongArray -> encoder.asNbtEncoder().encodeSerializableValue(NbtLongArraySerializer, value)
    }

    override fun deserialize(decoder: Decoder): NbtTag =
        decoder.asNbtDecoder().decodeNbtTag()
}

internal object NbtByteSerializer : KSerializer<NbtByte> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtByte", PrimitiveKind.BYTE)

    override fun serialize(encoder: Encoder, value: NbtByte): Unit =
        encoder.asNbtEncoder().encodeByte(value.value)

    override fun deserialize(decoder: Decoder): NbtByte =
        NbtByte(decoder.asNbtDecoder().decodeByte())
}

internal object NbtShortSerializer : KSerializer<NbtShort> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtShort", PrimitiveKind.SHORT)

    override fun serialize(encoder: Encoder, value: NbtShort): Unit =
        encoder.asNbtEncoder().encodeShort(value.value)

    override fun deserialize(decoder: Decoder): NbtShort =
        NbtShort(decoder.asNbtDecoder().decodeShort())
}

internal object NbtIntSerializer : KSerializer<NbtInt> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtInt", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: NbtInt): Unit =
        encoder.asNbtEncoder().encodeInt(value.value)

    override fun deserialize(decoder: Decoder): NbtInt =
        NbtInt(decoder.asNbtDecoder().decodeInt())
}

internal object NbtLongSerializer : KSerializer<NbtLong> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtLong", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: NbtLong): Unit =
        encoder.asNbtEncoder().encodeLong(value.value)

    override fun deserialize(decoder: Decoder): NbtLong =
        NbtLong(decoder.asNbtDecoder().decodeLong())
}

internal object NbtFloatSerializer : KSerializer<NbtFloat> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtFloat", PrimitiveKind.FLOAT)

    override fun serialize(encoder: Encoder, value: NbtFloat): Unit =
        encoder.asNbtEncoder().encodeFloat(value.value)

    override fun deserialize(decoder: Decoder): NbtFloat =
        NbtFloat(decoder.asNbtDecoder().decodeFloat())
}

internal object NbtDoubleSerializer : KSerializer<NbtDouble> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtDouble", PrimitiveKind.DOUBLE)

    override fun serialize(encoder: Encoder, value: NbtDouble): Unit =
        encoder.asNbtEncoder().encodeDouble(value.value)

    override fun deserialize(decoder: Decoder): NbtDouble =
        NbtDouble(decoder.asNbtDecoder().decodeDouble())
}

internal object NbtByteArraySerializer : KSerializer<NbtByteArray> {
    private object NbtByteArrayDescriptor : SerialDescriptor by serialDescriptor<ByteArray>() {
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.NbtByteArray"
    }

    override val descriptor: SerialDescriptor = NbtByteArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtByteArray): Unit =
        encoder.asNbtEncoder().encodeByteArray(value.content)

    override fun deserialize(decoder: Decoder): NbtByteArray =
        NbtByteArray(decoder.asNbtDecoder().decodeByteArray())
}

internal object NbtStringSerializer : KSerializer<NbtString> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("net.benwoodworth.knbt.NbtString", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: NbtString): Unit =
        encoder.asNbtEncoder().encodeString(value.value)

    override fun deserialize(decoder: Decoder): NbtString =
        NbtString(decoder.asNbtDecoder().decodeString())
}

internal class NbtListSerializer<T : NbtTag>(
    elementSerializer: KSerializer<T>,
) : KSerializer<NbtList<T>> {
    override val descriptor: SerialDescriptor = NbtListDescriptor(elementSerializer.descriptor)
    private val listSerializer = ListSerializer(elementSerializer)

    override fun serialize(encoder: Encoder, value: NbtList<T>): Unit =
        encoder.asNbtEncoder().encodeNbtTag(value)

    @OptIn(UnsafeNbtApi::class)
    override fun deserialize(decoder: Decoder): NbtList<T> =
        NbtList(listSerializer.deserialize(decoder))

    @OptIn(ExperimentalSerializationApi::class)
    private class NbtListDescriptor(
        val elementDescriptor: SerialDescriptor,
    ) : SerialDescriptor by listSerialDescriptor(elementDescriptor) {
        override val serialName: String = "net.benwoodworth.knbt.NbtList"
    }
}

internal object NbtCompoundSerializer : KSerializer<NbtCompound> {
    override val descriptor: SerialDescriptor = NbtCompoundDescriptor(NbtTag.serializer().descriptor)
    private val mapSerializer = MapSerializer(String.serializer(), NbtTag.serializer())

    override fun serialize(encoder: Encoder, value: NbtCompound): Unit =
        encoder.asNbtEncoder().encodeNbtTag(value)

    override fun deserialize(decoder: Decoder): NbtCompound {
        val map = mapSerializer.deserialize(decoder)
        return NbtCompound(map)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private class NbtCompoundDescriptor(
        val elementDescriptor: SerialDescriptor,
    ) : SerialDescriptor by mapSerialDescriptor(String.serializer().descriptor, elementDescriptor) {
        override val serialName: String = "net.benwoodworth.knbt.NbtCompound"
    }
}

internal object NbtIntArraySerializer : KSerializer<NbtIntArray> {
    @OptIn(ExperimentalSerializationApi::class)
    private object NbtIntArrayDescriptor : SerialDescriptor by serialDescriptor<IntArray>() {
        override val serialName: String = "net.benwoodworth.knbt.NbtIntArray"
    }

    override val descriptor: SerialDescriptor = NbtIntArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtIntArray): Unit =
        encoder.asNbtEncoder().encodeIntArray(value.content)

    override fun deserialize(decoder: Decoder): NbtIntArray =
        NbtIntArray(decoder.asNbtDecoder().decodeIntArray())
}

internal object NbtLongArraySerializer : KSerializer<NbtLongArray> {
    @OptIn(ExperimentalSerializationApi::class)
    private object NbtLongArrayDescriptor : SerialDescriptor by serialDescriptor<LongArray>() {
        override val serialName: String = "net.benwoodworth.knbt.NbtLongArray"
    }

    override val descriptor: SerialDescriptor = NbtLongArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtLongArray): Unit =
        encoder.asNbtEncoder().encodeLongArray(value.content)

    override fun deserialize(decoder: Decoder): NbtLongArray =
        NbtLongArray(decoder.asNbtDecoder().decodeLongArray())
}


/**
 * Returns serial descriptor that delegates all the calls to descriptor returned by [deferred] block.
 * Used to resolve cyclic dependencies between recursive serializable structures.
 */
@OptIn(ExperimentalSerializationApi::class)
private fun defer(deferred: () -> SerialDescriptor): SerialDescriptor = object : SerialDescriptor {
    private val original: SerialDescriptor by lazy(deferred)

    override val serialName: String
        get() = original.serialName
    override val kind: SerialKind
        get() = original.kind
    override val elementsCount: Int
        get() = original.elementsCount

    override fun getElementName(index: Int): String = original.getElementName(index)
    override fun getElementIndex(name: String): Int = original.getElementIndex(name)
    override fun getElementAnnotations(index: Int): List<Annotation> = original.getElementAnnotations(index)
    override fun getElementDescriptor(index: Int): SerialDescriptor = original.getElementDescriptor(index)
    override fun isElementOptional(index: Int): Boolean = original.isElementOptional(index)
}
