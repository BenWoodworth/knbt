package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

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

    override fun serialize(encoder: Encoder, value: NbtByteArray) {
        val composite = encoder.asNbtEncoder().beginList(descriptor, value.size)
        value.forEachIndexed { index, element ->
            composite.encodeByteElement(descriptor, index, element)
        }
        composite.endStructure(descriptor)
    }

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
        @ExperimentalSerializationApi
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
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.NbtCompound"
    }
}

internal object NbtIntArraySerializer : KSerializer<NbtIntArray> {
    private object NbtIntArrayDescriptor : SerialDescriptor by serialDescriptor<IntArray>() {
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.NbtIntArray"
    }

    override val descriptor: SerialDescriptor = NbtIntArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtIntArray) {
        val composite = encoder.asNbtEncoder().beginList(descriptor, value.size)
        value.forEachIndexed { index, element ->
            composite.encodeIntElement(descriptor, index, element)
        }
        composite.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): NbtIntArray =
        NbtIntArray(decoder.asNbtDecoder().decodeIntArray())
}

internal object NbtLongArraySerializer : KSerializer<NbtLongArray> {
    private object NbtLongArrayDescriptor : SerialDescriptor by serialDescriptor<LongArray>() {
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.NbtLongArray"
    }

    override val descriptor: SerialDescriptor = NbtLongArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtLongArray) {
        val composite = encoder.asNbtEncoder().beginList(descriptor, value.size)
        value.forEachIndexed { index, element ->
            composite.encodeLongElement(NbtIntArraySerializer.descriptor, index, element)
        }
        composite.endStructure(NbtIntArraySerializer.descriptor)
    }

    override fun deserialize(decoder: Decoder): NbtLongArray =
        NbtLongArray(decoder.asNbtDecoder().decodeLongArray())
}
