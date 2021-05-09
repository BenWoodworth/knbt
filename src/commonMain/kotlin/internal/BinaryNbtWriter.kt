package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.NbtEncodingException
import net.benwoodworth.knbt.internal.NbtTagType.TAG_Compound
import net.benwoodworth.knbt.internal.NbtTagType.TAG_End
import okio.BufferedSink

internal class BinaryNbtWriter(
    private val sink: BufferedSink,
) : NbtWriter {
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

        sink.writeByte(type.id.toInt())
        sink.writeNbtString(name)
    }

    override fun endCompound() {
        if (compoundNesting == 1 && !wroteRootEntry) throw NbtEncodingException("The binary NBT format only supports $TAG_Compound with one entry")

        compoundNesting--
        if (compoundNesting > 0) {
            sink.writeNbtTagType(TAG_End)
        } else {
            sink.flush()
        }
    }

    override fun beginList(type: NbtTagType, size: Int) {
        sink.writeNbtTagType(type)
        sink.writeInt(size)
    }

    override fun beginListEntry(): Unit = Unit

    override fun endList(): Unit = Unit

    override fun beginByteArray(size: Int) {
        sink.writeInt(size)
    }

    override fun beginByteArrayEntry(): Unit = Unit

    override fun endByteArray(): Unit = Unit

    override fun beginIntArray(size: Int) {
        sink.writeInt(size)
    }

    override fun beginIntArrayEntry(): Unit = Unit

    override fun endIntArray(): Unit = Unit

    override fun beginLongArray(size: Int) {
        sink.writeInt(size)
    }

    override fun beginLongArrayEntry(): Unit = Unit

    override fun endLongArray(): Unit = Unit

    override fun writeByte(value: Byte) {
        sink.writeByte(value.toInt())
    }

    override fun writeShort(value: Short) {
        sink.writeShort(value.toInt())
    }

    override fun writeInt(value: Int) {
        sink.writeInt(value)
    }

    override fun writeLong(value: Long) {
        sink.writeLong(value)
    }

    override fun writeFloat(value: Float) {
        sink.writeInt(value.toRawBits())
    }

    override fun writeDouble(value: Double) {
        sink.writeLong(value.toRawBits())
    }

    override fun writeString(value: String) {
        sink.writeNbtString(value)
    }
}
