package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.*
import net.benwoodworth.knbt.test.shouldReturn
import kotlin.test.Test

class TreeNbtReaderTest {
    private inline fun expectNbtReaderCalls(tag: NbtTag, assertCalls: NbtReader.() -> Unit) {
        TreeNbtReader(tag).assertCalls()
    }

    @Test
    fun should_read_Byte_correctly() = parameterizeTest {
        val value by parameterOfBytes()

        expectNbtReaderCalls(NbtByte(value)) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Byte)
            readByte() shouldReturn value
        }
    }

    @Test
    fun should_read_Short_correctly() = parameterizeTest {
        val value by parameterOfShorts()

        expectNbtReaderCalls(NbtShort(value)) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Short)
            readShort() shouldReturn value
        }
    }

    @Test
    fun should_read_Int_correctly() = parameterizeTest {
        val value by parameterOfInts()

        expectNbtReaderCalls(NbtInt(value)) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Int)
            readInt() shouldReturn value
        }
    }

    @Test
    fun should_read_Long_correctly() = parameterizeTest {
        val value by parameterOfLongs()

        expectNbtReaderCalls(NbtLong(value)) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Long)
            readLong() shouldReturn value
        }
    }

    @Test
    fun should_read_Float_correctly() = parameterizeTest {
        val value by parameterOfFloats()

        expectNbtReaderCalls(NbtFloat(value)) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Float)
            readFloat() shouldReturn value
        }
    }

    @Test
    fun should_read_Double_correctly() = parameterizeTest {
        val value by parameterOfDoubles()

        expectNbtReaderCalls(NbtDouble(value)) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Double)
            readDouble() shouldReturn value
        }
    }

    @Test
    fun should_read_ByteArray_correctly() = parameterizeTest {
        val value by parameterOfByteArrays()

        expectNbtReaderCalls(NbtByteArray(value.asList())) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Byte_Array)
            beginByteArray() shouldReturn ArrayInfo(value.size)
            repeat(value.size) { index ->
                readByte() shouldReturn value[index]
            }
            endByteArray()
        }
    }

    @Test
    fun should_read_IntArray_correctly() = parameterizeTest {
        val value by parameterOfIntArrays()

        expectNbtReaderCalls(NbtIntArray(value.asList())) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Int_Array)
            beginIntArray() shouldReturn ArrayInfo(value.size)
            repeat(value.size) { index ->
                readInt() shouldReturn value[index]
            }
            endIntArray()
        }
    }

    @Test
    fun should_read_LongArray_correctly() = parameterizeTest {
        val value by parameterOfLongArrays()

        expectNbtReaderCalls(NbtLongArray(value.asList())) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Long_Array)
            beginLongArray() shouldReturn ArrayInfo(value.size)
            repeat(value.size) { index ->
                readLong() shouldReturn value[index]
            }
            endLongArray()
        }
    }

    @Test
    fun should_read_Compound_with_no_entries_correctly() {
        expectNbtReaderCalls(buildNbtCompound {}) {
            beginRootTag() shouldReturn RootTagInfo(TAG_Compound)
            beginCompound()
            beginCompoundEntry() shouldReturn NamedTagInfo.End
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
            beginCompoundEntry() shouldReturn NamedTagInfo(TAG_Int, "entry")
            readInt() shouldReturn 5
            beginCompoundEntry() shouldReturn NamedTagInfo.End
            endCompound()
        }
    }

    @Test
    fun should_read_List_with_no_entries_correctly() {
        expectNbtReaderCalls(NbtList(emptyList())) {
            beginRootTag() shouldReturn RootTagInfo(TAG_List)
            beginList() shouldReturn ListInfo(TAG_End, 0)
            endList()
        }
    }

    @Test
    fun should_read_List_with_one_entry_correctly() {
        expectNbtReaderCalls(NbtList(listOf("entry").map { NbtString(it) })) {
            beginRootTag() shouldReturn RootTagInfo(TAG_List)
            beginList() shouldReturn ListInfo(TAG_String, 1)
            readString() shouldReturn "entry"
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

            beginList() shouldReturn ListInfo(TAG_String, 2)
            readString() shouldReturn "hello"
            readString() shouldReturn "world"
            endList()

            beginList() shouldReturn ListInfo(TAG_End, 0)
            endList()

            beginList() shouldReturn ListInfo(TAG_Int, 1)
            readInt() shouldReturn 42
            endList()

            endList()
        }
    }
}
