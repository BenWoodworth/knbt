package net.benwoodworth.knbt.internal

internal class StringifiedNbtWriter(
    private val appendable: Appendable,
) : NbtWriter {
    private var firstEntry = false

    private fun Appendable.appendNbtString(value: String): Appendable = when {
        value.all { it in 'a'..'z' || it in 'A'..'Z' || it in '0'..'9' } -> append(value)
        !value.contains('"') -> append('"').append(value).append('"')
        !value.contains('\'') -> append('\'').append(value).append('\'')
        else -> {
            append('"')
            value.forEach {
                if (it == '"') append("\\\"") else append(it)
            }
            append('"')
        }
    }

    private fun beginCollection(prefix: String) {
        appendable.append(prefix)
        firstEntry = true
    }

    private fun beginCollectionEntry() {
        if (!firstEntry) appendable.append(',')
        firstEntry = false
    }

    private fun endCollection(suffix: String) {
        appendable.append(suffix)
        firstEntry = false
    }

    override fun beginRootTag(type: NbtTagType): Unit = Unit

    override fun beginCompound(): Unit = beginCollection("{")

    override fun beginCompoundEntry(type: NbtTagType, name: String) {
        beginCollectionEntry()
        appendable.appendNbtString(name).append(':')
    }

    override fun endCompound(): Unit = endCollection("}")

    override fun beginList(type: NbtTagType, size: Int): Unit = beginCollection("[")
    override fun beginListEntry(): Unit = beginCollectionEntry()
    override fun endList(): Unit = endCollection("]")

    override fun beginByteArray(size: Int): Unit = beginCollection("[B;")
    override fun beginByteArrayEntry(): Unit = beginCollectionEntry()
    override fun endByteArray(): Unit = endCollection("]")

    override fun beginIntArray(size: Int): Unit = beginCollection("[I;")
    override fun beginIntArrayEntry(): Unit = beginCollectionEntry()
    override fun endIntArray(): Unit = endCollection("]")

    override fun beginLongArray(size: Int): Unit = beginCollection("[L;")
    override fun beginLongArrayEntry(): Unit = beginCollectionEntry()
    override fun endLongArray(): Unit = endCollection("]")

    override fun writeByte(value: Byte) {
        appendable.append(value.toString()).append("b")
    }

    override fun writeShort(value: Short) {
        appendable.append(value.toString()).append('s')
    }

    override fun writeInt(value: Int) {
        appendable.append(value.toString())
    }

    override fun writeLong(value: Long) {
        appendable.append(value.toString()).append('L')
    }

    override fun writeFloat(value: Float) {
        appendable.append(value.toString()).append('f')
    }

    override fun writeDouble(value: Double) {
        appendable.append(value.toString()).append('d')
    }

    override fun writeString(value: String) {
        appendable.appendNbtString(value)
    }
}
