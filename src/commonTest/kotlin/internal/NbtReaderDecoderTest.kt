package net.benwoodworth.knbt.internal

import io.kotest.matchers.shouldBe
import kotlinx.serialization.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.data.bigTestClass
import net.benwoodworth.knbt.test.data.bigTestTag
import net.benwoodworth.knbt.test.data.testClass
import net.benwoodworth.knbt.test.data.testTag
import net.benwoodworth.knbt.test.verify.VerifyingNbtReader
import kotlin.math.PI
import kotlin.test.*

class NbtReaderDecoderTest {
    private inline fun <reified T> assertReadsCorrectly(expectedValue: T, nbtTag: NbtTag) {
        val reader = VerifyingNbtReader(TreeNbtReader(nbtTag))
        val decoder = NbtReaderDecoder(NbtFormat(), reader)
        val actualValue = decoder.decodeSerializableValue(serializer<T>())

        reader.assertComplete()
        actualValue shouldBe expectedValue
    }

    @Test
    fun should_read_TestNbt_class_correctly() {
        assertReadsCorrectly(
            expectedValue = testClass,
            nbtTag = testTag,
        )
    }

    @Test
    fun should_read_TestNbt_tag_correctly() {
        assertReadsCorrectly(
            expectedValue = testTag,
            nbtTag = testTag,
        )
    }

    @Test
    fun should_read_BigTestNbt_class_correctly() {
        assertReadsCorrectly(
            expectedValue = bigTestClass,
            nbtTag = bigTestTag,
        )
    }

    @Test
    fun should_read_BigTestNbt_tag_correctly() {
        assertReadsCorrectly(
            expectedValue = bigTestTag,
            nbtTag = bigTestTag,
        )
    }

    @Test
    fun should_read_compound_with_no_entries_to_Map_correctly() {
        assertReadsCorrectly(
            expectedValue = mapOf<String, Int>(),
            nbtTag = buildNbtCompound { },
        )
    }

    @Test
    fun should_read_compound_with_one_entry_to_Map_correctly() {
        assertReadsCorrectly(
            expectedValue = mapOf("property" to 7),
            nbtTag = buildNbtCompound {
                put("property", 7)
            },
        )
    }

    @Serializable
    @SerialName("OneProperty")
    private data class OneProperty<T>(val property: T)

    @Test
    fun should_read_compound_with_one_entry_to_class_correctly() {
        assertReadsCorrectly(
            expectedValue = OneProperty(7),
            nbtTag = buildNbtCompound("OneProperty") {
                put("property", 7)
            },
        )
    }

    @Test
    fun should_read_List_correctly() {
        assertReadsCorrectly(
            expectedValue = listOf(listOf(1.toByte()), listOf()),
            nbtTag = buildNbtList<NbtList<*>> {
                addNbtList<NbtByte> { add(1.toByte()) }
                addNbtList<Nothing> { }
            },
        )
    }

    @Test
    fun should_read_ByteArray_correctly() {
        assertReadsCorrectly(
            expectedValue = byteArrayOf(1, 2, 3),
            nbtTag = NbtByteArray(byteArrayOf(1, 2, 3)),
        )
    }

    @Test
    fun should_read_IntArray_correctly() {
        assertReadsCorrectly(
            expectedValue = intArrayOf(1, 2, 3),
            nbtTag = NbtIntArray(intArrayOf(1, 2, 3)),
        )
    }

    @Test
    fun should_read_LongArray_correctly() {
        assertReadsCorrectly(
            expectedValue = longArrayOf(1, 2, 3),
            nbtTag = NbtLongArray(longArrayOf(1, 2, 3)),
        )
    }

    @Test
    fun should_read_compound_with_two_entries_correctly() {
        @Serializable
        @SerialName("TwoProperties")
        data class TwoProperties(val entry1: String, val entry2: Long)

        assertReadsCorrectly(
            expectedValue = TwoProperties(entry1 = "value1", entry2 = 1234L),
            nbtTag = buildNbtCompound("TwoProperties") {
                put("entry1", "value1")
                put("entry2", 1234L)
            },
        )
    }

    @Test
    fun should_read_Byte_correctly() {
        assertReadsCorrectly(
            expectedValue = 4.toByte(),
            nbtTag = NbtByte(4),
        )
    }

    @Test
    fun should_read_Short_correctly() {
        assertReadsCorrectly(
            expectedValue = 5.toShort(),
            nbtTag = NbtShort(5),
        )
    }

    @Test
    fun should_read_Int_correctly() {
        assertReadsCorrectly(
            expectedValue = 6,
            nbtTag = NbtInt(6),
        )
    }

    @Test
    fun should_read_Long_correctly() {
        assertReadsCorrectly(
            expectedValue = 7L,
            nbtTag = NbtLong(7L),
        )
    }

    @Test
    fun should_read_Float_correctly() {
        assertReadsCorrectly(
            expectedValue = 3.14f,
            nbtTag = NbtFloat(3.14f),
        )
    }

    @Test
    fun should_read_Double_correctly() {
        assertReadsCorrectly(
            expectedValue = 3.14,
            nbtTag = NbtDouble(3.14),
        )
    }

    @Serializable
    @SerialName("unknown-keys")
    private data class UnknownKeys(
        val int: Int,
        val string: String,
    ) {
        companion object {
            val expected = UnknownKeys(int = 42, string = "String!")
        }
    }

    private val unknownKeysTag = buildNbtCompound("unknown-keys") {
        put("int", UnknownKeys.expected.int)
        put("double", PI)
        put("string", UnknownKeys.expected.string)
    }

    @Test
    fun decoding_should_fail_on_unknown_key_if_not_ignoring() {
        val nbt = NbtFormat(ignoreUnknownKeys = false)

        assertFailsWith<NbtDecodingException> {
            nbt.decodeFromNbtTag<UnknownKeys>(unknownKeysTag)
        }
    }

    @Test
    fun decoding_should_not_fail_on_unknown_key_if_ignoring() {
        val nbt = NbtFormat(ignoreUnknownKeys = true)

        val actual = nbt.decodeFromNbtTag<UnknownKeys>(unknownKeysTag)
        assertEquals(UnknownKeys.expected, actual)
    }

    @Test
    fun decoding_a_class_should_unnest_from_a_compound_with_the_class_serial_name_as_the_key() {
        @Serializable
        @SerialName("RootKey")
        data class MyClass(val property: String)

        assertReadsCorrectly(
            expectedValue = MyClass("value"),
            nbtTag = buildNbtCompound {
                putNbtCompound("RootKey") {
                    put("property", "value")
                }
            }
        )

    }
}
