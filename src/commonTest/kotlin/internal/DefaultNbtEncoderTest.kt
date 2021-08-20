@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import data.testTag
import kotlinx.serialization.ExperimentalSerializationApi
import net.benwoodworth.knbt.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultNbtEncoderTest {
    private fun assertEncodesCorrectly(tag: NbtTag, expectedLog: String) {
        val stringBuilder = StringBuilder()
        var actualTag: NbtTag? = null
        val writer = LoggingNbtWriter(TreeNbtWriter { actualTag = it }, stringBuilder)

        try {
            @OptIn(ExperimentalSerializationApi::class)
            DefaultNbtEncoder(Nbt, writer).encodeSerializableValue(NbtTag.serializer(), tag)
        } catch (e: Exception) {
            throw Exception("Error encoding. NbtWriter log: <\n$stringBuilder>", e)
        }

        assertEquals(tag, actualTag)
        assertEquals(expectedLog.trimIndent(), stringBuilder.toString().trimIndent())
    }

    @Test
    fun Encoding_TestNbt_should_write_correctly() {
        assertEncodesCorrectly(
            tag = testTag,
            expectedLog = """
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_Compound, hello world)
                beginCompound()
                beginCompoundEntry(TAG_String, name)
                writeString(Bananrama)
                endCompound()
                endCompound()
            """,
        )
    }

//    @Test
//    fun Encoding_BigTestNbt_should_write_correctly() {
//        assertEncodesCorrectly(
//            tag = bigTestTag,
//            expectedLog = getResourceAsText("/bigtest-writer.log"),
//        )
//    }

    @Test
    fun Encoding_compound_with_no_entries_should_write_correctly() {
        assertEncodesCorrectly(
            tag = buildNbtCompound {},
            expectedLog = """
                beginRootTag(TAG_Compound)
                beginCompound()
                endCompound()
            """,
        )
    }

    @Test
    fun Encoding_compound_with_one_entry_should_write_correctly() {
        assertEncodesCorrectly(
            tag = buildNbtCompound { put("property", 7) },
            expectedLog = """
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_Int, property)
                writeInt(7)
                endCompound()
            """,
        )
    }

    @Test
    fun Encoding_compound_with_two_entries_should_write_correctly() {
        assertEncodesCorrectly(
            tag = buildNbtCompound {
                put("entry1", "value1")
                put("entry2", 1234L)
            },
            expectedLog = """
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_String, entry1)
                writeString(value1)
                beginCompoundEntry(TAG_Long, entry2)
                writeLong(1234)
                endCompound()
            """,
        )
    }

    @Test
    fun Encoding_List_should_write_correctly() {
        assertEncodesCorrectly(
            tag = buildNbtList<NbtList<*>> {
                add(NbtList(listOf(NbtByte(1))))
                add(NbtList(emptyList<NbtInt>()))
            },
            expectedLog = """
                beginRootTag(TAG_List)
                beginList(TAG_List, 2)
                beginListEntry()
                beginList(TAG_Byte, 1)
                beginListEntry()
                writeByte(1)
                endList()
                beginListEntry()
                beginList(TAG_End, 0)
                endList()
                endList()
            """,
        )
    }

    @Test
    fun Encoding_ByteArray_should_write_correctly() {
        assertEncodesCorrectly(
            tag = NbtByteArray(listOf(1, 2, 3)),
            expectedLog = """
                beginRootTag(TAG_Byte_Array)
                beginByteArray(3)
                beginByteArrayEntry()
                writeByte(1)
                beginByteArrayEntry()
                writeByte(2)
                beginByteArrayEntry()
                writeByte(3)
                endByteArray()
            """,
        )
    }

    @Test
    fun Encoding_IntArray_should_write_correctly() {
        assertEncodesCorrectly(
            tag = NbtIntArray(listOf(1, 2, 3)),
            expectedLog = """
                beginRootTag(TAG_Int_Array)
                beginIntArray(3)
                beginIntArrayEntry()
                writeInt(1)
                beginIntArrayEntry()
                writeInt(2)
                beginIntArrayEntry()
                writeInt(3)
                endIntArray()
            """,
        )
    }

    @Test
    fun Encoding_LongArray_should_write_correctly() {
        assertEncodesCorrectly(
            tag = NbtLongArray(listOf(1, 2, 3)),
            expectedLog = """
                beginRootTag(TAG_Long_Array)
                beginLongArray(3)
                beginLongArrayEntry()
                writeLong(1)
                beginLongArrayEntry()
                writeLong(2)
                beginLongArrayEntry()
                writeLong(3)
                endLongArray()
            """,
        )
    }

    @Test
    fun Encoding_Byte_should_write_correctly() {
        assertEncodesCorrectly(
            tag = NbtByte(4),
            expectedLog = """
                beginRootTag(TAG_Byte)
                writeByte(4)
            """,
        )
    }

    @Test
    fun Encoding_Short_should_write_correctly() {
        assertEncodesCorrectly(
            tag = NbtShort(5),
            expectedLog = """
                beginRootTag(TAG_Short)
                writeShort(5)
            """,
        )
    }

    @Test
    fun Encoding_Int_should_write_correctly() {
        assertEncodesCorrectly(
            tag = NbtInt(6),
            expectedLog = """
                beginRootTag(TAG_Int)
                writeInt(6)
            """,
        )
    }

    @Test
    fun Encoding_Long_should_write_correctly() {
        assertEncodesCorrectly(
            tag = NbtLong(7L),
            expectedLog = """
                beginRootTag(TAG_Long)
                writeLong(7)
            """,
        )
    }

    @Test
    fun Encoding_Float_should_write_correctly() {
        assertEncodesCorrectly(
            tag = NbtFloat(3.14f),
            expectedLog = """
                beginRootTag(TAG_Float)
                writeFloat(3.14)
            """,
        )
    }

    @Test
    fun Encoding_Double_should_write_correctly() {
        assertEncodesCorrectly(
            tag = NbtDouble(3.14),
            expectedLog = """
                beginRootTag(TAG_Double)
                writeDouble(3.14)
            """,
        )
    }
}
