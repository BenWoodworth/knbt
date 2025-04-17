package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtType.*
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TreeNbtWriterTest {
    private val rootName = "root_name"

    private inline fun expectNbtWriterCalls(expectedTag: NbtTag, write: NbtWriter.() -> Unit) {
        var actualTag: NbtNamed<NbtTag>? = null
        TreeNbtWriter { actualTag = it }.write()
        assertEquals(NbtNamed(rootName, expectedTag), actualTag)
    }

    @Test
    fun should_write_Byte_correctly() = parameterizeTest {
        val value by parameterOfBytes()

        expectNbtWriterCalls(NbtByte(value)) {
            beginRootTag(TAG_Byte, rootName)
            writeByte(value)
        }
    }

    @Test
    fun should_write_Short_correctly() = parameterizeTest {
        val value by parameterOfShorts()

        expectNbtWriterCalls(NbtShort(value)) {
            beginRootTag(TAG_Short, rootName)
            writeShort(value)
        }
    }

    @Test
    fun should_write_Int_correctly() = parameterizeTest {
        val value by parameterOfInts()

        expectNbtWriterCalls(NbtInt(value)) {
            beginRootTag(TAG_Int, rootName)
            writeInt(value)
        }
    }

    @Test
    fun should_write_Long_correctly() = parameterizeTest {
        val value by parameterOfLongs()

        expectNbtWriterCalls(NbtLong(value)) {
            beginRootTag(TAG_Long, rootName)
            writeLong(value)
        }
    }

    @Test
    fun should_write_Float_correctly() = parameterizeTest {
        val value by parameterOfFloats()

        expectNbtWriterCalls(NbtFloat(value)) {
            beginRootTag(TAG_Float, rootName)
            writeFloat(value)
        }
    }

    @Test
    fun should_write_Double_correctly() = parameterizeTest {
        val value by parameterOfDoubles()

        expectNbtWriterCalls(NbtDouble(value)) {
            beginRootTag(TAG_Double, rootName)
            writeDouble(value)
        }
    }

    @Test
    fun should_write_ByteArray_correctly() = parameterizeTest {
        val value by parameterOfByteArrays()

        expectNbtWriterCalls(NbtByteArray(value.asList())) {
            beginRootTag(TAG_Byte_Array, rootName)
            beginByteArray(value.size)
            value.forEach { entry ->
                beginByteArrayEntry()
                writeByte(entry)
            }
            endByteArray()
        }
    }

    @Test
    fun should_write_IntArray_correctly() = parameterizeTest {
        val value by parameterOfIntArrays()

        expectNbtWriterCalls(NbtIntArray(value.asList())) {
            beginRootTag(TAG_Int_Array, rootName)
            beginIntArray(value.size)
            value.forEach { entry ->
                beginIntArrayEntry()
                writeInt(entry)
            }
            endIntArray()
        }
    }

    @Test
    fun should_write_LongArray_correctly() = parameterizeTest {
        val value by parameterOfLongArrays()

        expectNbtWriterCalls(NbtLongArray(value.asList())) {
            beginRootTag(TAG_Long_Array, rootName)
            beginLongArray(value.size)
            value.forEach { entry ->
                beginLongArrayEntry()
                writeLong(entry)
            }
            endLongArray()
        }
    }

    @Test
    fun should_write_Compound_with_no_entries_correctly() {
        expectNbtWriterCalls(buildNbtCompound {}) {
            beginRootTag(TAG_Compound, rootName)
            beginCompound()
            endCompound()
        }
    }

    @Test
    fun should_write_Compound_with_one_entry_correctly() {
        expectNbtWriterCalls(
            buildNbtCompound { put("entry", 5) }
        ) {
            beginRootTag(TAG_Compound, rootName)
            beginCompound()
            beginCompoundEntry(TAG_Int, "entry")
            writeInt(5)
            endCompound()
        }
    }

    @Test
    fun should_write_List_with_no_entries_correctly() {
        expectNbtWriterCalls(NbtList(emptyList())) {
            beginRootTag(TAG_List, rootName)
            beginList(TAG_End, 0)
            endList()
        }
    }

    @Test
    fun should_write_List_with_one_entry_correctly() {
        expectNbtWriterCalls(NbtList(listOf("entry").map { NbtString(it) })) {
            beginRootTag(TAG_List, rootName)
            beginList(TAG_String, 1)
            beginListEntry()
            writeString("entry")
            endList()
        }
    }
}
