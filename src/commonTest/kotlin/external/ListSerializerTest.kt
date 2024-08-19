package net.benwoodworth.knbt.external

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeCollection
import net.benwoodworth.knbt.NbtList
import net.benwoodworth.knbt.UnsafeNbtApi
import net.benwoodworth.knbt.buildNbtList
import net.benwoodworth.knbt.test.decodeStructureAndElements
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfSerializableTypeEdgeCases
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import net.benwoodworth.knbt.test.parameters.serializer
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ListSerializerTest {
    @Test
    fun should_serialize_List_to_NbtList() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ListSerializer(Unit.serializer()),
            listOf(),
            NbtList(listOf()),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    @OptIn(UnsafeNbtApi::class)
    fun should_serialize_List_of_value_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val serializableType by parameterOfSerializableTypeEdgeCases()

        val serializer = object : KSerializer<Unit> {
            override val descriptor: SerialDescriptor = serializableType.baseDescriptor

            override fun serialize(encoder: Encoder, value: Unit): Unit =
                serializableType.encodeValue(encoder, descriptor)

            override fun deserialize(decoder: Decoder): Unit =
                serializableType.decodeValue(decoder, descriptor)
        }

        nbt.verifyEncoderOrDecoder(
            ListSerializer(serializer),
            listOf(Unit),
            NbtList(listOf(serializableType.valueTag)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    fun should_serialize_List_of_a_List_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        val listType by parameterOfSerializableTypeEdgeCases()

//        val serializer = object : KSerializer<Unit> {
//            override val descriptor: SerialDescriptor =
//                buildSerialDescriptor("ListOfDifferentLists", StructureKind.LIST) {
//                    element("0", list0Type.baseDescriptor)
//                }
//
//            override fun serialize(encoder: Encoder, value: Unit) {
//                encoder.encodeCollection(descriptor, 2) {
//                    encodeSerializableElement(descriptor, 0, list0Type.serializer(), Unit)
//                    encodeSerializableElement(descriptor, 1, list1Type.serializer(), Unit)
//                }
//            }
//
//            override fun deserialize(decoder: Decoder) {
//                decoder.decodeStructureAndElements(descriptor) { index ->
//                    when (index) {
//                        0 -> decoder.decodeSerializableValue(list0Type.serializer())
//                        1 -> decoder.decodeSerializableValue(list1Type.serializer())
//                        else -> error("Unexpected index: $index")
//                    }
//                }
//            }
//        }
        val serializer = ListSerializer(ListSerializer(listType.serializer()))

        @OptIn(UnsafeNbtApi::class)
        nbt.verifyEncoderOrDecoder(
            serializer,
            listOf(listOf(Unit), listOf(Unit)),
            buildNbtList {
                add(NbtList(listOf(listType.valueTag)))
                add(NbtList(listOf(listType.valueTag)))
            }
        )
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    fun should_serialize_List_of_differently_typed_Lists_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        val list0ElementType by parameterOfSerializableTypeEdgeCases()
        val list1ElementType by parameterOfSerializableTypeEdgeCases()

        val list0Serializer = ListSerializer(list0ElementType.serializer())
        val list1Serializer = ListSerializer(list1ElementType.serializer())

        val serializer = object : KSerializer<Unit> {
            override val descriptor: SerialDescriptor =
                buildSerialDescriptor("ListOfDifferentLists", StructureKind.LIST) {
                    element("0", list0Serializer.descriptor)
                    element("1", list1Serializer.descriptor)
                }

            override fun serialize(encoder: Encoder, value: Unit) {
                encoder.encodeCollection(descriptor, 2) {
                    encodeSerializableElement(descriptor, 0, list0Serializer, listOf(Unit))
                    encodeSerializableElement(descriptor, 1, list1Serializer, listOf(Unit))
                }
            }

            override fun deserialize(decoder: Decoder) {
                decoder.decodeStructureAndElements(descriptor) { index ->
                    when (index) {
                        0 -> decoder.decodeSerializableValue(list0Serializer)
                        1 -> decoder.decodeSerializableValue(list1Serializer)
                        else -> error("Unexpected index: $index")
                    }
                }
            }
        }

        @OptIn(UnsafeNbtApi::class)
        nbt.verifyEncoderOrDecoder(
            serializer,
            Unit,
            buildNbtList {
                add(NbtList(listOf(list0ElementType.valueTag)))
                add(NbtList(listOf(list1ElementType.valueTag)))
            }
        )
    }
}
