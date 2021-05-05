package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.TestValues
import net.benwoodworth.knbt.internal.NbtReader.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.shouldReturn
import net.benwoodworth.knbt.tag.*
import org.junit.Test

class TreeNbtReaderTest {
    private inline fun expectNbtReaderCalls(tag: NbtTag, assertCalls: NbtReader.() -> Unit) {
        TreeNbtReader(tag).assertCalls()
    }

    @Test
    fun `Should read Byte correctly`() {
        TestValues.bytes.forEach { value ->
            expectNbtReaderCalls(NbtByte(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Byte)
                readByte() shouldReturn value
            }
        }
    }

    @Test
    fun `Should read Short correctly`() {
        TestValues.shorts.forEach { value ->
            expectNbtReaderCalls(NbtShort(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Short)
                readShort() shouldReturn value
            }
        }
    }

    @Test
    fun `Should read Int correctly`() {
        TestValues.ints.forEach { value ->
            expectNbtReaderCalls(NbtInt(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Int)
                readInt() shouldReturn value
            }
        }
    }

    @Test
    fun `Should read Long correctly`() {
        TestValues.longs.forEach { value ->
            expectNbtReaderCalls(NbtLong(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Long)
                readLong() shouldReturn value
            }
        }
    }

    @Test
    fun `Should read Float correctly`() {
        TestValues.floats.forEach { value ->
            expectNbtReaderCalls(NbtFloat(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Float)
                readFloat() shouldReturn value
            }
        }
    }

    @Test
    fun `Should read Double correctly`() {
        TestValues.doubles.forEach { value ->
            expectNbtReaderCalls(NbtDouble(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Double)
                readDouble() shouldReturn value
            }
        }
    }

    @Test
    fun `Should read ByteArray correctly`() {
        TestValues.byteArrays.forEach { value ->
            expectNbtReaderCalls(value.toNbtByteArray()) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Byte_Array)
                beginByteArray() shouldReturn ArrayInfo(value.size)
                repeat(value.size) { index ->
                    beginByteArrayEntry() shouldReturn true
                    readByte() shouldReturn value[index]
                }
                beginByteArrayEntry() shouldReturn false
                endByteArray()
            }
        }
    }

    @Test
    fun `Should read IntArray correctly`() {
        TestValues.intArrays.forEach { value ->
            expectNbtReaderCalls(value.toNbtIntArray()) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Int_Array)
                beginIntArray() shouldReturn ArrayInfo(value.size)
                repeat(value.size) { index ->
                    beginIntArrayEntry() shouldReturn true
                    readInt() shouldReturn value[index]
                }
                beginIntArrayEntry() shouldReturn false
                endIntArray()
            }
        }
    }

    @Test
    fun `Should read LongArray correctly`() {
        TestValues.longArrays.forEach { value ->
            expectNbtReaderCalls(value.toNbtLongArray()) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Long_Array)
                beginLongArray() shouldReturn ArrayInfo(value.size)
                repeat(value.size) { index ->
                    beginLongArrayEntry() shouldReturn true
                    readLong() shouldReturn value[index]
                }
                beginLongArrayEntry() shouldReturn false
                endLongArray()
            }
        }
    }

    @Test
    fun `Should read Compound with no entries correctly`() {
        expectNbtReaderCalls(buildNbtCompound {}) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Compound)
            beginCompound()
            beginCompoundEntry() shouldReturn CompoundEntryInfo.End
            endCompound()
        }
    }

    @Test
    fun `Should read Compound with one entry correctly`() {
        expectNbtReaderCalls(
            buildNbtCompound { put("entry", 5) }
        ) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Compound)
            beginCompound()
            beginCompoundEntry() shouldReturn CompoundEntryInfo(TAG_Int, "entry")
            readInt() shouldReturn 5
            beginCompoundEntry() shouldReturn CompoundEntryInfo.End
            endCompound()
        }
    }

    @Test
    fun `Should read List with no entries correctly`() {
        expectNbtReaderCalls(nbtListOf<NbtTag>()) {
            beginRootTag() shouldReturn RootTagInfo(TAG_List)
            beginList() shouldReturn ListInfo(TAG_End, 0)
            beginListEntry() shouldReturn false
            endList()
        }
    }

    @Test
    fun `Should read List with one entry correctly`() {
        expectNbtReaderCalls(listOf("entry").toNbtList()) {
            beginRootTag() shouldReturn RootTagInfo(TAG_List)
            beginList() shouldReturn ListInfo(TAG_String, 1)
            beginListEntry() shouldReturn true
            readString() shouldReturn "entry"
            beginListEntry() shouldReturn false
            endList()
        }
    }

    @Test
    fun `Should read List of Lists correctly`() {
        val list = buildNbtList<NbtList<*>> {
            addNbtList<NbtString> {
                add("hello")
                add("world")
            }
            addNbtList<Nothing> {}
            addNbtList<NbtInt> {
                add(42)
            }
        }

        expectNbtReaderCalls(list) {
            beginRootTag() shouldReturn RootTagInfo(TAG_List)
            beginList() shouldReturn ListInfo(TAG_List, 3)

            beginListEntry() shouldReturn true
            beginList() shouldReturn ListInfo(TAG_String, 2)
            beginListEntry() shouldReturn true
            readString() shouldReturn "hello"
            beginListEntry() shouldReturn true
            readString() shouldReturn "world"
            beginListEntry() shouldReturn false
            endList()

            beginListEntry() shouldReturn true
            beginList() shouldReturn ListInfo(TAG_End, 0)
            beginListEntry() shouldReturn false
            endList()

            beginListEntry() shouldReturn true
            beginList() shouldReturn ListInfo(TAG_Int, 1)
            beginListEntry() shouldReturn true
            readInt() shouldReturn 42
            beginListEntry() shouldReturn false
            endList()

            beginListEntry() shouldReturn false
            endList()
        }
    }
}
