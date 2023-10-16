package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.NbtTagType.TAG_Compound
import net.benwoodworth.knbt.internal.NbtTagType.TAG_End
import okio.BufferedSink
import okio.Closeable

internal abstract class BinaryNbtWriter : NbtWriter, Closeable {
    protected abstract val sink: BufferedSink

    private var compoundNesting = 0
    private var wroteRootEntry = false

    override fun close(): Unit = sink.close()

    private fun BufferedSink.writeNbtTagType(value: NbtTagType) {
        writeByte(value.id.toInt())
    }

    override fun beginRootTag(type: NbtTagType) {
        if (type != TAG_Compound) throw NbtEncodingException("The binary NBT format only supports $TAG_Compound with one entry")
    }

    final override fun beginCompound() {
        compoundNesting++
    }

    final override fun beginCompoundEntry(type: NbtTagType, name: String) {
        if (compoundNesting == 1) {
            if (wroteRootEntry) throw NbtEncodingException("The binary NBT format only supports $TAG_Compound with one entry")
            wroteRootEntry = true
        }

        sink.writeNbtTagType(type)
        sink.writeNbtString(name)
    }

    final override fun endCompound() {
        if (compoundNesting == 1 && !wroteRootEntry) throw NbtEncodingException("The binary NBT format only supports $TAG_Compound with one entry")

        compoundNesting--
        if (compoundNesting > 0) {
            sink.writeNbtTagType(TAG_End)
        }
    }

    final override fun beginList(type: NbtTagType, size: Int) {
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

internal class JavaNbtWriter(
    override val sink: BufferedSink
) : BinaryNbtWriter() {
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

internal class BedrockNbtWriter(
    override val sink: BufferedSink
) : BinaryNbtWriter() {
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
) : BinaryNbtWriter() {
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
