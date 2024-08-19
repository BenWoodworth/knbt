package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.NothingSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import net.benwoodworth.knbt.*
import kotlin.test.assertEquals

data class SerializableTypeEdgeCase(
    val name: String,
    val baseDescriptor: SerialDescriptor,
    val encodeValue: Encoder.(valueDescriptor: SerialDescriptor) -> Unit,
    val decodeValue: Decoder.(valueDescriptor: SerialDescriptor) -> Unit,
    val valueTag: NbtTag
) {
    override fun toString(): String = name
}

fun SerializableTypeEdgeCase.serializer(descriptor: SerialDescriptor = baseDescriptor): KSerializer<Unit> =
    object : KSerializer<Unit> {
        override val descriptor: SerialDescriptor
            get() = descriptor

        override fun serialize(encoder: Encoder, value: Unit): Unit =
            encoder.encodeValue(descriptor)

        override fun deserialize(decoder: Decoder): Unit =
            decoder.decodeValue(descriptor)

        override fun equals(other: Any?): Boolean =
            other is KSerializer<*> && descriptor == other.descriptor

        override fun hashCode(): Int = descriptor.hashCode()
    }

/**
 * A serializer for each possible value serialization call in [NbtEncoder] and [NbtDecoder].
 *
 * Use in tests as the implementation for a [Unit] serializer
 */
internal fun ParameterizeScope.parameterOfSerializableTypeEdgeCases() = parameter {
    nbtTypes + basicTypes// + inlineTypes + elementTypes // TODO
}

@Serializable
private enum class Enum { Entry }

@Serializable
private class Structure

//@OptIn(ExperimentalSerializationApi::class)
//private val nullType = SerializableTypeEdgeCase(
//    "Null",
//    NothingSerializer().nullable,
//    { encodeNull()},
//    { decodeNull() },
//    null,
//)

private val nbtTypes = listOf(
    NbtByte(0),
    NbtShort(0),
    NbtInt(0),
    NbtLong(0),
    NbtFloat(0.0f),
    NbtDouble(0.0),
    NbtByteArray(emptyList()),
    NbtString(""),
    NbtList(emptyList()),
    NbtCompound(mapOf()),
    NbtIntArray(emptyList()),
    NbtLongArray(emptyList()),
).map { nbtTag ->
    SerializableTypeEdgeCase(
        "NbtTag (${nbtTag::class.simpleName})",
        NbtTag.serializer().descriptor,
        { asNbtEncoder().encodeNbtTag(nbtTag) },
        { asNbtDecoder().decodeNbtTag() },
        nbtTag
    )
}

@OptIn(ExperimentalSerializationApi::class)
private val basicTypes = listOf(
    SerializableTypeEdgeCase(
        "Boolean",
        Boolean.serializer().descriptor,
        { encodeBoolean(false) },
        { decodeBoolean() },
        NbtByte.fromBoolean(false),
    ),
    SerializableTypeEdgeCase(
        "Byte",
        Byte.serializer().descriptor,
        { encodeByte(0) },
        { decodeByte() },
        NbtByte(0),
    ),
    SerializableTypeEdgeCase(
        "Short",
        Short.serializer().descriptor,
        { encodeShort(0) },
        { decodeShort() },
        NbtShort(0),
    ),
    SerializableTypeEdgeCase(
        "Char",
        Char.serializer().descriptor,
        { encodeChar('a') },
        { decodeChar() },
        NbtString("a"),
    ),
    SerializableTypeEdgeCase(
        "Int",
        Int.serializer().descriptor,
        { encodeInt(0) },
        { decodeInt() },
        NbtInt(0),
    ),
    SerializableTypeEdgeCase(
        "Long",
        Long.serializer().descriptor,
        { encodeLong(0) },
        { decodeLong() },
        NbtLong(0),
    ),
    SerializableTypeEdgeCase(
        "Float",
        Float.serializer().descriptor,
        { encodeFloat(0.0f) },
        { decodeFloat() },
        NbtFloat(0.0f),
    ),
    SerializableTypeEdgeCase(
        "Double",
        Double.serializer().descriptor,
        { encodeDouble(0.0) },
        { decodeDouble() },
        NbtDouble(0.0),
    ),
    SerializableTypeEdgeCase(
        "String",
        String.serializer().descriptor,
        { encodeString("") },
        { decodeString() },
        NbtString(""),
    ),
//    SerializableTypeEdgeCase(
//        "Enum",
//        Enum.serializer().descriptor,
//        { descriptor -> encodeEnum(descriptor, Enum.Entry.ordinal) },
//        { descriptor -> decodeEnum(descriptor) },
//        NbtInt(Enum.Entry.ordinal)
//    ),
    SerializableTypeEdgeCase(
        "Structure",
        Structure.serializer().descriptor,
        { descriptor -> encodeStructure(descriptor) {} },
        { descriptor ->
            decodeStructure(descriptor) {
                decodeElementIndex(descriptor)
                    .also { assertEquals(DECODE_DONE, it, "decodeElementIndex(...)") }
            }
        },
        buildNbtCompound {}
    ),
    SerializableTypeEdgeCase(
        "Collection (non-sequentially)",
        ListSerializer(NothingSerializer()).descriptor,
        { descriptor -> encodeCollection(descriptor, 0) {} },
        { descriptor ->
            decodeStructure(descriptor) {
                decodeCollectionSize(descriptor)
                decodeElementIndex(descriptor)
                    .also { assertEquals(DECODE_DONE, it, "decodeElementIndex(...)") }
            }
        },
        NbtList(emptyList())
    ),
    SerializableTypeEdgeCase(
        "Collection (sequentially, if supported)",
        ListSerializer(NothingSerializer()).descriptor,
        { descriptor -> encodeCollection(descriptor, 0) {} },
        { descriptor ->
            decodeStructure(descriptor) {
                if (decodeSequentially()) {
                    decodeCollectionSize(descriptor)
                        .also { assertEquals(0, it, "decodeCollectionSize(...)") }

                    // No elements to decode
                } else {
                    decodeCollectionSize(descriptor)
                    decodeElementIndex(descriptor)
                        .also { assertEquals(DECODE_DONE, it, "decodeElementIndex(...)") }
                }
            }
        },
        NbtList(emptyList())
    ),
)

// TODO
//@Serializable
//@JvmInline
//private value class Inline<out T>(val element: T)
//
//@Suppress("UNCHECKED_CAST")
//private val inlineTypes = basicTypes.map { basicType ->
//    SerializableTypeEdgeCase(
//        "Inline ${basicType.name}",
//        Inline.serializer(basicType.serializer) as KSerializer<Inline<Any>>, // KT-68606: Remove cast
//        Inline(basicType.value),
//        buildNbtCompound { put("element", basicType.tag) }
//    )
//}
//
//
//@Serializable
//private class Element<out T>(val element: T)
//
//@Suppress("UNCHECKED_CAST")
//private val elementTypes = (basicTypes + inlineTypes).map { unnestedType ->
//    SerializableTypeEdgeCase(
//        "${unnestedType.name} Element",
//        Element.serializer(unnestedType.serializer) as KSerializer<Element<Any>>, // KT-68606: Remove cast
//        Element(unnestedType.value),
//        buildNbtCompound { put("element", unnestedType.tag) }
//    )
//}
