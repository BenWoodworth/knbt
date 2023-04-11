package net.benwoodworth.knbt.serialization

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encoding.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.*
import net.benwoodworth.knbt.test.serializers.ListSerializerWithAnnotations
import kotlin.test.Test

class NbtArraySerializationTest : SerializationTest() {
    private inline fun <reified T> listAsNbtArraySerializer(): KSerializer<List<T>> =
        ListSerializerWithAnnotations(serializer(), listOf(NbtArray()))

    @Test
    fun should_serialize_List_to_NbtByteArray_according_to_the_descriptor_NbtType() {
        defaultNbt.testSerialization(
            listAsNbtArraySerializer(),
            listOf<Byte>(1, 2, 3),
            NbtByteArray(byteArrayOf(1, 2, 3))
        )
    }

    @Test
    fun should_serialize_List_to_NbtIntArray_according_to_the_descriptor_NbtType() {
        defaultNbt.testSerialization(
            listAsNbtArraySerializer(),
            listOf(1, 2, 3),
            NbtIntArray(intArrayOf(1, 2, 3))
        )
    }

    @Test
    fun should_serialize_List_to_NbtLongArray_according_to_the_descriptor_NbtType() {
        defaultNbt.testSerialization(
            listAsNbtArraySerializer(),
            listOf(1L, 2L, 3L),
            NbtLongArray(longArrayOf(1L, 2L, 3L))
        )
    }

    @Test
    fun class_property_marked_as_NbtArray_should_serialize_Byte_List_as_NbtByteArray() {
        @Serializable
        @NbtNamed("Class")
        data class Class(@NbtArray val property: List<Byte>)

        defaultNbt.testSerialization(
            value = Class(listOf(123)),
            nbtTag = buildNbtCompound("Class") {
                put("property", byteArrayOf(123))
            }
        )
    }

    @Test
    fun class_property_marked_as_NbtArray_should_serialize_Int_List_as_NbtIntArray() {
        @Serializable
        @NbtNamed("Class")
        data class Class(@NbtArray val property: List<Int>)

        defaultNbt.testSerialization(
            value = Class(listOf(123)),
            nbtTag = buildNbtCompound("Class") {
                put("property", intArrayOf(123))
            }
        )
    }

    @Test
    fun class_property_marked_as_NbtArray_should_serialize_Long_List_as_NbtLongArray() {
        @Serializable
        @NbtNamed("Class")
        data class Class(@NbtArray val property: List<Long>)

        defaultNbt.testSerialization(
            value = Class(listOf(123)),
            nbtTag = buildNbtCompound("Class") {
                put("property", longArrayOf(123))
            }
        )
    }

    @Test
    fun list_element_marked_as_NbtArray_should_serialize_Byte_List_as_NbtByteArray() {
        defaultNbt.testSerialization(
            value = listOf(listOf(123.toByte())),
            nbtTag = buildNbtList<NbtByteArray> {
                add(byteArrayOf(123))
            },
            serializer = ListSerializer(listAsNbtArraySerializer())
        )
    }

    @Test
    fun list_element_marked_as_NbtArray_should_serialize_Int_List_as_NbtIntArray() {
        defaultNbt.testSerialization(
            value = listOf(listOf(123)),
            nbtTag = buildNbtList<NbtIntArray> {
                add(intArrayOf(123))
            },
            serializer = ListSerializer(listAsNbtArraySerializer())
        )
    }

    @Test
    fun list_element_marked_as_NbtArray_should_serialize_Long_List_as_NbtLongArray() {
        defaultNbt.testSerialization(
            value = listOf(listOf(123.toLong())),
            nbtTag = buildNbtList<NbtLongArray> {
                add(longArrayOf(123))
            },
            serializer = ListSerializer(listAsNbtArraySerializer())
        )
    }
}
