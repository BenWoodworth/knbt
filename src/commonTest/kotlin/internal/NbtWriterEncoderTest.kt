@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import data.bigTestTag
import data.testTag
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.mocks.VerifyingNbtWriterMock
import kotlin.test.Test

class NbtWriterEncoderTest {
    private fun assertWritesCorrectly(tag: NbtTag, expectedCalls: VerifyingNbtWriterMock.Builder.() -> Unit) {
        VerifyingNbtWriterMock.create(expectedCalls).verify { writer ->
            NbtWriterEncoder(NbtFormat(), writer).encodeSerializableValue(NbtTag.serializer(), tag)
        }
    }

    @Test
    fun Encoding_TestNbt_should_write_correctly() {
        assertWritesCorrectly(
            tag = testTag,
            expectedCalls = {
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_Compound, "hello world")
                beginCompound()
                beginCompoundEntry(TAG_String, "name")
                writeString("Bananrama")
                endCompound()
                endCompound()
            },
        )
    }

    @Test
    fun Encoding_BigTestNbt_should_write_correctly() {
        assertWritesCorrectly(
            tag = bigTestTag,
            expectedCalls = {
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_Compound, "Level")
                beginCompound()
                beginCompoundEntry(TAG_Long, "longTest")
                writeLong(9223372036854775807L)
                beginCompoundEntry(TAG_Short, "shortTest")
                writeShort(32767)
                beginCompoundEntry(TAG_String, "stringTest")
                writeString("HELLO WORLD THIS IS A TEST STRING ÅÄÖ!")
                beginCompoundEntry(TAG_Float, "floatTest")
                writeFloat(0.49823147f)
                beginCompoundEntry(TAG_Int, "intTest")
                writeInt(2147483647)
                beginCompoundEntry(TAG_Compound, "nested compound test")
                beginCompound()
                beginCompoundEntry(TAG_Compound, "ham")
                beginCompound()
                beginCompoundEntry(TAG_String, "name")
                writeString("Hampus")
                beginCompoundEntry(TAG_Float, "value")
                writeFloat(0.75f)
                endCompound()
                beginCompoundEntry(TAG_Compound, "egg")
                beginCompound()
                beginCompoundEntry(TAG_String, "name")
                writeString("Eggbert")
                beginCompoundEntry(TAG_Float, "value")
                writeFloat(0.5f)
                endCompound()
                endCompound()
                beginCompoundEntry(TAG_List, "listTest (long)")
                beginList(TAG_Long, 5)
                for (n in 11L..15L) {
                    beginListEntry()
                    writeLong(n)
                }
                endList()
                beginCompoundEntry(TAG_List, "listTest (compound)")
                beginList(TAG_Compound, 2)
                beginListEntry()
                beginCompound()
                beginCompoundEntry(TAG_String, "name")
                writeString("Compound tag #0")
                beginCompoundEntry(TAG_Long, "created-on")
                writeLong(1264099775885L)
                endCompound()
                beginListEntry()
                beginCompound()
                beginCompoundEntry(TAG_String, "name")
                writeString("Compound tag #1")
                beginCompoundEntry(TAG_Long, "created-on")
                writeLong(1264099775885L)
                endCompound()
                endList()
                beginCompoundEntry(TAG_Byte, "byteTest")
                writeByte(127)
                beginCompoundEntry(
                    TAG_Byte_Array,
                    "byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))"
                )
                beginByteArray(1000)
                repeat(1000) { n ->
                    beginByteArrayEntry()
                    writeByte(((n * n * 255 + n * 7) % 100).toByte())
                }
                endByteArray()
                beginCompoundEntry(TAG_Double, "doubleTest")
                writeDouble(0.4931287132182315)
                endCompound()
                endCompound()
            },
        )
    }

    @Test
    fun Encoding_compound_with_no_entries_should_write_correctly() {
        assertWritesCorrectly(
            tag = buildNbtCompound {},
            expectedCalls = {
                beginRootTag(TAG_Compound)
                beginCompound()
                endCompound()
            },
        )
    }

    @Test
    fun Encoding_compound_with_one_entry_should_write_correctly() {
        assertWritesCorrectly(
            tag = buildNbtCompound { put("property", 7) },
            expectedCalls = {
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_Int, "property")
                writeInt(7)
                endCompound()
            },
        )
    }

    @Test
    fun Encoding_compound_with_two_entries_should_write_correctly() {
        assertWritesCorrectly(
            tag = buildNbtCompound {
                put("entry1", "value1")
                put("entry2", 1234L)
            },
            expectedCalls = {
                beginRootTag(TAG_Compound)
                beginCompound()
                beginCompoundEntry(TAG_String, "entry1")
                writeString("value1")
                beginCompoundEntry(TAG_Long, "entry2")
                writeLong(1234)
                endCompound()
            },
        )
    }

    @Test
    fun Encoding_List_should_write_correctly() {
        assertWritesCorrectly(
            tag = buildNbtList<NbtList<*>> {
                add(NbtList(listOf(NbtByte(1))))
                add(NbtList(emptyList<NbtInt>()))
            },
            expectedCalls = {
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
            },
        )
    }

    @Test
    fun Encoding_ByteArray_should_write_correctly() {
        assertWritesCorrectly(
            tag = NbtByteArray(byteArrayOf(1, 2, 3)),
            expectedCalls = {
                beginRootTag(TAG_Byte_Array)
                beginByteArray(3)
                beginByteArrayEntry()
                writeByte(1)
                beginByteArrayEntry()
                writeByte(2)
                beginByteArrayEntry()
                writeByte(3)
                endByteArray()
            },
        )
    }

    @Test
    fun Encoding_IntArray_should_write_correctly() {
        assertWritesCorrectly(
            tag = NbtIntArray(intArrayOf(1, 2, 3)),
            expectedCalls = {
                beginRootTag(TAG_Int_Array)
                beginIntArray(3)
                beginIntArrayEntry()
                writeInt(1)
                beginIntArrayEntry()
                writeInt(2)
                beginIntArrayEntry()
                writeInt(3)
                endIntArray()
            },
        )
    }

    @Test
    fun Encoding_LongArray_should_write_correctly() {
        assertWritesCorrectly(
            tag = NbtLongArray(longArrayOf(1, 2, 3)),
            expectedCalls = {
                beginRootTag(TAG_Long_Array)
                beginLongArray(3)
                beginLongArrayEntry()
                writeLong(1)
                beginLongArrayEntry()
                writeLong(2)
                beginLongArrayEntry()
                writeLong(3)
                endLongArray()
            },
        )
    }

    @Test
    fun Encoding_Byte_should_write_correctly() {
        assertWritesCorrectly(
            tag = NbtByte(4),
            expectedCalls = {
                beginRootTag(TAG_Byte)
                writeByte(4)
            },
        )
    }

    @Test
    fun Encoding_Short_should_write_correctly() {
        assertWritesCorrectly(
            tag = NbtShort(5),
            expectedCalls = {
                beginRootTag(TAG_Short)
                writeShort(5)
            },
        )
    }

    @Test
    fun Encoding_Int_should_write_correctly() {
        assertWritesCorrectly(
            tag = NbtInt(6),
            expectedCalls = {
                beginRootTag(TAG_Int)
                writeInt(6)
            },
        )
    }

    @Test
    fun Encoding_Long_should_write_correctly() {
        assertWritesCorrectly(
            tag = NbtLong(7L),
            expectedCalls = {
                beginRootTag(TAG_Long)
                writeLong(7)
            },
        )
    }

    @Test
    fun Encoding_Float_should_write_correctly() {
        assertWritesCorrectly(
            tag = NbtFloat(3.14f),
            expectedCalls = {
                beginRootTag(TAG_Float)
                writeFloat(3.14f)
            },
        )
    }

    @Test
    fun Encoding_Double_should_write_correctly() {
        assertWritesCorrectly(
            tag = NbtDouble(3.14),
            expectedCalls = {
                beginRootTag(TAG_Double)
                writeDouble(3.14)
            },
        )
    }
}
