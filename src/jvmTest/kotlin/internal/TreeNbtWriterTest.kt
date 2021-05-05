package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.TestValues
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.tag.*
import org.junit.Test
import kotlin.test.assertEquals

class TreeNbtWriterTest {
    private inline fun expectNbtWriterCalls(expectedTag: NbtTag, write: NbtWriter.() -> Unit) {
        var actualTag: NbtTag? = null
        TreeNbtWriter { actualTag = it }.write()
        assertEquals(expectedTag, actualTag)
    }

    @Test
    fun `Should write Byte correctly`() {
        TestValues.bytes.forEach { value ->
            expectNbtWriterCalls(NbtByte(value)) {
                beginRootTag(TAG_Byte)
                writeByte(value)
            }
        }
    }

    @Test
    fun `Should write Short correctly`() {
        TestValues.shorts.forEach { value ->
            expectNbtWriterCalls(NbtShort(value)) {
                beginRootTag(TAG_Short)
                writeShort(value)
            }
        }
    }

    @Test
    fun `Should write Int correctly`() {
        TestValues.ints.forEach { value ->
            expectNbtWriterCalls(NbtInt(value)) {
                beginRootTag(TAG_Int)
                writeInt(value)
            }
        }
    }

    @Test
    fun `Should write Long correctly`() {
        TestValues.longs.forEach { value ->
            expectNbtWriterCalls(NbtLong(value)) {
                beginRootTag(TAG_Long)
                writeLong(value)
            }
        }
    }

    @Test
    fun `Should write Float correctly`() {
        TestValues.floats.forEach { value ->
            expectNbtWriterCalls(NbtFloat(value)) {
                beginRootTag(TAG_Float)
                writeFloat(value)
            }
        }
    }

    @Test
    fun `Should write Double correctly`() {
        TestValues.doubles.forEach { value ->
            expectNbtWriterCalls(NbtDouble(value)) {
                beginRootTag(TAG_Double)
                writeDouble(value)
            }
        }
    }

    @Test
    fun `Should write ByteArray correctly`() {
        TestValues.byteArrays.forEach { value ->
            expectNbtWriterCalls(value.toNbtByteArray()) {
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
    fun `Should write IntArray correctly`() {
        TestValues.intArrays.forEach { value ->
            expectNbtWriterCalls(value.toNbtIntArray()) {
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
    fun `Should write LongArray correctly`() {
        TestValues.longArrays.forEach { value ->
            expectNbtWriterCalls(value.toNbtLongArray()) {
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
    fun `Should write Compound with no entries correctly`() {
        expectNbtWriterCalls(buildNbtCompound {}) {
            beginRootTag(TAG_Compound)
            beginCompound()
            endCompound()
        }
    }

    @Test
    fun `Should write Compound with one entry correctly`() {
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
    fun `Should write List with no entries correctly`() {
        expectNbtWriterCalls(nbtListOf<NbtTag>()) {
            beginRootTag(TAG_List)
            beginList(TAG_End, 0)
            endList()
        }
    }

    @Test
    fun `Should write List with one entry correctly`() {
        expectNbtWriterCalls(listOf("entry").toNbtList()) {
            beginRootTag(TAG_List)
            beginList(TAG_String, 1)
            beginListEntry()
            writeString("entry")
            endList()
        }
    }
}
