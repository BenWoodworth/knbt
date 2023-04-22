package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.TestValues
import net.benwoodworth.knbt.test.data.*
import net.benwoodworth.knbt.test.shouldReturn
import kotlin.test.Test

class TreeNbtReaderTest {
    private inline fun expectNbtReaderCalls(tag: NbtTag, assertCalls: NbtReader.() -> Unit) {
        TreeNbtReader(tag).assertCalls()
    }

    @Test
    fun should_decode_test_to_class_correctly(): Unit = assertStructureEquals(
        expected = testClass,
        actual = NbtFormat().decodeFromNbtTag(testTag),
    )

    @Test
    fun should_decode_bigtest_to_class_correctly(): Unit = assertStructureEquals(
        expected = bigTestClass,
        actual = NbtFormat().decodeFromNbtTag(bigTestTag),
    )

    @Test
    fun should_read_Byte_correctly() {
        TestValues.bytes.forEach { value ->
            expectNbtReaderCalls(NbtByte(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Byte)
                readByte() shouldReturn value
            }
        }
    }

    @Test
    fun should_read_Short_correctly() {
        TestValues.shorts.forEach { value ->
            expectNbtReaderCalls(NbtShort(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Short)
                readShort() shouldReturn value
            }
        }
    }

    @Test
    fun should_read_Int_correctly() {
        TestValues.ints.forEach { value ->
            expectNbtReaderCalls(NbtInt(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Int)
                readInt() shouldReturn value
            }
        }
    }

    @Test
    fun should_read_Long_correctly() {
        TestValues.longs.forEach { value ->
            expectNbtReaderCalls(NbtLong(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Long)
                readLong() shouldReturn value
            }
        }
    }

    @Test
    fun should_read_Float_correctly() {
        TestValues.floats.forEach { value ->
            expectNbtReaderCalls(NbtFloat(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Float)
                readFloat() shouldReturn value
            }
        }
    }

    @Test
    fun should_read_Double_correctly() {
        TestValues.doubles.forEach { value ->
            expectNbtReaderCalls(NbtDouble(value)) {
                beginRootTag() shouldReturn RootTagInfo(TAG_Double)
                readDouble() shouldReturn value
            }
        }
    }

    @Test
    fun should_read_ByteArray_correctly() {
        TestValues.byteArrays.forEach { value ->
            expectNbtReaderCalls(NbtByteArray(value.asList())) {
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
    fun should_read_IntArray_correctly() {
        TestValues.intArrays.forEach { value ->
            expectNbtReaderCalls(NbtIntArray(value.asList())) {
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
    fun should_read_LongArray_correctly() {
        TestValues.longArrays.forEach { value ->
            expectNbtReaderCalls(NbtLongArray(value.asList())) {
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
    fun should_read_Compound_with_no_entries_correctly() {
        expectNbtReaderCalls(buildNbtCompound {}) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Compound)
            beginCompound()
            beginCompoundEntry() shouldReturn CompoundEntryInfo.End
            endCompound()
        }
    }

    @Test
    fun should_read_Compound_with_one_entry_correctly() {
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
    fun should_read_List_with_no_entries_correctly() {
        expectNbtReaderCalls(NbtList(emptyList())) {
            beginRootTag() shouldReturn RootTagInfo(TAG_List)
            beginList() shouldReturn ListInfo(TAG_End, 0)
            beginListEntry() shouldReturn false
            endList()
        }
    }

    @Test
    fun should_read_List_with_one_entry_correctly() {
        expectNbtReaderCalls(NbtList(listOf("entry").map { NbtString(it) })) {
            beginRootTag() shouldReturn RootTagInfo(TAG_List)
            beginList() shouldReturn ListInfo(TAG_String, 1)
            beginListEntry() shouldReturn true
            readString() shouldReturn "entry"
            beginListEntry() shouldReturn false
            endList()
        }
    }

    @Test
    fun should_read_List_of_Lists_correctly() {
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
