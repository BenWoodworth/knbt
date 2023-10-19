package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.NbtTagType.*
import okio.BufferedSource
import okio.Closeable

internal abstract class BinaryNbtReader : NbtReader, Closeable {
    protected abstract val source: BufferedSource

    private val tagTypeStack = ArrayDeque<NbtTagType>()
    private val elementsRemainingStack = ArrayDeque<Int>()

    override fun close(): Unit = source.close()

    private fun <T> ArrayDeque<T>.replaceLast(element: T): T = set(lastIndex, element)

    protected fun BufferedSource.readNbtTagType(): NbtTagType {
        val tagId = readByte()

        return tagId.toNbtTagTypeOrNull()
            ?: throw NbtDecodingException("Unknown NBT tag type ID: 0x${tagId.toHex()}")
    }

    private fun checkTagType(expected: NbtTagType) {
        val actual = tagTypeStack.lastOrNull()
        if (expected != actual && actual != null) {
            throw NbtEncodingException("Expected $expected, but got $actual")
        }
    }

    abstract override fun beginRootTag(): NbtReader.RootTagInfo

    override fun beginCompound() {
        checkTagType(TAG_Compound)
        tagTypeStack += TAG_End
    }

    override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo {
        val type = source.readNbtTagType()
        return if (type == TAG_End) {
            NbtReader.CompoundEntryInfo.End
        } else {
            tagTypeStack.replaceLast(type)
            NbtReader.CompoundEntryInfo(type, source.readNbtString())
        }
    }

    override fun endCompound() {
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

    final override fun beginList(): NbtReader.ListInfo {
        checkTagType(TAG_List)

        val type = source.readNbtTagType()
        val size = source.readNbtInt()
        beginCollection(type, size)

        return NbtReader.ListInfo(type, size)
    }

    final override fun beginListEntry(): Boolean = beginCollectionEntry()

    final override fun endList(): Unit = endCollection()

    final override fun beginByteArray(): NbtReader.ArrayInfo {
        checkTagType(TAG_Byte_Array)

        val size = source.readNbtInt()
        beginCollection(TAG_Byte, size)

        return NbtReader.ArrayInfo(size)
    }

    final override fun beginByteArrayEntry(): Boolean = beginCollectionEntry()

    final override fun endByteArray(): Unit = endCollection()

    final override fun beginIntArray(): NbtReader.ArrayInfo {
        checkTagType(TAG_Int_Array)

        val size = source.readNbtInt()
        beginCollection(TAG_Int, size)

        return NbtReader.ArrayInfo(size)
    }

    final override fun beginIntArrayEntry(): Boolean = beginCollectionEntry()

    final override fun endIntArray(): Unit = endCollection()

    final override fun beginLongArray(): NbtReader.ArrayInfo {
        checkTagType(TAG_Long_Array)

        val size = source.readNbtInt()
        beginCollection(TAG_Long, size)

        return NbtReader.ArrayInfo(size)
    }

    final override fun beginLongArrayEntry(): Boolean = beginCollectionEntry()

    final override fun endLongArray(): Unit = endCollection()

    final override fun readByte(): Byte {
        checkTagType(TAG_Byte)
        return source.readByte()
    }

    final override fun readShort(): Short {
        checkTagType(TAG_Short)
        return source.readNbtShort()
    }

    final override fun readInt(): Int {
        checkTagType(TAG_Int)
        return source.readNbtInt()
    }

    final override fun readLong(): Long {
        checkTagType(TAG_Long)
        return source.readNbtLong()
    }

    final override fun readFloat(): Float {
        checkTagType(TAG_Float)
        return source.readNbtFloat()
    }

    final override fun readDouble(): Double {
        checkTagType(TAG_Double)
        return source.readNbtDouble()
    }

    final override fun readString(): String {
        checkTagType(TAG_String)
        return source.readNbtString()
    }

    protected abstract fun BufferedSource.readNbtShort(): Short
    protected abstract fun BufferedSource.readNbtInt(): Int
    protected abstract fun BufferedSource.readNbtLong(): Long
    protected abstract fun BufferedSource.readNbtFloat(): Float
    protected abstract fun BufferedSource.readNbtDouble(): Double
    protected abstract fun BufferedSource.readNbtString(): String
}

internal abstract class NamedBinaryNbtReader : BinaryNbtReader() {
    private var compoundNesting = 0
    private var readRootEntry = false

