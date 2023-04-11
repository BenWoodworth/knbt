package net.benwoodworth.knbt.internal
import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.*
import net.benwoodworth.knbt.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SerialDescriptorTest {
    @OptIn(ExperimentalSerializationApi::class)
    private fun SerialDescriptor.asNbtArray(): SerialDescriptor =
        object : SerialDescriptor by this {
            override val annotations: List<Annotation> = this@asNbtArray.annotations + NbtArray()
        }


    private fun test(expectedKind: NbtListKind, descriptor: SerialDescriptor): Unit =
        assertEquals(expectedKind, descriptor.nbtListKind)

    private fun testElement(expectedKind: NbtListKind, elementDescriptor: SerialDescriptor) {
        val descriptor = buildClassSerialDescriptor("Parent", elementDescriptor) {
            element("element", elementDescriptor)
        }

        assertEquals(expectedKind, descriptor.getElementNbtListKind(0))
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun testNbtArrayElement(expectedKind: NbtListKind, elementDescriptor: SerialDescriptor) {
        val descriptor = buildClassSerialDescriptor("Parent", elementDescriptor) {
            element("element", elementDescriptor, listOf(NbtArray()))
        }

        assertEquals(expectedKind, descriptor.getElementNbtListKind(0))
    }


    @Test
    fun kind_for_ByteArray_should_be_ByteArray(): Unit =
        test(NbtListKind.ByteArray, ByteArraySerializer().descriptor)

    @Test
    fun kind_for_element_ByteArray_should_be_ByteArray(): Unit =
        testElement(NbtListKind.ByteArray, ByteArraySerializer().descriptor)


    @Test
    fun kind_for_IntArray_should_be_IntArray(): Unit =
        test(NbtListKind.IntArray, IntArraySerializer().descriptor)

    @Test
    fun kind_for_element_IntArray_should_be_IntArray(): Unit =
        testElement(NbtListKind.IntArray, IntArraySerializer().descriptor)


    @Test
    fun kind_for_LongArray_should_be_LongArray(): Unit =
        test(NbtListKind.LongArray, LongArraySerializer().descriptor)

    @Test
    fun kind_for_element_LongArray_should_be_LongArray(): Unit =
        testElement(NbtListKind.LongArray, LongArraySerializer().descriptor)


    @Test
    fun kind_for_Byte_List_should_be_List(): Unit =
        test(NbtListKind.List, serializer<List<Byte>>().descriptor)

    @Test
    fun kind_for_element_Byte_List_should_be_List(): Unit =
        testElement(NbtListKind.List, serializer<List<Byte>>().descriptor)

    @Test
    fun kind_for_NbtArray_element_Byte_List_should_be_ByteArray(): Unit =
        testNbtArrayElement(NbtListKind.ByteArray, serializer<List<Byte>>().descriptor)


    @Test
    fun kind_for_Int_List_should_be_List(): Unit =
        test(NbtListKind.List, serializer<List<Int>>().descriptor)

    @Test
    fun kind_for_element_Int_List_should_be_List(): Unit =
        testElement(NbtListKind.List, serializer<List<Int>>().descriptor)

    @Test
    fun kind_for_NbtArray_element_Int_List_should_be_IntArray(): Unit =
        testNbtArrayElement(NbtListKind.IntArray, serializer<List<Int>>().descriptor)


    @Test
    fun kind_for_Long_List_should_be_List(): Unit =
        test(NbtListKind.List, serializer<List<Long>>().descriptor)

    @Test
    fun kind_for_element_Long_List_should_be_List(): Unit =
        testElement(NbtListKind.List, serializer<List<Long>>().descriptor)

    @Test
    fun kind_for_NbtArray_element_Long_List_should_be_LongArray(): Unit =
        testNbtArrayElement(NbtListKind.LongArray, serializer<List<Long>>().descriptor)


    @Test
    fun kind_for_Byte_NbtArrayList_should_be_ByteArray(): Unit =
        test(NbtListKind.ByteArray, serializer<List<Byte>>().descriptor.asNbtArray())

    @Test
    fun kind_for_element_Byte_NbtArrayList_should_be_ByteArray(): Unit =
        testElement(NbtListKind.ByteArray, serializer<List<Byte>>().descriptor.asNbtArray())


    @Test
    fun kind_for_Int_NbtArrayList_should_be_IntArray(): Unit =
        test(NbtListKind.IntArray, serializer<List<Int>>().descriptor.asNbtArray())

    @Test
    fun kind_for_element_Int_NbtArrayList_should_be_IntArray(): Unit =
        testElement(NbtListKind.IntArray, serializer<List<Int>>().descriptor.asNbtArray())


    @Test
    fun kind_for_Long_NbtArrayList_should_be_LongArray(): Unit =
        test(NbtListKind.LongArray, serializer<List<Long>>().descriptor.asNbtArray())

    @Test
    fun kind_for_element_Long_NbtArrayList_should_be_LongArray(): Unit =
        testElement(NbtListKind.LongArray, serializer<List<Long>>().descriptor.asNbtArray())


    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    private val nbtArrayDescriptorWithZeroElements = buildSerialDescriptor("ListDescriptorWithZeroElements", StructureKind.LIST) {
        annotations = listOf(NbtArray())
    }

    @Test
    fun getting_kind_for_NbtArray_descriptor_should_throw_SerializationException_if_it_has_zero_elements() {
        assertFailsWith<NbtException> {
            nbtArrayDescriptorWithZeroElements.nbtListKind
        }
    }

    @Test
    fun getting_kind_for_NbtArray_element_descriptor_should_throw_SerializationException_if_it_has_zero_elements() {
        val descriptor = buildClassSerialDescriptor("MyClass") {
            element("0", nbtArrayDescriptorWithZeroElements)
        }

        assertFailsWith<NbtException> {
            descriptor.getElementNbtListKind(0)
        }
    }
}
