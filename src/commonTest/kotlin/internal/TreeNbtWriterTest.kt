package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.TestValues
import net.benwoodworth.knbt.test.data.testClass
import net.benwoodworth.knbt.test.data.testTag
import kotlin.test.Test
import kotlin.test.assertEquals

class TreeNbtWriterTest {
    private inline fun expectNbtWriterCalls(expectedTag: NbtTag, write: NbtWriter.() -> Unit) {
        var actualTag: NbtTag? = null
        TreeNbtWriter { actualTag = it }.write()
        assertEquals(expectedTag, actualTag)
    }

    @Test
    fun should_encode_test_from_class_correctly(): Unit = assertEquals(
        expected = testTag,
        actual = NbtFormat().encodeToNbtTag(testClass),
    )

    @Test
    fun should_encode_bigtest_from_class_correctly(): Unit = assertEquals(
        expected = testTag,
        actual = NbtFormat().encodeToNbtTag(testClass),
    )

    @Test
    fun should_write_Byte_correctly() {
        TestValues.bytes.forEach { value ->
            expectNbtWriterCalls(NbtByte(value)) {
                beginRootTag(TAG_Byte)
                writeByte(value)
            }
        }
    }

    @Test
    fun should_write_Short_correctly() {
        TestValues.shorts.forEach { value ->
            expectNbtWriterCalls(NbtShort(value)) {
                beginRootTag(TAG_Short)
                writeShort(value)
            }
        }
    }

    @Test
    fun should_write_Int_correctly() {
        TestValues.ints.forEach { value ->
            expectNbtWriterCalls(NbtInt(value)) {
                beginRootTag(TAG_Int)
                writeInt(value)
            }
        }
    }

    @Test
    fun should_write_Long_correctly() {
        TestValues.longs.forEach { value ->
            expectNbtWriterCalls(NbtLong(value)) {
                beginRootTag(TAG_Long)
                writeLong(value)
            }
        }
    }

    @Test
    fun should_write_Float_correctly() {
        TestValues.floats.forEach { value ->
            expectNbtWriterCalls(NbtFloat(value)) {
                beginRootTag(TAG_Float)
                writeFloat(value)
            }
        }
    }

    @Test
    fun should_write_Double_correctly() {
        TestValues.doubles.forEach { value ->
            expectNbtWriterCalls(NbtDouble(value)) {
                beginRootTag(TAG_Double)
                writeDouble(value)
            }
        }
    }

    @Test
    fun should_write_ByteArray_correctly() {
        TestValues.byteArrays.forEach { value ->
            expectNbtWriterCalls(NbtByteArray(value)) {
                beginRootTag(TAG_Byte_Array)
                beginByteArray(value.size)
                value.forEach { entry ->
                    beginByteArrayEntry()
                    writeByte(entry)
                }
                endByteArray()
            }
        }
    }

    @Test
    fun should_write_IntArray_correctly() {
        TestValues.intArrays.forEach { value ->
            expectNbtWriterCalls(NbtIntArray(value)) {
                beginRootTag(TAG_Int_Array)
                beginIntArray(value.size)
                value.forEach { entry ->
                    beginIntArrayEntry()
                    writeInt(entry)
                }
                endIntArray()
            }
        }
    }

    @Test
    fun should_write_LongArray_correctly() {
        TestValues.longArrays.forEach { value ->
            expectNbtWriterCalls(NbtLongArray(value)) {
                beginRootTag(TAG_Long_Array)
                beginLongArray(value.size)
                value.forEach { entry ->
                    beginLongArrayEntry()
                    writeLong(entry)
                }
                endLongArray()
            }
        }
    }

    @Test
    fun should_write_Compound_with_no_entries_correctly() {
        expectNbtWriterCalls(buildNbtCompound {}) {
            beginRootTag(TAG_Compound)
            beginCompound()
            endCompound()
        }
    }

    @Test
    fun should_write_Compound_with_one_entry_correctly() {
        expectNbtWriterCalls(
            buildNbtCompound { put("entry", 5) }
        ) {
            beginRootTag(TAG_Compound)
            beginCompound()
            beginCompoundEntry(TAG_Int, "entry")
            writeInt(5)
            endCompound()
        }
    }

    @Test
    fun should_write_List_with_no_entries_correctly() {
        expectNbtWriterCalls(NbtList<NbtByte>(emptyList())) {
            beginRootTag(TAG_List)
            beginList(TAG_End, 0)
            endList()
        }
    }

    @Test
    fun should_write_List_with_one_entry_correctly() {
        expectNbtWriterCalls(NbtList(listOf("entry").map { NbtString(it) })) {
            beginRootTag(TAG_List)
            beginList(TAG_String, 1)
            beginListEntry()
            writeString("entry")
            endList()
        }
    }
}
