package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.NbtReader
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.NbtWriter

internal class LoggingNbtReader(
    private val reader: NbtReader,
    private val appendable: Appendable,
) : NbtReader {
    override fun beginRootTag(): NbtReader.RootTagInfo {
        appendable.append("beginRootTag() -> ")
        return reader.beginRootTag()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun beginCompound() {
        appendable.appendLine("beginCompound()")
        reader.beginCompound()
    }

    override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo {
        appendable.append("beginCompoundEntry() -> ")
        return reader.beginCompoundEntry()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun endCompound() {
        appendable.appendLine("endCompound()")
        reader.endCompound()
    }

    override fun beginList(): NbtReader.ListInfo {
        appendable.append("beginList() -> ")
        return reader.beginList()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun beginListEntry(): Boolean {
        appendable.append("beginListEntry() -> ")
        return reader.beginListEntry()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun endList() {
        appendable.appendLine("endList()")
        reader.endList()
    }

    override fun beginByteArray(): NbtReader.ArrayInfo {
        appendable.append("beginByteArray() -> ")
        return reader.beginByteArray()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun beginByteArrayEntry(): Boolean {
        appendable.append("beginByteArrayEntry() -> ")
        return reader.beginByteArrayEntry()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun endByteArray() {
        appendable.appendLine("endByteArray()")
        reader.endByteArray()
    }

    override fun beginIntArray(): NbtReader.ArrayInfo {
        appendable.append("beginIntArray() -> ")
        return reader.beginIntArray()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun beginIntArrayEntry(): Boolean {
        appendable.append("beginIntArrayEntry() -> ")
        return reader.beginIntArrayEntry()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun endIntArray() {
        appendable.appendLine("endIntArray()")
        reader.endIntArray()
    }

    override fun beginLongArray(): NbtReader.ArrayInfo {
        appendable.append("beginLongArray() -> ")
        return reader.beginLongArray()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun beginLongArrayEntry(): Boolean {
        appendable.append("beginLongArrayEntry() -> ")
        return reader.beginLongArrayEntry()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun endLongArray() {
        appendable.appendLine("endLongArray()")
        reader.endLongArray()
    }

    override fun readByte(): Byte {
        appendable.append("readByte() -> ")
        return reader.readByte()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun readShort(): Short {
        appendable.append("readShort() -> ")
        return reader.readShort()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun readInt(): Int {
        appendable.append("readInt() -> ")
        return reader.readInt()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun readLong(): Long {
        appendable.append("readLong() -> ")
        return reader.readLong()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun readFloat(): Float {
        appendable.append("readFloat() -> ")
        return reader.readFloat()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun readDouble(): Double {
        appendable.append("readDouble() -> ")
        return reader.readDouble()
            .also { appendable.appendLine(it.toString()) }
    }

    override fun readString(): String {
        appendable.append("readString() -> ")
        return reader.readString()
            .also { appendable.appendLine(it) }
    }
}

internal class LoggingNbtWriter(
    private val writer: NbtWriter,
    private val appendable: Appendable,
) : NbtWriter {
    override fun beginRootTag(type: NbtTagType) {
        appendable.appendLine("beginRootTag($type)")
        writer.beginRootTag(type)
    }

    override fun beginCompound() {
        appendable.appendLine("beginCompound()")
        writer.beginCompound()
    }

    override fun beginCompoundEntry(type: NbtTagType, name: String) {
        appendable.appendLine("beginCompoundEntry($type, $name)")
        writer.beginCompoundEntry(type, name)
    }

    override fun endCompound() {
        appendable.appendLine("endCompound()")
        writer.endCompound()
    }

    override fun beginList(type: NbtTagType, size: Int) {
        appendable.appendLine("beginList($type, $size)")
        writer.beginList(type, size)
    }

    override fun beginListEntry() {
        appendable.appendLine("beginListEntry()")
        writer.beginListEntry()
    }

    override fun endList() {
        appendable.appendLine("endList()")
        writer.endList()
    }

    override fun beginByteArray(size: Int) {
        appendable.appendLine("beginByteArray($size)")
        writer.beginByteArray(size)
    }

    override fun beginByteArrayEntry() {
        appendable.appendLine("beginByteArrayEntry()")
        writer.beginByteArrayEntry()
    }

    override fun endByteArray() {
        appendable.appendLine("endByteArray()")
        writer.endByteArray()
    }

    override fun beginIntArray(size: Int) {
        appendable.appendLine("beginIntArray($size)")
        writer.beginIntArray(size)
    }

    override fun beginIntArrayEntry() {
        appendable.appendLine("beginIntArrayEntry()")
        writer.beginIntArrayEntry()
    }

    override fun endIntArray() {
        appendable.appendLine("endIntArray()")
        writer.endIntArray()
    }

    override fun beginLongArray(size: Int) {
        appendable.appendLine("beginLongArray($size)")
        writer.beginLongArray(size)
    }

    override fun beginLongArrayEntry() {
        appendable.appendLine("beginLongArrayEntry()")
        writer.beginLongArrayEntry()
    }

    override fun endLongArray() {
        appendable.appendLine("endLongArray()")
        writer.endLongArray()
    }

    override fun writeByte(value: Byte) {
        appendable.appendLine("writeByte($value)")
        writer.writeByte(value)
    }

    override fun writeShort(value: Short) {
        appendable.appendLine("writeShort($value)")
        writer.writeShort(value)
    }

    override fun writeInt(value: Int) {
        appendable.appendLine("writeInt($value)")
        writer.writeInt(value)
    }

    override fun writeLong(value: Long) {
        appendable.appendLine("writeLong($value)")
        writer.writeLong(value)
    }

    override fun writeFloat(value: Float) {
        appendable.appendLine("writeFloat($value)")
        writer.writeFloat(value)
    }

    override fun writeDouble(value: Double) {
        appendable.appendLine("writeDouble($value)")
        writer.writeDouble(value)
    }

    override fun writeString(value: String) {
        appendable.appendLine("writeString($value)")
        writer.writeString(value)
    }
}
