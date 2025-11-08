@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.ParameterizeScope
import data.testTag
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.*
import kotlin.math.PI
import kotlin.test.*

class NbtDecoderTest {
    private fun <T> ParameterizeScope.assertDecodesCorrectly(
        serializer: DeserializationStrategy<T>,
        tag: NbtTag,
        expectedValue: T,
        expectedLog: String,
    ) {
        val stringBuilder = StringBuilder()
        val reader = LoggingNbtReader(TreeNbtReader(tag), stringBuilder)

        val actualValue = try {
            NbtDecoder(parameterizedNbtFormat(), reader).decodeSerializableValue(serializer)
        } catch (e: Exception) {
            val log = stringBuilder.toString().trimIndent()
            val expectedLogTrimmed = expectedLog.trimIndent()

            throw Exception("Error decoding. NbtReader log: <\n$log\n>\nExpected log: <\n$expectedLogTrimmed\n>", e)
        }

        when (expectedValue) {
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

        assertEquals(expectedLog.trimIndent(), stringBuilder.toString().trimIndent())
    }

    private inline fun <reified T> ParameterizeScope.assertDecodesCorrectly(
        tag: NbtTag,
        expectedValue: T,
        expectedLog: String
    ): Unit =
        assertDecodesCorrectly(serializer(), tag, expectedValue, expectedLog)

    @Test
    fun Decoding_TestNbt_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            serializer = NbtTag.serializer(),
            tag = testTag,
            expectedValue = testTag,
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Compound)
                beginCompound()
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_Compound, name=hello world)
                beginCompound()
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_String, name=name)
                readString() -> Bananrama
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_End, name=)
                endCompound()
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_End, name=)
                endCompound()
            """,
        )
    }

//    @Test
//    fun Decoding_BigTestNbt_should_read_correctly() {
//        assertDecodesCorrectly(
//            tag = bigTestTag,
//            expectedValue = bigTestClass,
//            expectedLog = getResourceAsText("/bigtest-reader.log"),
//        )
//    }

    @Test
    fun Decoding_compound_with_no_entries_to_Map_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = buildNbtCompound {},
            expectedValue = mapOf<String, Int>(),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Compound)
                beginCompound()
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_End, name=)
                endCompound()
            """,
        )
    }

    @Test
    fun Decoding_compound_with_one_entry_to_Map_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = buildNbtCompound { put("property", 7) },
            expectedValue = mapOf("property" to 7),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Compound)
                beginCompound()
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_Int, name=property)
                readInt() -> 7
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_End, name=)
                endCompound()
            """,
        )
    }

    @Serializable
    data class OneProperty<T>(val property: T)

    @Test
    fun Decoding_compound_with_one_entry_to_class_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            serializer = OneProperty.serializer(Int.serializer()),
            tag = buildNbtCompound { put("property", 7) },
            expectedValue = OneProperty(7),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Compound)
                beginCompound()
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_Int, name=property)
                readInt() -> 7
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_End, name=)
                endCompound()
            """,
        )
    }

    @Test
    fun Decoding_List_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = buildNbtList<NbtList<*>> {
                add(NbtList(listOf(NbtByte(1))))
                add(NbtList(emptyList<NbtByte>()))
            },
            expectedValue = listOf(listOf(1.toByte()), listOf()),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_List)
                beginList() -> ListInfo(type=TAG_List, size=2)
                beginList() -> ListInfo(type=TAG_Byte, size=1)
                readByte() -> 1
                endList()
                beginList() -> ListInfo(type=TAG_End, size=0)
                endList()
                endList()
            """,
        )
    }

    @Test
    fun Decoding_ByteArray_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = NbtByteArray(byteArrayOf(1, 2, 3)),
            expectedValue = byteArrayOf(1, 2, 3),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Byte_Array)
                beginByteArray() -> ArrayInfo(size=3)
                readByte() -> 1
                readByte() -> 2
                readByte() -> 3
                endByteArray()
            """,
        )
    }

    @Test
    fun Decoding_IntArray_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = NbtIntArray(intArrayOf(1, 2, 3)),
            expectedValue = intArrayOf(1, 2, 3),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Int_Array)
                beginIntArray() -> ArrayInfo(size=3)
                readInt() -> 1
                readInt() -> 2
                readInt() -> 3
                endIntArray()
            """,
        )
    }

    @Test
    fun Decoding_LongArray_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = NbtLongArray(longArrayOf(1, 2, 3)),
            expectedValue = longArrayOf(1, 2, 3),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Long_Array)
                beginLongArray() -> ArrayInfo(size=3)
                readLong() -> 1
                readLong() -> 2
                readLong() -> 3
                endLongArray()
            """,
        )
    }

    @Serializable
    private data class TwoProperties(val entry1: String, val entry2: Long)

    @Test
    fun Decoding_compound_with_two_entries_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            serializer = TwoProperties.serializer(),
            tag = buildNbtCompound {
                put("entry1", "value1")
                put("entry2", 1234L)
            },
            expectedValue = TwoProperties(entry1 = "value1", entry2 = 1234L),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Compound)
                beginCompound()
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_String, name=entry1)
                readString() -> value1
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_Long, name=entry2)
                readLong() -> 1234
                beginCompoundEntry() -> CompoundEntryInfo(type=TAG_End, name=)
                endCompound()
            """,
        )
    }

    @Test
    fun Decoding_Byte_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = NbtByte(4),
            expectedValue = 4.toByte(),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Byte)
                readByte() -> 4
            """,
        )
    }

    @Test
    fun Decoding_Short_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = NbtShort(5),
            expectedValue = 5.toShort(),
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Short)
                readShort() -> 5
            """,
        )
    }

    @Test
    fun Decoding_Int_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = NbtInt(6),
            expectedValue = 6,
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Int)
                readInt() -> 6
            """,
        )
    }

    @Test
    fun Decoding_Long_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = NbtLong(7L),
            expectedValue = 7L,
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Long)
                readLong() -> 7
            """,
        )
    }

    @Test
    fun Decoding_Float_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = NbtFloat(3.14f),
            expectedValue = 3.14f,
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Float)
                readFloat() -> 3.14
            """,
        )
    }

    @Test
    fun Decoding_Double_should_read_correctly() = parameterizeTest {
        assertDecodesCorrectly(
            tag = NbtDouble(3.14),
            expectedValue = 3.14,
            expectedLog = """
                beginRootTag() -> RootTagInfo(type=TAG_Double)
                readDouble() -> 3.14
            """,
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
    fun Decoding_should_fail_on_unknown_key_if_not_ignoring() = parameterizeTest {
        val nbt = parameterizedNbtFormat { ignoreUnknownKeys = false }

        assertFailsWith<NbtDecodingException> {
            nbt.decodeFromNbtTag(UnknownKeys.serializer(), unknownKeysTag)
        }
    }

    @Test
    fun Decoding_should_not_fail_on_unknown_key_if_ignoring() = parameterizeTest {
        val nbt = parameterizedNbtFormat { ignoreUnknownKeys = true }

        val actual = nbt.decodeFromNbtTag(UnknownKeys.serializer(), unknownKeysTag)
        assertEquals(UnknownKeys.expected, actual)
    }
}
