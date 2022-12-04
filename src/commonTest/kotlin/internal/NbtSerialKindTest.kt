package net.benwoodworth.knbt.internal

import io.kotest.assertions.withClue
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.*
import kotlin.test.Test

class NbtSerialDescriptorTest {
    @Test
    fun discriminating_list_kind_from_a_builtin_List_descriptor_should_resolve_to_NBT_List() = table(
        headers("type", "serializer", "expected kind"),

        row("List<Byte>", serializer<List<Byte>>(), NbtListKind.List),
        row("List<Int>", serializer<List<Int>>(), NbtListKind.List),
        row("List<Long>", serializer<List<Long>>(), NbtListKind.List),
    ).forAll { type, serializer, expectedKind ->
        val listKind = serializer.descriptor.nbtListKind

        withClue("$type serializer") {
            listKind.shouldBe(expectedKind)
        }
    }

    @Test
    fun discriminating_list_kind_from_a_builtin_generic_Array_descriptor_should_resolve_to_NBT_List() = table(
        headers("type", "serializer", "expected kind"),

        row("Array<Byte>", serializer<Array<Byte>>(), NbtListKind.List),
        row("Array<Int>", serializer<Array<Int>>(), NbtListKind.List),
        row("Array<Long>", serializer<Array<Long>>(), NbtListKind.List),
    ).forAll { type, serializer, expectedKind ->
        val listKind = serializer.descriptor.nbtListKind

        withClue("$type serializer") {
            listKind.shouldBe(expectedKind)
        }
    }

    @Test
    fun discriminating_list_kind_from_a_builtin_ByteArray_descriptor_should_resolve_to_NBT_ByteArray() {
        val serializer = serializer<ByteArray>()
        val listKind = serializer.descriptor.nbtListKind

        listKind.shouldBe(NbtListKind.ByteArray)
    }

    @Test
    fun discriminating_list_kind_from_a_builtin_IntArray_descriptor_should_resolve_to_NBT_IntArray() {
        val serializer = serializer<IntArray>()
        val listKind = serializer.descriptor.nbtListKind

        listKind.shouldBe(NbtListKind.IntArray)
    }

    @Test
    fun discriminating_list_kind_from_a_builtin_LongArray_descriptor_should_resolve_to_NBT_LongArray() {
        val serializer = serializer<LongArray>()
        val listKind = serializer.descriptor.nbtListKind

        listKind.shouldBe(NbtListKind.LongArray)
    }

    @Test
    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    fun discriminating_list_kind_with_NbtType_descriptor_annotation_should_use_the_annotation_type() = table(
        headers("NbtType", "expected kind"),

        row(NbtList::class, NbtListKind.List),
        row(NbtByteArray::class, NbtListKind.ByteArray),
        row(NbtIntArray::class, NbtListKind.IntArray),
        row(NbtLongArray::class, NbtListKind.LongArray),
    ).forAll { type, expectedKind ->
        val descriptor = buildSerialDescriptor("MyList", StructureKind.LIST) {
            annotations = listOf(NbtType(type))
        }

        val listKind = descriptor.nbtListKind
        listKind shouldBe expectedKind
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun discriminating_element_list_kind_with_NbtType_annotation_should_use_the_element_annotation_type() {
        @Serializable
        class ElementNbtListKinds(
            @NbtType(NbtIntArray::class) val listAsIntArray: List<Int>,
            @NbtType(NbtByteArray::class) val listAsByteArray: List<Byte>,
            @NbtType(NbtLongArray::class) val listAsLongArray: List<Long>,
            @NbtType(NbtList::class) val byteArrayAsList: ByteArray,
            @NbtType(NbtList::class) val intArrayAsList: IntArray,
            @NbtType(NbtList::class) val longArrayAsList: LongArray,
        )

        val descriptor = ElementNbtListKinds.serializer().descriptor

        table(
            headers("Element", "Expected List Kind"),

            row(ElementNbtListKinds::listAsIntArray.name, NbtListKind.IntArray),
            row(ElementNbtListKinds::listAsByteArray.name, NbtListKind.ByteArray),
            row(ElementNbtListKinds::listAsLongArray.name, NbtListKind.LongArray),
            row(ElementNbtListKinds::byteArrayAsList.name, NbtListKind.List),
            row(ElementNbtListKinds::intArrayAsList.name, NbtListKind.List),
            row(ElementNbtListKinds::longArrayAsList.name, NbtListKind.List),
        ).forAll { element, expectedListKind ->
            val elementIndex = descriptor.getElementIndex(element)

            val listKind = descriptor.getElementNbtListKind(elementIndex)
            listKind shouldBe expectedListKind
        }
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class, InternalSerializationApi::class)
    fun discriminating_element_list_kind_should_prefer_element_annotation_over_element_descriptor_annotation() = table(
        headers("Class Element NbtType", "Element NbtType", "Expected NbtListKind"),

        row(NbtList::class, NbtByteArray::class, NbtListKind.List),
        row(NbtList::class, NbtIntArray::class, NbtListKind.List),
        row(NbtList::class, NbtLongArray::class, NbtListKind.List),

        row(NbtByteArray::class, NbtList::class, NbtListKind.ByteArray),
        row(NbtIntArray::class, NbtList::class, NbtListKind.IntArray),
        row(NbtLongArray::class, NbtList::class, NbtListKind.LongArray),
    ).forAll { classElementType, elementType, expectedNbtListKind ->
        val elementDescriptor = buildSerialDescriptor("Element", StructureKind.LIST) {
            annotations = listOf(NbtType(elementType))
        }

        val classDescriptor = buildClassSerialDescriptor("Class") {
            element("element", elementDescriptor, listOf(NbtType(classElementType)))
        }

        val listKind = classDescriptor.getElementNbtListKind(0)
        listKind shouldBe expectedNbtListKind
    }
}
