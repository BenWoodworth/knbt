package net.benwoodworth.knbt.integration

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.*
import net.benwoodworth.knbt.test.serializers.ListSerializerWithAnnotations
import kotlin.test.Test

@OptIn(ExperimentalSerializationApi::class)
class NbtTypeSerializationTest : SerializationTest() {
    private inline fun <reified T, reified TNbt : NbtTag> listSerializer(): KSerializer<List<T>> =
        ListSerializerWithAnnotations(serializer(), listOf(NbtType(TNbt::class)))

    private object ListAsNbtByteArraySerializer : KSerializer<List<Byte>> by ListSerializerWithAnnotations(
        elementSerializer = Byte.serializer(),
        annotations = listOf(NbtType(NbtByteArray::class))
    )

    @Test
    fun should_serialize_List_to_NbtByteArray_according_to_the_descriptor_NbtType() {
        defaultNbt.testSerialization(
            listSerializer<_, NbtByteArray>(),
            listOf<Byte>(1, 2, 3),
            NbtByteArray(byteArrayOf(1, 2, 3))
        )
    }

    @Test
    fun should_serialize_List_to_NbtIntArray_according_to_the_descriptor_NbtType() {
        defaultNbt.testSerialization(
            listSerializer<_, NbtIntArray>(),
            listOf(1, 2, 3),
            NbtIntArray(intArrayOf(1, 2, 3))
        )
    }

    @Test
    fun should_serialize_List_to_NbtLongArray_according_to_the_descriptor_NbtType() {
        defaultNbt.testSerialization(
            listSerializer<_, NbtLongArray>(),
            listOf(1L, 2L, 3L),
            NbtLongArray(longArrayOf(1L, 2L, 3L))
        )
    }

    @Test
    fun class_property_marked_as_NbtList_should_serialize_as_NbtList() {
        @Serializable
        @SerialName("Class")
        class Class(
            @NbtType(NbtList::class)
            val property: ByteArray
        )

        defaultNbt.testSerialization(
            value = Class(byteArrayOf(123)),
            nbtTag = buildNbtCompound("Class") {
                putNbtList<NbtByte>("property") {
                    add(123)
                }
            },
            compareBy = { it.property.asList() },
        )
    }

    @Test
    fun class_property_marked_as_NbtByteArray_should_serialize_as_NbtByteArray() {
        @Serializable
        @SerialName("Class")
        data class Class(
            @NbtType(NbtByteArray::class)
            val property: List<Byte>
        )

        defaultNbt.testSerialization(
            value = Class(listOf(123)),
            nbtTag = buildNbtCompound("Class") {
                put("property", byteArrayOf(123))
            }
        )
    }

    @Test
    fun class_property_marked_as_NbtIntArray_should_serialize_as_NbtIntArray() {
        @Serializable
        @SerialName("Class")
        data class Class(
            @NbtType(NbtIntArray::class)
            val property: List<Int>
        )

        defaultNbt.testSerialization(
            value = Class(listOf(123)),
            nbtTag = buildNbtCompound("Class") {
                put("property", intArrayOf(123))
            }
        )
    }

    @Test
    fun class_property_marked_as_NbtLongArray_should_serialize_as_NbtLongArray() {
        @Serializable
        @SerialName("Class")
        data class Class(
            @NbtType(NbtLongArray::class)
            val property: List<Long>
        )

        defaultNbt.testSerialization(
            value = Class(listOf(123)),
            nbtTag = buildNbtCompound("Class") {
                put("property", longArrayOf(123))
            }
        )
    }

    @Test
    fun class_property_NbtType_should_take_priority_over_its_value_NbtType() {
        @Serializable
        @SerialName("Class")
        data class Class(
            @NbtType(NbtIntArray::class)
            @Serializable(ListAsNbtByteArraySerializer::class)
            val property: List<Byte>
        )

        defaultNbt.testSerialization(
            value = Class(listOf()),
            nbtTag = buildNbtCompound("Class") {
                put("property", intArrayOf())
            }
        )
    }

    @Test
    fun list_element_marked_as_NbtList_should_serialize_as_NbtList() {
        defaultNbt.testSerialization(
            value = listOf(byteArrayOf(123)),
            nbtTag = buildNbtList<NbtList<*>> {
                addNbtList<NbtByte> {
                    add(123)
                }
            },
            serializer = ListSerializerWithAnnotations(
                elementSerializer = serializer(),
                elementAnnotations = listOf(NbtType(NbtList::class))
            ),
            compareBy = { it[0].asList() },
        )
    }

    @Test
    fun list_element_marked_as_NbtByteArray_should_serialize_as_NbtByteArray() {
        defaultNbt.testSerialization(
            value = listOf(listOf(123.toByte())),
            nbtTag = buildNbtList<NbtByteArray> {
                add(byteArrayOf(123))
            },
            serializer = ListSerializerWithAnnotations(
                elementSerializer = serializer(),
                elementAnnotations = listOf(NbtType(NbtByteArray::class))
            )
        )
    }

    @Test
    fun list_element_marked_as_NbtIntArray_should_serialize_as_NbtIntArray() {
        defaultNbt.testSerialization(
            value = listOf(listOf(123)),
            nbtTag = buildNbtList<NbtIntArray> {
                add(intArrayOf(123))
            },
            serializer = ListSerializerWithAnnotations(
                elementSerializer = serializer(),
                elementAnnotations = listOf(NbtType(NbtIntArray::class))
            )
        )
    }

    @Test
    fun list_element_marked_as_NbtLongArray_should_serialize_as_NbtLongArray() {
        defaultNbt.testSerialization(
            value = listOf(listOf(123.toLong())),
            nbtTag = buildNbtList<NbtLongArray> {
                add(longArrayOf(123))
            },
            serializer = ListSerializerWithAnnotations(
                elementSerializer = serializer(),
                elementAnnotations = listOf(NbtType(NbtLongArray::class))
            )
        )
    }

    @Test
    fun list_element_NbtType_should_take_priority_over_its_value_NbtType() {
        defaultNbt.testSerialization(
            value = listOf(listOf()),
            nbtTag = buildNbtList<NbtIntArray> {
                add(intArrayOf())
            },
            serializer = ListSerializerWithAnnotations(
                elementSerializer = ListAsNbtByteArraySerializer,
                elementAnnotations = listOf(NbtType(NbtIntArray::class))
            )
        )
    }
}
