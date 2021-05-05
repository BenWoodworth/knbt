package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.NbtDecodingException
import net.benwoodworth.knbt.NbtEncodingException
import net.benwoodworth.knbt.internal.NbtTagType.*
import okio.BufferedSource
import okio.Source
import okio.buffer

internal class BinaryNbtReader(source: Source) : NbtReader {
    private val buffer = source.buffer()

    private var compoundNesting = 0
    private var readRootEntry = false

    private val tagTypeStack = ArrayDeque<NbtTagType>()
    private val elementsRemainingStack = ArrayDeque<Int>()

    private fun <T> ArrayDeque<T>.replaceLast(element: T): T = set(lastIndex, element)

    private fun BufferedSource.readNbtString(): String =
        readUtf8(readShort().toUShort().toLong())

    private fun BufferedSource.readNbtTagType(): NbtTagType =
        NbtTagType.fromId(readByte())

    private fun checkTagType(expected: NbtTagType) {
        val actual = tagTypeStack.lastOrNull()
        if (expected != actual && actual != null) {
            throw NbtEncodingException("Expected $expected, but got $actual")
        }
    }

    override fun beginRootTag(): NbtReader.RootTagInfo =
        NbtReader.RootTagInfo(TAG_Compound)

    override fun beginCompound() {
        checkTagType(TAG_Compound)
        compoundNesting++
        tagTypeStack += TAG_End
    }

    override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo {
        if (compoundNesting == 1) {
            if (readRootEntry) return NbtReader.CompoundEntryInfo.End
            readRootEntry = true
        }

        val type = buffer.readNbtTagType()
        return if (type == TAG_End) {
            NbtReader.CompoundEntryInfo.End
        } else {
            tagTypeStack.replaceLast(type)
            NbtReader.CompoundEntryInfo(type, buffer.readNbtString())
        }
    }

    override fun endCompound() {
        if (compoundNesting == 1 && !readRootEntry) throw NbtDecodingException("The binary NBT format only supports $TAG_Compound with one entry")
        compoundNesting--
        tagTypeStack.removeLast()
    }

    private fun beginCollection(elementType: NbtTagType, size: Int) {
        tagTypeStack += elementType
        elementsRemainingStack += size
    }

    private fun beginCollectionEntry(): Boolean {
        val remaining = elementsRemainingStack.last()
        return if (remaining > 0) {
            elementsRemainingStack.replaceLast(remaining - 1)
            true
        } else {
            false
        }
    }

    private fun endCollection() {
        tagTypeStack.removeLast()
        elementsRemainingStack.removeLast()
    }

    override fun beginList(): NbtReader.ListInfo {
        checkTagType(TAG_List)

        val type = buffer.readNbtTagType()
        val size = buffer.readInt()
        beginCollection(type, size)

        return NbtReader.ListInfo(type, size)
    }

    override fun beginListEntry(): Boolean = beginCollectionEntry()

    override fun endList(): Unit = endCollection()

    override fun beginByteArray(): NbtReader.ArrayInfo {
        checkTagType(TAG_Byte_Array)

        val size = buffer.readInt()
        beginCollection(TAG_Byte, size)

        return NbtReader.ArrayInfo(size)
    }

    override fun beginByteArrayEntry(): Boolean = beginCollectionEntry()

    override fun endByteArray(): Unit = endCollection()

    override fun beginIntArray(): NbtReader.ArrayInfo {
        checkTagType(TAG_Int_Array)

        val size = buffer.readInt()
        beginCollection(TAG_Int, size)

        return NbtReader.ArrayInfo(size)
    }

    override fun beginIntArrayEntry(): Boolean = beginCollectionEntry()

    override fun endIntArray(): Unit = endCollection()

    override fun beginLongArray(): NbtReader.ArrayInfo {
        checkTagType(TAG_Long_Array)

        val size = buffer.readInt()
        beginCollection(TAG_Long, size)

        return NbtReader.ArrayInfo(size)
    }

    override fun beginLongArrayEntry(): Boolean = beginCollectionEntry()

    override fun endLongArray(): Unit = endCollection()

    override fun readByte(): Byte {
        checkTagType(TAG_Byte)
        return buffer.readByte()
    }

    override fun readShort(): Short {
        checkTagType(TAG_Short)
        return buffer.readShort()
    }

    override fun readInt(): Int {
        checkTagType(TAG_Int)
        return buffer.readInt()
    }

    override fun readLong(): Long {
        checkTagType(TAG_Long)
        return buffer.readLong()
    }

    override fun readFloat(): Float {
        checkTagType(TAG_Float)
        return Float.fromBits(buffer.readInt())
    }

    override fun readDouble(): Double {
        checkTagType(TAG_Double)
        return Double.fromBits(buffer.readLong())
    }

    override fun readString(): String {
        checkTagType(TAG_String)
        return buffer.readNbtString()
    }
}
