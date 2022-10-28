package net.benwoodworth.knbt.internal

import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import kotlinx.serialization.serializer
import kotlin.test.Test

class NbtSerialDiscriminatorTest {
    @Test
    fun discriminating_list_kind_given_a_builtin_descriptor_should_resolve_correctly() {
        table(
            headers("type", "serializer", "expected kind"),

            row("List<Byte>", serializer<List<Byte>>(), NbtListKind.List),
            row("List<Int>", serializer<List<Int>>(), NbtListKind.List),
            row("List<Long>", serializer<List<Long>>(), NbtListKind.List),

            row("Array<Byte>", serializer<Array<Byte>>(), NbtListKind.List),
            row("Array<Int>", serializer<Array<Int>>(), NbtListKind.List),
            row("Array<Long>", serializer<Array<Long>>(), NbtListKind.List),

            row("ByteArray", serializer<ByteArray>(), NbtListKind.ByteArray),
            row("IntArray", serializer<IntArray>(), NbtListKind.IntArray),
            row("LongArray", serializer<LongArray>(), NbtListKind.LongArray),
        ).forAll { type, serializer, expectedKind ->
            DefaultNbtSerialDiscriminator.discriminateListKind(serializer.descriptor)
                .shouldBe(expectedKind)
        }
    }
}
