package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

internal object NbtTagSerializer : KSerializer<NbtTag> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("net.benwoodworth.knbt.NbtTag")

    override fun serialize(encoder: Encoder, value: NbtTag): Unit =
        encoder.asNbtEncoder().encodeNbtTag(value)

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
    @OptIn(SealedSerializationApi::class)
    private object NbtByteArrayDescriptor : SerialDescriptor by ListSerializer(Byte.serializer()).descriptor {
        override val serialName: String = "net.benwoodworth.knbt.NbtByteArray"
        override val annotations: List<Annotation> = listOf(NbtArray())
    }

    override val descriptor: SerialDescriptor = NbtByteArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtByteArray): Unit =
        encoder.asNbtEncoder().encodeCollection(descriptor, value.content) { index, element ->
            encodeByteElement(descriptor, index, element)
        }

    override fun deserialize(decoder: Decoder): NbtByteArray =
        NbtByteArray(decoder.decodeList(descriptor, CompositeDecoder::decodeByteElement))
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
        listSerializer.deserialize(decoder)
            .let { NbtList(it.firstOrNull()?.type ?: NbtTagType.TAG_End, it) }

    @OptIn(ExperimentalSerializationApi::class, SealedSerializationApi::class)
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

    @OptIn(ExperimentalSerializationApi::class, SealedSerializationApi::class)
    private class NbtCompoundDescriptor(
        val elementDescriptor: SerialDescriptor,
    ) : SerialDescriptor by mapSerialDescriptor(String.serializer().descriptor, elementDescriptor) {
        override val serialName: String = "net.benwoodworth.knbt.NbtCompound"
    }
}

internal object NbtIntArraySerializer : KSerializer<NbtIntArray> {
    @OptIn(SealedSerializationApi::class)
    private object NbtIntArrayDescriptor : SerialDescriptor by ListSerializer(Int.serializer()).descriptor {
        override val serialName: String = "net.benwoodworth.knbt.NbtIntArray"
        override val annotations: List<Annotation> = listOf(NbtArray())
    }

    override val descriptor: SerialDescriptor = NbtIntArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtIntArray): Unit =
        encoder.asNbtEncoder().encodeCollection(descriptor, value.content) { index, element ->
            encodeIntElement(descriptor, index, element)
        }

    override fun deserialize(decoder: Decoder): NbtIntArray =
        NbtIntArray(decoder.decodeList(descriptor, CompositeDecoder::decodeIntElement))
}

internal object NbtLongArraySerializer : KSerializer<NbtLongArray> {
    @OptIn(SealedSerializationApi::class)
    private object NbtLongArrayDescriptor : SerialDescriptor by ListSerializer(Long.serializer()).descriptor {
        override val serialName: String = "net.benwoodworth.knbt.NbtLongArray"
        override val annotations: List<Annotation> = listOf(NbtArray())
    }

    override val descriptor: SerialDescriptor = NbtLongArrayDescriptor

    override fun serialize(encoder: Encoder, value: NbtLongArray): Unit =
        encoder.asNbtEncoder().encodeCollection(descriptor, value.content) { index, element ->
            encodeLongElement(descriptor, index, element)
        }

    override fun deserialize(decoder: Decoder): NbtLongArray =
        NbtLongArray(decoder.decodeList(descriptor, CompositeDecoder::decodeLongElement))
}

internal fun Encoder.asNbtEncoder(): NbtEncoder =
    this as? NbtEncoder ?: throw IllegalArgumentException(
        "This serializer can be used only with NBT format. Expected Encoder to be NbtEncoder, got ${this::class}"
    )

internal fun Decoder.asNbtDecoder(): NbtDecoder =
    this as? NbtDecoder ?: throw IllegalArgumentException(
        "This serializer can be used only with NBT format. Expected Decoder to be NbtDecoder, got ${this::class}"
    )

@OptIn(ExperimentalSerializationApi::class)
private inline fun <T> Decoder.decodeList(
    descriptor: SerialDescriptor,
    crossinline decodeElement: CompositeDecoder.(descriptor: SerialDescriptor, index: Int) -> T
): List<T> = decodeStructure(descriptor) {
    val size = decodeCollectionSize(NbtLongArraySerializer.descriptor)

    when {
        decodeSequentially() -> List(size) { index ->
            decodeElement(descriptor, index)
        }

        size >= 0 -> buildList(size) {
            while (true) {
                val index = decodeElementIndex(descriptor)
                if (index == CompositeDecoder.DECODE_DONE) break

                add(index, decodeElement(descriptor, index))
            }
        }

        else -> buildList {
            while (true) {
                val index = decodeElementIndex(descriptor)
                if (index == CompositeDecoder.DECODE_DONE) break

                add(index, decodeElement(descriptor, index))
            }
        }
    }
}