    final override fun beginRootTag(): NbtReader.RootTagInfo =
        NbtReader.RootTagInfo(TAG_Compound)

    final override fun beginCompound() {
        super.beginCompound()
        compoundNesting++
    }

    final override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo {
        if (compoundNesting == 1) {
            if (readRootEntry) return NbtReader.CompoundEntryInfo.End
            readRootEntry = true
        }

        return super.beginCompoundEntry()
    }

    final override fun endCompound() {
        if (compoundNesting == 1 && !readRootEntry) throw NbtDecodingException("The binary NBT format only supports $TAG_Compound with one entry")

        super.endCompound()
        compoundNesting--
    }
}

internal class JavaNbtReader(
    override val source: BufferedSource
) : NamedBinaryNbtReader() {
    override fun BufferedSource.readNbtShort(): Short =
        readShort()

    override fun BufferedSource.readNbtInt(): Int =
        readInt()

    override fun BufferedSource.readNbtLong(): Long =
        readLong()

    override fun BufferedSource.readNbtFloat(): Float =
        Float.fromBits(readInt())

    override fun BufferedSource.readNbtDouble(): Double =
        Double.fromBits(readLong())

    override fun BufferedSource.readNbtString(): String {
        val byteCount = readShort().toUShort().toLong()
        return readUtf8(byteCount)
    }
}

internal abstract class JavaNetworkNbtReader : BinaryNbtReader() {
    override fun BufferedSource.readNbtShort(): Short =
        readShort()

    override fun BufferedSource.readNbtInt(): Int =
        readInt()

    override fun BufferedSource.readNbtLong(): Long =
        readLong()

    override fun BufferedSource.readNbtFloat(): Float =
        Float.fromBits(readInt())

    override fun BufferedSource.readNbtDouble(): Double =
        Double.fromBits(readLong())

    override fun BufferedSource.readNbtString(): String {
        val byteCount = readShort().toUShort().toLong()
        return readUtf8(byteCount)
    }

    class EmptyNamedRoot(
        override val source: BufferedSource
    ) : JavaNetworkNbtReader() {
        private fun BufferedSource.discardTagName() {
            val nameLength = readShort().toUShort().toLong()
            skip(nameLength)
        }

        override fun beginRootTag(): NbtReader.RootTagInfo {
            val type = source.readNbtTagType()
            source.discardTagName()

            return NbtReader.RootTagInfo(type)
        }
    }

    class UnnamedRoot(
        override val source: BufferedSource
    ) : JavaNetworkNbtReader() {
        override fun beginRootTag(): NbtReader.RootTagInfo =
            NbtReader.RootTagInfo(source.readNbtTagType())
    }
}

internal class BedrockNbtReader(
    override val source: BufferedSource
) : NamedBinaryNbtReader() {
    override fun BufferedSource.readNbtShort(): Short =
        readShortLe()

    override fun BufferedSource.readNbtInt(): Int =
        readIntLe()

    override fun BufferedSource.readNbtLong(): Long =
        readLongLe()

    override fun BufferedSource.readNbtFloat(): Float =
        Float.fromBits(readIntLe())

    override fun BufferedSource.readNbtDouble(): Double =
        Double.fromBits(readLongLe())

    override fun BufferedSource.readNbtString(): String {
        val byteCount = readShortLe().toUShort().toLong()
        return readUtf8(byteCount)
    }
}

internal class BedrockNetworkNbtReader(
    override val source: BufferedSource
) : NamedBinaryNbtReader() {
    override fun BufferedSource.readNbtShort(): Short =
        readShortLe()

    override fun BufferedSource.readNbtInt(): Int =
        readLEB128(5).zigZagDecode().toInt()

    override fun BufferedSource.readNbtLong(): Long =
        readLEB128(10).zigZagDecode()

    override fun BufferedSource.readNbtFloat(): Float =
        Float.fromBits(readLEB128(5).zigZagDecode().toInt())

    override fun BufferedSource.readNbtDouble(): Double =
        Double.fromBits(readLEB128(10).zigZagDecode())

    override fun BufferedSource.readNbtString(): String {
        val byteCount = readLEB128(5).toLong()
        return readUtf8(byteCount)
    }
}
