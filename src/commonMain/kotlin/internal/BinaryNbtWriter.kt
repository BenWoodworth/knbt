package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.NbtEncodingException
import net.benwoodworth.knbt.internal.NbtTagType.TAG_Compound
import net.benwoodworth.knbt.internal.NbtTagType.TAG_End
import okio.BufferedSink
import okio.Sink
import okio.buffer

internal class BinaryNbtWriter(sink: Sink) : NbtWriter {
    private val buffer = sink.buffer()

    private var compoundNesting = 0
    private var wroteRootEntry = false

    private fun BufferedSink.writeNbtString(value: String) {
        val bytes = value.encodeToByteArray()
        if (bytes.size > UShort.MAX_VALUE.toInt()) throw NbtEncodingException("String too long to encode")

        writeShort(bytes.size.toShort())
        write(bytes)
    }

    private fun BufferedSink.writeNbtTagType(value: NbtTagType) {
        writeByte(value.id.toInt())
    }

    override fun beginRootTag(type: NbtTagType) {
        if (type != TAG_Compound) throw NbtEncodingException("The binary NBT format only supports $TAG_Compound with one entry")
    }

    override fun beginCompound() {
        compoundNesting++
    }

    override fun beginCompoundEntry(type: NbtTagType, name: String) {
        if (compoundNesting == 1) {
            if (wroteRootEntry) throw NbtEncodingException("The binary NBT format only supports $TAG_Compound with one entry")
            wroteRootEntry = true
        }

        buffer.writeByte(type.id.toInt())
        buffer.writeNbtString(name)
    }

    override fun endCompound() {
        if (compoundNesting == 1 && !wroteRootEntry) throw NbtEncodingException("The binary NBT format only supports $TAG_Compound with one entry")

        compoundNesting--
        if (compoundNesting > 0) {
            buffer.writeNbtTagType(TAG_End)
        } else {
            buffer.flush()
        }
    }

    override fun beginList(type: NbtTagType, size: Int) {
        buffer.writeNbtTagType(type)
        buffer.writeInt(size)
    }

    override fun beginListEntry(): Unit = Unit

    override fun endList(): Unit = Unit

    override fun beginByteArray(size: Int) {
        buffer.writeInt(size)
    }

    override fun beginByteArrayEntry(): Unit = Unit

    override fun endByteArray(): Unit = Unit

    override fun beginIntArray(size: Int) {
        buffer.writeInt(size)
    }

    override fun beginIntArrayEntry(): Unit = Unit

    override fun endIntArray(): Unit = Unit

    override fun beginLongArray(size: Int) {
        buffer.writeInt(size)
    }

    override fun beginLongArrayEntry(): Unit = Unit

    override fun endLongArray(): Unit = Unit

    override fun writeByte(value: Byte) {
        buffer.writeByte(value.toInt())
    }

    override fun writeShort(value: Short) {
        buffer.writeShort(value.toInt())
    }

    override fun writeInt(value: Int) {
        buffer.writeInt(value)
    }

    override fun writeLong(value: Long) {
        buffer.writeLong(value)
    }

    override fun writeFloat(value: Float) {
        buffer.writeInt(value.toRawBits())
    }

    override fun writeDouble(value: Double) {
        buffer.writeLong(value.toRawBits())
    }

    override fun writeString(value: String) {
        buffer.writeNbtString(value)
    }
}
