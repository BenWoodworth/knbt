package net.benwoodworth.knbt.internal

import io.kotest.assertions.withClue
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import kotlinx.serialization.serializer
import kotlin.test.Test

class NbtSerialDiscriminatorTest {
    @Test
    fun discriminating_list_kind_from_a_builtin_List_descriptor_should_resolve_to_NBT_List() = table(
        headers("type", "serializer", "expected kind"),

        row("List<Byte>", serializer<List<Byte>>(), NbtListKind.List),
        row("List<Int>", serializer<List<Int>>(), NbtListKind.List),
        row("List<Long>", serializer<List<Long>>(), NbtListKind.List),
    ).forAll { type, serializer, expectedKind ->
        val listKind = DefaultNbtSerialDiscriminator.discriminateListKind(serializer.descriptor)

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
        val listKind = DefaultNbtSerialDiscriminator.discriminateListKind(serializer.descriptor)

        withClue("$type serializer") {
            listKind.shouldBe(expectedKind)
        }
    }

    @Test
    fun discriminating_list_kind_from_a_builtin_ByteArray_descriptor_should_resolve_to_NBT_ByteArray() {
        val serializer = serializer<ByteArray>()
        val listKind = DefaultNbtSerialDiscriminator.discriminateListKind(serializer.descriptor)

        listKind.shouldBe(NbtListKind.ByteArray)
    }

    @Test
    fun discriminating_list_kind_from_a_builtin_IntArray_descriptor_should_resolve_to_NBT_IntArray() {
        val serializer = serializer<IntArray>()
        val listKind = DefaultNbtSerialDiscriminator.discriminateListKind(serializer.descriptor)

        listKind.shouldBe(NbtListKind.IntArray)
    }

    @Test
    fun discriminating_list_kind_from_a_builtin_LongArray_descriptor_should_resolve_to_NBT_LongArray() {
        val serializer = serializer<LongArray>()
        val listKind = DefaultNbtSerialDiscriminator.discriminateListKind(serializer.descriptor)

        listKind.shouldBe(NbtListKind.LongArray)
    }
}
