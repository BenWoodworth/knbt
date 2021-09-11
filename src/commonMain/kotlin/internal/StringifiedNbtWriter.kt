package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.ExperimentalNbtApi
import net.benwoodworth.knbt.StringifiedNbt

@OptIn(ExperimentalNbtApi::class)
internal class StringifiedNbtWriter(
    private val snbt: StringifiedNbt,
    private val appendable: Appendable,
) : NbtWriter {
    private var firstEntry = false
    private var inArray = false
    private var level = 0

    private val prettySpace: String =
        if (snbt.configuration.prettyPrint) " " else ""

    private fun Appendable.appendPrettyNewLine(): Appendable {
        if (snbt.configuration.prettyPrint) {
            appendable.appendLine()
            repeat(level) { appendable.append(snbt.configuration.prettyPrintIndent) }
        }
        return this
    }

    private fun beginCollection(prefix: String, array: Boolean) {
        appendable.append(prefix)
        firstEntry = true
        inArray = array
        level++
    }

    private fun beginCollectionEntry() {
        if (!firstEntry) {
            if (inArray) {
                appendable.append(",$prettySpace")
            } else {
                appendable.append(',')
            }
        }

        if (!inArray) {
            appendable.appendPrettyNewLine()
        }

        firstEntry = false
    }

    private fun endCollection(suffix: String, separateLine: Boolean) {
        level--
        if (separateLine) appendable.appendPrettyNewLine()
        appendable.append(suffix)

        firstEntry = false
        inArray = false
    }

    override fun beginRootTag(type: NbtTagType): Unit = Unit

    override fun beginCompound(): Unit = beginCollection("{", false)

    override fun beginCompoundEntry(type: NbtTagType, name: String) {
        beginCollectionEntry()
        appendable.appendNbtString(name).append(":$prettySpace")
    }

    override fun endCompound(): Unit = endCollection("}", true)

    override fun beginList(type: NbtTagType, size: Int): Unit = beginCollection("[", false)
    override fun beginListEntry(): Unit = beginCollectionEntry()
    override fun endList(): Unit = endCollection("]", true)

    override fun beginByteArray(size: Int): Unit = beginCollection("[B;$prettySpace", true)
    override fun beginByteArrayEntry(): Unit = beginCollectionEntry()
    override fun endByteArray(): Unit = endCollection("]", false)

    override fun beginIntArray(size: Int): Unit = beginCollection("[I;$prettySpace", true)
    override fun beginIntArrayEntry(): Unit = beginCollectionEntry()
    override fun endIntArray(): Unit = endCollection("]", false)

    override fun beginLongArray(size: Int): Unit = beginCollection("[L;$prettySpace", true)
    override fun beginLongArrayEntry(): Unit = beginCollectionEntry()
    override fun endLongArray(): Unit = endCollection("]", false)

    override fun writeByte(value: Byte) {
        appendable.append(value.toString()).append(if (inArray) 'B' else 'b')
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
        appendable.appendNbtString(value, forceQuote = true)
    }
}

internal fun Appendable.appendNbtString(value: String, forceQuote: Boolean = false): Appendable {
    fun Appendable.appendQuoted(): Appendable = apply {
        append('"')
        value.forEach {
            if (it == '"') append("\\\"") else append(it)
        }
        append('"')
    }

    fun Char.isSafeCharacter(): Boolean = when (this) {
        '-', '_', in 'a'..'z', in 'A'..'Z', in '0'..'9' -> true
        else -> false
    }

    return when {
        forceQuote -> appendQuoted()
        value.isEmpty() -> append("\"\"")
        value.all { it.isSafeCharacter() } -> append(value)
        !value.contains('"') -> append('"').append(value).append('"')
        !value.contains('\'') -> append('\'').append(value).append('\'')
        else -> appendQuoted()
    }
}

internal fun String.toNbtString(forceQuote: Boolean = false): String =
    buildString { appendNbtString(this@toNbtString, forceQuote) }
