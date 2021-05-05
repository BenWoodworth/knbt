package net.benwoodworth.knbt.internal

import data.bigTestTag
import data.testTag
import kotlinx.serialization.ExperimentalSerializationApi
import net.benwoodworth.knbt.LoggingNbtWriter
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.getResourceAsText
import net.benwoodworth.knbt.tag.*
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
    fun `Encoding TestNbt should write correctly`() {
        assertEncodesCorrectly(
            tag = testTag,
            expectedLog = """
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_Compound, "hello world")
                beginCompound()
                beginCompoundEntry(TAG_String, "name")
                writeString("Bananrama")
                endCompound()
                endCompound()
            """,
        )
    }

    @Test
    fun `Encoding BigTestNbt should write correctly`() {
        assertEncodesCorrectly(
            tag = bigTestTag,
            expectedLog = getResourceAsText("/bigtest-writer.log"),
        )
    }

    @Test
    fun `Encoding compound with no entries should write correctly`() {
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
    fun `Encoding compound with one entry should write correctly`() {
        assertEncodesCorrectly(
            tag = buildNbtCompound { put("property", 7) },
            expectedLog = """
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_Int, "property")
                writeInt(7)
                endCompound()
            """,
        )
    }

    @Test
    fun `Encoding compound with two entries should write correctly`() {
        assertEncodesCorrectly(
            tag = buildNbtCompound {
                put("entry1", "value1")
                put("entry2", 1234L)
            },
            expectedLog = """
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_String, "entry1")
                writeString("value1")
                beginCompoundEntry(TAG_Long, "entry2")
                writeLong(1234L)
                endCompound()
            """,
        )
    }

    @Test
    fun `Encoding List should write correctly`() {
        assertEncodesCorrectly(
            tag = buildNbtList<NbtList<*>> {
                addInternal(nbtListOf(1.toNbtByte()))
                addInternal(nbtListOf<NbtInt>())
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
    fun `Encoding ByteArray should write correctly`() {
        assertEncodesCorrectly(
            tag = nbtByteArrayOf(1, 2, 3),
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
    fun `Encoding IntArray should write correctly`() {
        assertEncodesCorrectly(
            tag = nbtIntArrayOf(1, 2, 3),
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
    fun `Encoding LongArray should write correctly`() {
        assertEncodesCorrectly(
            tag = nbtLongArrayOf(1, 2, 3),
            expectedLog = """
                beginRootTag(TAG_Long_Array)
                beginLongArray(3)
                beginLongArrayEntry()
                writeLong(1L)
                beginLongArrayEntry()
                writeLong(2L)
                beginLongArrayEntry()
                writeLong(3L)
                endLongArray()
            """,
        )
    }

    @Test
    fun `Encoding Byte should write correctly`() {
        assertEncodesCorrectly(
            tag = NbtByte(4),
            expectedLog = """
                beginRootTag(TAG_Byte)
                writeByte(4)
            """,
        )
    }

    @Test
    fun `Encoding Short should write correctly`() {
        assertEncodesCorrectly(
            tag = NbtShort(5),
            expectedLog = """
                beginRootTag(TAG_Short)
                writeShort(5)
            """,
        )
    }

    @Test
    fun `Encoding Int should write correctly`() {
        assertEncodesCorrectly(
            tag = NbtInt(6),
            expectedLog = """
                beginRootTag(TAG_Int)
                writeInt(6)
            """,
        )
    }

    @Test
    fun `Encoding Long should write correctly`() {
        assertEncodesCorrectly(
            tag = NbtLong(7L),
            expectedLog = """
                beginRootTag(TAG_Long)
                writeLong(7L)
            """,
        )
    }

    @Test
    fun `Encoding Float should write correctly`() {
        assertEncodesCorrectly(
            tag = NbtFloat(3.14f),
            expectedLog = """
                beginRootTag(TAG_Float)
                writeFloat(3.14f)
            """,
        )
    }

    @Test
    fun `Encoding Double should write correctly`() {
        assertEncodesCorrectly(
            tag = NbtDouble(3.14),
            expectedLog = """
                beginRootTag(TAG_Double)
                writeDouble(3.14)
            """,
        )
    }
}
