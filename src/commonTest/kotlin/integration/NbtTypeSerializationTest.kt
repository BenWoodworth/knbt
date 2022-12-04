package net.benwoodworth.knbt.integration

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.*
import net.benwoodworth.knbt.test.serializers.ListSerializerWithAnnotations
import net.benwoodworth.knbt.test.serializers.NothingSerializer
import kotlin.test.Test

@OptIn(ExperimentalSerializationApi::class)
class NbtTypeSerializationTest : SerializationTest() {
    private inline fun <reified T, reified TNbt : NbtTag> listSerializer(): KSerializer<List<T>> =
        ListSerializerWithAnnotations(serializer(), listOf(NbtType(TNbt::class)))

    private data class ListType<T>(
        val serializer: KSerializer<T>,
        val empty: T,
        val emptyTag: NbtTag,
        val asList: (T) -> List<*>,
    )

    private val ListType<*>.nbtType: NbtType
        get() = NbtType(emptyTag::class)

    private val listTypes = listOf(
        ListType(serializer(), listOf<String>(), NbtList(emptyList<NbtString>())) { it },
        ListType(serializer(), byteArrayOf(), NbtByteArray(byteArrayOf())) { it.asList() },
        ListType(serializer(), intArrayOf(), NbtIntArray(intArrayOf())) { it.asList() },
        ListType(serializer(), longArrayOf(), NbtLongArray(longArrayOf())) { it.asList() },
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

    private data class NbtTypeProperty<T>(val property: T)

    private class NbtTypePropertySerializer<T>(
        private val propertyNbtType: NbtType,
        private val propertySerializer: KSerializer<T>,
    ) : KSerializer<NbtTypeProperty<T>> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor("NbtTypeProperty", propertySerializer.descriptor) {
                element(NbtTypeProperty<T>::property.name, propertySerializer.descriptor, listOf(propertyNbtType))
            }

        override fun serialize(encoder: Encoder, value: NbtTypeProperty<T>): Unit =
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(descriptor, 0, propertySerializer, value.property)
            }

        override fun deserialize(decoder: Decoder): NbtTypeProperty<T> =
            decoder.decodeStructure(descriptor) {
                check(decodeElementIndex(descriptor) == 0)
                val property = decodeSerializableElement(descriptor, 0, propertySerializer)
                check(decodeElementIndex(descriptor) == CompositeDecoder.DECODE_DONE)
                NbtTypeProperty(property)
            }
    }

    @Test
    fun class_property_with_NbtType_should_serialize_according_to_that_type() {
        fun <T> test(valueType: ListType<T>, propertyType: ListType<*>): Unit =
            defaultNbt.testSerialization(
                serializer = NbtTypePropertySerializer(propertyType.nbtType, valueType.serializer),
                value = NbtTypeProperty(valueType.empty),
                nbtTag = buildNbtCompound("NbtTypeProperty") {
                    put("property", propertyType.emptyTag)
                },
                compareBy = { valueType.asList(it.property) },
            )

        listTypes.forEach { propertyType ->
            listTypes.forEach { valueType ->
                test(valueType, propertyType)
            }
        }
    }

    @Test
    fun class_property_NbtType_should_take_priority_over_the_element_descriptor_NbtType() {
        fun test(elementDescriptorType: ListType<*>, propertyType: ListType<*>): Unit =
            defaultNbt.testSerialization(
                serializer = NbtTypePropertySerializer(
                    propertyType.nbtType,
                    ListSerializerWithAnnotations(NothingSerializer, listOf(elementDescriptorType.nbtType)),
                ),
                value = NbtTypeProperty(emptyList()),
                nbtTag = buildNbtCompound("NbtTypeProperty") {
                    put("property", propertyType.emptyTag)
                },
            )

        listTypes.forEach { propertyType ->
            listTypes.forEach { elementDescriptorType ->
                test(elementDescriptorType, propertyType)
            }
        }
    }

    @Test
    fun class_with_NbtType_property_followed_by_non_NbtType_property() {
        @Serializable
        @SerialName("TestClass")
        data class TestClass(
            @NbtType(NbtByteArray::class)
            val propertyWithNbtType: List<Byte>,

            val propertyWithoutNbtType: List<Byte>,
        )

        defaultNbt.testSerialization(
            value = TestClass(
                propertyWithNbtType = listOf(1, 2),
                propertyWithoutNbtType = listOf(3, 4)
            ),
            nbtTag = buildNbtCompound("TestClass") {
                put("propertyWithNbtType", byteArrayOf(1, 2))
                putNbtList<NbtByte>("propertyWithoutNbtType") { add(3); add(4) }
            }
        )
    }

    @Test
    fun class_with_nested_NbtType_property_followed_by_non_NbtType_property() {
        @Serializable
        data class Nested(
            @NbtType(NbtByteArray::class)
            val propertyWithNbtType: List<Byte>,
        )

        @Serializable
        @SerialName("TestClass")
        data class TestClass(
            val nested: Nested,
            val propertyWithoutNbtType: List<Byte>,
        )

        defaultNbt.testSerialization(
            value = TestClass(
                nested = Nested(
                    propertyWithNbtType = listOf(1, 2)
                ),
                propertyWithoutNbtType = listOf(3, 4)
            ),
            nbtTag = buildNbtCompound("TestClass") {
                putNbtCompound("nested") {
                    put("propertyWithNbtType", byteArrayOf(1, 2))
                }
                putNbtList<NbtByte>("propertyWithoutNbtType") { add(3); add(4) }
            }
        )
    }
}
