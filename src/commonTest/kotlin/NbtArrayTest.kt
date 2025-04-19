package net.benwoodworth.knbt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import net.benwoodworth.knbt.test.serializers.ListSerializerWithAnnotations
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtArrayTest {
    private inline fun <reified T> listAsNbtArraySerializer(): KSerializer<List<T>> =
        ListSerializerWithAnnotations(serializer(), listOf(NbtArray()))

    @Test
    fun should_serialize_List_to_NbtByteArray_according_to_the_descriptor_NbtType() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            listAsNbtArraySerializer(),
            listOf<Byte>(1, 2, 3),
            NbtByteArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_List_to_NbtIntArray_according_to_the_descriptor_NbtType() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            listAsNbtArraySerializer(),
            listOf(1, 2, 3),
            NbtIntArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_List_to_NbtLongArray_according_to_the_descriptor_NbtType() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            listAsNbtArraySerializer(),
            listOf(1L, 2L, 3L),
            NbtLongArray(listOf(1L, 2L, 3L)),
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun class_property_marked_as_NbtArray_should_serialize_Byte_List_as_NbtByteArray() = parameterizeTest {
        @Serializable
        @NbtName("Class")
        data class Class(@NbtArray val property: List<Byte>)

        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            Class.serializer(),
            Class(listOf(123)),
            buildNbtCompound("Class") {
                put("property", byteArrayOf(123))
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun class_property_marked_as_NbtArray_should_serialize_Int_List_as_NbtIntArray() = parameterizeTest {
        @Serializable
        @NbtName("Class")
        data class Class(@NbtArray val property: List<Int>)

        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            Class.serializer(),
            Class(listOf(123)),
            buildNbtCompound("Class") {
                put("property", intArrayOf(123))
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun class_property_marked_as_NbtArray_should_serialize_Long_List_as_NbtLongArray() = parameterizeTest {
        @Serializable
        @NbtName("Class")
        data class Class(@NbtArray val property: List<Long>)

        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            Class.serializer(),
            Class(listOf(123)),
            buildNbtCompound("Class") {
                put("property", longArrayOf(123))
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun list_element_marked_as_NbtArray_should_serialize_Byte_List_as_NbtByteArray() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ListSerializer(listAsNbtArraySerializer()),
            listOf(listOf(123.toByte())),
            buildNbtList<NbtByteArray> {
                add(byteArrayOf(123))
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun list_element_marked_as_NbtArray_should_serialize_Int_List_as_NbtIntArray() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ListSerializer(listAsNbtArraySerializer()),
            listOf(listOf(123)),
            buildNbtList<NbtIntArray> {
                add(intArrayOf(123))
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun list_element_marked_as_NbtArray_should_serialize_Long_List_as_NbtLongArray() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ListSerializer(listAsNbtArraySerializer()),
            listOf(listOf(123.toLong())),
            buildNbtList<NbtLongArray> {
                add(longArrayOf(123))
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }
}
