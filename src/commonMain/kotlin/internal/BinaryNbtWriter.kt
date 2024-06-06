package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.NbtTagType.TAG_End
import okio.BufferedSink
import okio.Closeable

internal abstract class BinaryNbtWriter : NbtWriter, Closeable {
    protected abstract val sink: BufferedSink

    override fun close(): Unit = sink.close()

    protected fun BufferedSink.writeNbtTagType(value: NbtTagType) {
        writeByte(value.id.toInt())
    }

    abstract override fun beginRootTag(type: NbtTagType)

    override fun beginCompound(): Unit = Unit

    override fun beginCompoundEntry(type: NbtTagType, name: String) {
        sink.writeNbtTagType(type)
        sink.writeNbtString(name)
    }

    override fun endCompound() {
        sink.writeNbtTagType(TAG_End)
    }

    override fun beginList(type: NbtTagType, size: Int) {
        sink.writeNbtTagType(type)
        sink.writeNbtInt(size)
    }

    final override fun beginListEntry(): Unit = Unit

    final override fun endList(): Unit = Unit

    final override fun beginByteArray(size: Int) {
        sink.writeNbtInt(size)
    }

    final override fun beginByteArrayEntry(): Unit = Unit

    final override fun endByteArray(): Unit = Unit

    final override fun beginIntArray(size: Int) {
        sink.writeNbtInt(size)
    }

    final override fun beginIntArrayEntry(): Unit = Unit

    final override fun endIntArray(): Unit = Unit

    final override fun beginLongArray(size: Int) {
        sink.writeNbtInt(size)
    }

    final override fun beginLongArrayEntry(): Unit = Unit

    final override fun endLongArray(): Unit = Unit

    final override fun writeByte(value: Byte) {
        sink.writeByte(value.toInt())
    }

    final override fun writeShort(value: Short) {
        sink.writeNbtShort(value)
    }

    final override fun writeInt(value: Int) {
        sink.writeNbtInt(value)
    }

    final override fun writeLong(value: Long) {
        sink.writeNbtLong(value)
    }

    final override fun writeFloat(value: Float) {
        sink.writeNbtFloat(value)
    }

    final override fun writeDouble(value: Double) {
        sink.writeNbtDouble(value)
    }

    final override fun writeString(value: String) {
        sink.writeNbtString(value)
    }

    protected abstract fun BufferedSink.writeNbtShort(value: Short): BufferedSink
    protected abstract fun BufferedSink.writeNbtInt(value: Int): BufferedSink
    protected abstract fun BufferedSink.writeNbtLong(value: Long): BufferedSink
    protected abstract fun BufferedSink.writeNbtFloat(value: Float): BufferedSink
    protected abstract fun BufferedSink.writeNbtDouble(value: Double): BufferedSink
    protected abstract fun BufferedSink.writeNbtString(value: String): BufferedSink
}

internal abstract class NamedBinaryNbtWriter : BinaryNbtWriter() {
    private var compoundNesting = 0

    override fun beginRootTag(type: NbtTagType) {
    }

    final override fun beginCompound() {
        super.beginCompound()
        compoundNesting++
    }

    final override fun endCompound() {
        compoundNesting--
        if (compoundNesting > 0) {
            sink.writeNbtTagType(TAG_End)
        }
    }
}

internal class JavaNbtWriter(
    override val sink: BufferedSink
) : NamedBinaryNbtWriter() {
    override fun BufferedSink.writeNbtShort(value: Short): BufferedSink =
        writeShort(value.toInt())

    override fun BufferedSink.writeNbtInt(value: Int): BufferedSink =
        writeInt(value)

    override fun BufferedSink.writeNbtLong(value: Long): BufferedSink =
        writeLong(value)

    override fun BufferedSink.writeNbtFloat(value: Float): BufferedSink =
        writeInt(value.toRawBits())

    override fun BufferedSink.writeNbtDouble(value: Double): BufferedSink =
        writeLong(value.toRawBits())

    override fun BufferedSink.writeNbtString(value: String): BufferedSink = apply {
        val bytes = value.encodeToByteArray()
        if (bytes.size > UShort.MAX_VALUE.toInt()) throw NbtEncodingException("String too long to encode")
        writeShort(bytes.size).write(bytes)
    }
}

internal abstract class JavaNetworkNbtWriter : BinaryNbtWriter() {
    override fun BufferedSink.writeNbtShort(value: Short): BufferedSink =
        writeShort(value.toInt())

    override fun BufferedSink.writeNbtInt(value: Int): BufferedSink =
        writeInt(value)

    override fun BufferedSink.writeNbtLong(value: Long): BufferedSink =
        writeLong(value)

    override fun BufferedSink.writeNbtFloat(value: Float): BufferedSink =
        writeInt(value.toRawBits())

    override fun BufferedSink.writeNbtDouble(value: Double): BufferedSink =
        writeLong(value.toRawBits())

    override fun BufferedSink.writeNbtString(value: String): BufferedSink = apply {
        val bytes = value.encodeToByteArray()
        if (bytes.size > UShort.MAX_VALUE.toInt()) throw NbtEncodingException("String too long to encode")
        writeShort(bytes.size).write(bytes)
    }

    class EmptyNamedRoot(
        override val sink: BufferedSink
    ) : JavaNetworkNbtWriter() {
        override fun beginRootTag(type: NbtTagType) {
            sink.writeNbtTagType(type)
            sink.writeNbtString("")
        }
    }

    class UnnamedRoot(
        override val sink: BufferedSink
    ) : JavaNetworkNbtWriter() {
        override fun beginRootTag(type: NbtTagType) {
            sink.writeNbtTagType(type)
        }
    }
}

internal class BedrockNbtWriter(
    override val sink: BufferedSink
) : NamedBinaryNbtWriter() {
    override fun BufferedSink.writeNbtShort(value: Short): BufferedSink =
        writeShortLe(value.toInt())

    override fun BufferedSink.writeNbtInt(value: Int): BufferedSink =
        writeIntLe(value)

    override fun BufferedSink.writeNbtLong(value: Long): BufferedSink =
        writeLongLe(value)

    override fun BufferedSink.writeNbtFloat(value: Float): BufferedSink =
        writeIntLe(value.toRawBits())

    override fun BufferedSink.writeNbtDouble(value: Double): BufferedSink =
        writeLongLe(value.toRawBits())

    override fun BufferedSink.writeNbtString(value: String): BufferedSink = apply {
        val bytes = value.encodeToByteArray()
        if (bytes.size > UShort.MAX_VALUE.toInt()) throw NbtEncodingException("String too long to encode")
        writeShortLe(bytes.size)
        write(bytes)
    }
}

internal class BedrockNetworkNbtWriter(
    override val sink: BufferedSink
) : NamedBinaryNbtWriter() {
    override fun BufferedSink.writeNbtShort(value: Short): BufferedSink =
        writeShortLe(value.toInt())

    override fun BufferedSink.writeNbtInt(value: Int): BufferedSink =
        writeLEB128(value.toLong().zigZagEncode())

    override fun BufferedSink.writeNbtLong(value: Long): BufferedSink =
        writeLEB128(value.zigZagEncode())

    override fun BufferedSink.writeNbtFloat(value: Float): BufferedSink =
        writeLEB128(value.toRawBits().toLong().zigZagEncode())

    override fun BufferedSink.writeNbtDouble(value: Double): BufferedSink =
        writeLEB128(value.toRawBits().zigZagEncode())

    override fun BufferedSink.writeNbtString(value: String): BufferedSink = apply {
        val bytes = value.encodeToByteArray()
        writeLEB128(bytes.size.toULong())
        write(bytes)
    }
}
