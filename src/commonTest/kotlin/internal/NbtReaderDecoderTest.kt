@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import data.bigTestTag
import data.testTag
import kotlinx.serialization.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.mocks.VerifyingNbtReaderMock
import kotlin.math.PI
import kotlin.test.*

class NbtReaderDecoderTest {
    private inline fun <reified T> assertReadsCorrectly(
        expectedValue: T,
        noinline expectedCalls: VerifyingNbtReaderMock.Builder.() -> Unit,
    ) {
        val actualValue = VerifyingNbtReaderMock.create(expectedCalls).verify { reader ->
            NbtReaderDecoder(NbtFormat(), reader).decodeSerializableValue(serializer<T>())
        }

        when (expectedValue) {
            is Float -> {
                assertIs<Float>(actualValue)
                assertEquals(expectedValue.fix(), actualValue.fix())
            }

            is ByteArray -> {
                assertIs<ByteArray>(actualValue)
                assertContentEquals(expectedValue, actualValue)
            }

            is IntArray -> {
                assertIs<IntArray>(actualValue)
                assertContentEquals(expectedValue, actualValue)
            }

            is LongArray -> {
                assertIs<LongArray>(actualValue)
                assertContentEquals(expectedValue, actualValue)
            }

            else -> assertEquals(expectedValue, actualValue)
        }
    }

    @Test
    fun Decoding_TestNbt_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = testTag,
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Compound)
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Compound, "hello world")
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_String, "name")
                readString() returns "Bananrama"
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
            },
        )
    }

    @Test
    fun Decoding_BigTestNbt_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = bigTestTag,
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Compound)
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Compound, "Level")
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Long, "longTest")
                readLong() returns 9223372036854775807L
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Short, "shortTest")
                readShort() returns 32767
                beginCompoundEntry() returns CompoundEntryInfo(TAG_String, "stringTest")
                readString() returns "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!"
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Float, "floatTest")
                readFloat() returns 0.49823147f
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Int, "intTest")
                readInt() returns 2147483647
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Compound, "nested compound test")
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Compound, "ham")
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_String, "name")
                readString() returns "Hampus"
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Float, "value")
                readFloat() returns 0.75f
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Compound, "egg")
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_String, "name")
                readString() returns "Eggbert"
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Float, "value")
                readFloat() returns 0.5f
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_List, "listTest (long)")
                beginList() returns ListInfo(TAG_Long, 5)
                for (n in 11L..15L) {
                    readLong() returns n
                }
                endList()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_List, "listTest (compound)")
                beginList() returns ListInfo(TAG_Compound, 2)
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_String, "name")
                readString() returns "Compound tag #0"
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Long, "created-on")
                readLong() returns 1264099775885L
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_String, "name")
                readString() returns "Compound tag #1"
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Long, "created-on")
                readLong() returns 1264099775885L
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
                endList()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Byte, "byteTest")
                readByte() returns 127
                beginCompoundEntry() returns CompoundEntryInfo(
                    TAG_Byte_Array,
                    "byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))"
                )
                beginByteArray() returns ArrayInfo(1000)
                repeat(1000) { n ->
                    readByte() returns ((n * n * 255 + n * 7) % 100).toByte()
                }
                endByteArray()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Double, "doubleTest")
                readDouble() returns 0.4931287132182315
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
            },
        )
    }

    @Test
    fun Decoding_compound_with_no_entries_to_Map_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = mapOf<String, Int>(),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Compound)
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
            },
        )
    }

    @Test
    fun Decoding_compound_with_one_entry_to_Map_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = mapOf("property" to 7),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Compound)
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Int, "property")
                readInt() returns 7
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
            },
        )
    }

    @Serializable
    data class OneProperty<T>(val property: T)

    @Test
    fun Decoding_compound_with_one_entry_to_class_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = OneProperty(7),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Compound)
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Int, "property")
                readInt() returns 7
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
            },
        )
    }

    @Test
    fun Decoding_List_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = listOf(listOf(1.toByte()), listOf()),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_List)
                beginList() returns ListInfo(TAG_List, 2)
                beginList() returns ListInfo(TAG_Byte, 1)
                readByte() returns 1
                endList()
                beginList() returns ListInfo(TAG_End, 0)
                endList()
                endList()
            },
        )
    }

    @Test
    fun Decoding_ByteArray_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = byteArrayOf(1, 2, 3),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Byte_Array)
                beginByteArray() returns ArrayInfo(size = 3)
                readByte() returns 1
                readByte() returns 2
                readByte() returns 3
                endByteArray()
            },
        )
    }

    @Test
    fun Decoding_IntArray_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = intArrayOf(1, 2, 3),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Int_Array)
                beginIntArray() returns ArrayInfo(size = 3)
                readInt() returns 1
                readInt() returns 2
                readInt() returns 3
                endIntArray()
            },
        )
    }

    @Test
    fun Decoding_LongArray_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = longArrayOf(1, 2, 3),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Long_Array)
                beginLongArray() returns ArrayInfo(size = 3)
                readLong() returns 1
                readLong() returns 2
                readLong() returns 3
                endLongArray()
            },
        )
    }

    @Serializable
    private data class TwoProperties(val entry1: String, val entry2: Long)

    @Test
    fun Decoding_compound_with_two_entries_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = TwoProperties(entry1 = "value1", entry2 = 1234L),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Compound)
                beginCompound()
                beginCompoundEntry() returns CompoundEntryInfo(TAG_String, "entry1")
                readString() returns "value1"
                beginCompoundEntry() returns CompoundEntryInfo(TAG_Long, "entry2")
                readLong() returns 1234
                beginCompoundEntry() returns CompoundEntryInfo(TAG_End, "")
                endCompound()
            },
        )
    }

    @Test
    fun Decoding_Byte_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = 4.toByte(),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Byte)
                readByte() returns 4
            },
        )
    }

    @Test
    fun Decoding_Short_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = 5.toShort(),
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Short)
                readShort() returns 5
            },
        )
    }

    @Test
    fun Decoding_Int_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = 6,
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Int)
                readInt() returns 6
            },
        )
    }

    @Test
    fun Decoding_Long_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = 7L,
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Long)
                readLong() returns 7
            },
        )
    }

    @Test
    fun Decoding_Float_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = 3.14f,
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Float)
                readFloat() returns 3.14f
            },
        )
    }

    @Test
    fun Decoding_Double_should_read_correctly() {
        assertReadsCorrectly(
            expectedValue = 3.14,
            expectedCalls = {
                beginRootTag() returns RootTagInfo(TAG_Double)
                readDouble() returns 3.14
            },
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
    fun Decoding_should_fail_on_unknown_key_if_not_ignoring() {
        val nbt = NbtFormat(ignoreUnknownKeys = false)

        assertFailsWith<NbtDecodingException> {
            nbt.decodeFromNbtTag<UnknownKeys>(unknownKeysTag)
        }
    }

    @Test
    fun Decoding_should_not_fail_on_unknown_key_if_ignoring() {
        val nbt = NbtFormat(ignoreUnknownKeys = true)

        val actual = nbt.decodeFromNbtTag<UnknownKeys>(unknownKeysTag)
        assertEquals(UnknownKeys.expected, actual)
    }
}
