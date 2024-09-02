package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.NbtTagType.*
import okio.BufferedSource

internal abstract class BinaryNbtReader : NbtReader {
    protected abstract val context: NbtContext
    protected abstract val source: BufferedSource

    private val tagTypeStack = ArrayDeque<NbtTagType>()
    private val elementsRemainingStack = ArrayDeque<Int>()

    private fun <T> ArrayDeque<T>.replaceLast(element: T): T = set(lastIndex, element)

    protected fun BufferedSource.readNbtTagType(): NbtTagType {
        val tagId = readByte()

        return tagId.toNbtTagTypeOrNull()
            ?: throw NbtDecodingException(context, "Unknown NBT tag type ID: 0x${tagId.toHex()}")
    }

    private fun checkTagType(expected: NbtTagType) {
        val actual = tagTypeStack.lastOrNull()
        if (expected != actual && actual != null) {
            throw NbtDecodingException(context, "Expected $expected, but got $actual")
        }
    }

    abstract override fun beginRootTag(): NbtReader.RootTagInfo

    override fun beginCompound() {
        checkTagType(TAG_Compound)
        tagTypeStack += TAG_End
    }

    override fun beginCompoundEntry(): NbtReader.NamedTagInfo {
        val type = source.readNbtTagType()
        return if (type == TAG_End) {
            NbtReader.NamedTagInfo.End
        } else {
            tagTypeStack.replaceLast(type)
            NbtReader.NamedTagInfo(type, source.readNbtString())
        }
    }

    override fun endCompound() {
        tagTypeStack.removeLast()
    }

    private fun beginCollection(elementType: NbtTagType, size: Int) {
        tagTypeStack += elementType
        elementsRemainingStack += size
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

    final override fun beginListEntry(): Boolean =
        error("Should not be called unless size is unknown")

    final override fun endList(): Unit = endCollection()

    final override fun beginByteArray(): NbtReader.ArrayInfo {
        checkTagType(TAG_Byte_Array)

        val size = source.readNbtInt()
        beginCollection(TAG_Byte, size)

        return NbtReader.ArrayInfo(size)
    }

    final override fun beginByteArrayEntry(): Boolean =
        error("Should not be called unless size is unknown")

    final override fun endByteArray(): Unit = endCollection()

    final override fun beginIntArray(): NbtReader.ArrayInfo {
        checkTagType(TAG_Int_Array)

        val size = source.readNbtInt()
        beginCollection(TAG_Int, size)

        return NbtReader.ArrayInfo(size)
    }

    final override fun beginIntArrayEntry(): Boolean =
        error("Should not be called unless size is unknown")

    final override fun endIntArray(): Unit = endCollection()

    final override fun beginLongArray(): NbtReader.ArrayInfo {
        checkTagType(TAG_Long_Array)

        val size = source.readNbtInt()
        beginCollection(TAG_Long, size)

        return NbtReader.ArrayInfo(size)
    }

    final override fun beginLongArrayEntry(): Boolean =
        error("Should not be called unless size is unknown")

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

    final override fun beginCompoundEntry(): NbtReader.NamedTagInfo {
        if (compoundNesting == 1) {
            if (readRootEntry) return NbtReader.NamedTagInfo.End
            readRootEntry = true
        }

        return super.beginCompoundEntry()
    }

    final override fun endCompound() {
        super.endCompound()
        compoundNesting--
    }
}

internal class JavaNbtReader(
    override val context: NbtContext,
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
        override val context: NbtContext,
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
        override val context: NbtContext,
        override val source: BufferedSource
    ) : JavaNetworkNbtReader() {
        override fun beginRootTag(): NbtReader.RootTagInfo =
            NbtReader.RootTagInfo(source.readNbtTagType())
    }
}

internal class BedrockNbtReader(
    override val context: NbtContext,
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
    override val context: NbtContext,
    override val source: BufferedSource
) : NamedBinaryNbtReader() {
    override fun BufferedSource.readNbtShort(): Short =
        readShortLe()

    override fun BufferedSource.readNbtInt(): Int =
        readLEB128(context, 5).zigZagDecode().toInt()

    override fun BufferedSource.readNbtLong(): Long =
        readLEB128(context, 10).zigZagDecode()

    override fun BufferedSource.readNbtFloat(): Float =
        Float.fromBits(readLEB128(context, 5).zigZagDecode().toInt())

    override fun BufferedSource.readNbtDouble(): Double =
        Double.fromBits(readLEB128(context, 10).zigZagDecode())

    override fun BufferedSource.readNbtString(): String {
        val byteCount = readLEB128(context, 5).toLong()
        return readUtf8(byteCount)
    }
}
