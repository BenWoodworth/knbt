package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader.*

internal class TreeNbtReader(tag: NbtTag) : NbtReader {
    private var reader: NbtTagReader = RootNbtTagReader(tag)

    override fun beginRootTag(): RootTagInfo = reader.beginRootTag()
    override fun beginCompound() = reader.beginCompound()
    override fun beginCompoundEntry(): CompoundEntryInfo = reader.beginCompoundEntry()
    override fun endCompound() = reader.endCompound()
    override fun beginList(): ListInfo = reader.beginList()
    override fun beginListEntry(): Boolean = reader.beginListEntry()
    override fun endList() = reader.endList()
    override fun beginByteArray(): ArrayInfo = reader.beginByteArray()
    override fun beginByteArrayEntry(): Boolean = reader.beginByteArrayEntry()
    override fun endByteArray() = reader.endByteArray()
    override fun beginIntArray(): ArrayInfo = reader.beginIntArray()
    override fun beginIntArrayEntry(): Boolean = reader.beginIntArrayEntry()
    override fun endIntArray() = reader.endIntArray()
    override fun beginLongArray(): ArrayInfo = reader.beginLongArray()
    override fun beginLongArrayEntry(): Boolean = reader.beginLongArrayEntry()
    override fun endLongArray() = reader.endLongArray()
    override fun readByte(): Byte = reader.readByte()
    override fun readShort(): Short = reader.readShort()
    override fun readInt(): Int = reader.readInt()
    override fun readLong(): Long = reader.readLong()
    override fun readFloat(): Float = reader.readFloat()
    override fun readDouble(): Double = reader.readDouble()
    override fun readString(): String = reader.readString()

    private sealed interface NbtTagReader {
        fun beginRootTag(): RootTagInfo = error("${this::class} does not support beginRootTag()")
        fun beginCompound(): Unit = error("${this::class} does not support beginCompound()")
        fun beginCompoundEntry(): CompoundEntryInfo = error("${this::class} does not support beginCompoundEntry()")
        fun endCompound(): Unit = error("${this::class} does not support endCompound()")
        fun beginList(): ListInfo = error("${this::class} does not support beginList()")
        fun beginListEntry(): Boolean = error("${this::class} does not support beginListEntry()")
        fun endList(): Unit = error("${this::class} does not support endList()")
        fun beginByteArray(): ArrayInfo = error("${this::class} does not support beginByteArray()")
        fun beginByteArrayEntry(): Boolean = error("${this::class} does not support beginByteArrayEntry()")
        fun endByteArray(): Unit = error("${this::class} does not support endByteArray()")
        fun beginIntArray(): ArrayInfo = error("${this::class} does not support beginIntArray()")
        fun beginIntArrayEntry(): Boolean = error("${this::class} does not support beginIntArrayEntry()")
        fun endIntArray(): Unit = error("${this::class} does not support endIntArray()")
        fun beginLongArray(): ArrayInfo = error("${this::class} does not support beginLongArray()")
        fun beginLongArrayEntry(): Boolean = error("${this::class} does not support beginLongArrayEntry()")
        fun endLongArray(): Unit = error("${this::class} does not support endLongArray()")
        fun readByte(): Byte = error("${this::class} does not support readByte()")
        fun readShort(): Short = error("${this::class} does not support readShort()")
        fun readInt(): Int = error("${this::class} does not support readInt()")
        fun readLong(): Long = error("${this::class} does not support readLong()")
        fun readFloat(): Float = error("${this::class} does not support readFloat()")
        fun readDouble(): Double = error("${this::class} does not support readDouble()")
        fun readString(): String = error("${this::class} does not support readString()")
    }

    private inner class RootNbtTagReader(private val tag: NbtTag) : NbtTagReader {
        override fun beginRootTag(): RootTagInfo = RootTagInfo(tag.type)

        override fun beginCompound() {
            reader = NbtCompoundReader(this, tag as NbtCompound)
        }

        override fun beginList(): ListInfo {
            reader = NbtListReader(this, tag as NbtList<*>)
            return ListInfo(tag.elementType, tag.size)
        }

        override fun beginByteArray(): ArrayInfo {
            reader = NbtByteArrayReader(this, tag as NbtByteArray)
            return ArrayInfo(tag.size)
        }

        override fun beginIntArray(): ArrayInfo {
            reader = NbtIntArrayReader(this, tag as NbtIntArray)
            return ArrayInfo(tag.size)
        }

        override fun beginLongArray(): ArrayInfo {
            reader = NbtLongArrayReader(this, tag as NbtLongArray)
            return ArrayInfo(tag.size)
        }

        override fun readByte(): Byte = (tag as NbtByte).value
        override fun readShort(): Short = (tag as NbtShort).value
        override fun readInt(): Int = (tag as NbtInt).value
        override fun readLong(): Long = (tag as NbtLong).value
        override fun readFloat(): Float = (tag as NbtFloat).value
        override fun readDouble(): Double = (tag as NbtDouble).value
        override fun readString(): String = (tag as NbtString).value
    }

    private inner class NbtCompoundReader(val parent: NbtTagReader, tag: NbtCompound) : NbtTagReader {
        private val iterator = tag.content.iterator()
        private var next = if (iterator.hasNext()) iterator.next() else null

        private inline fun <reified T : NbtTag> readEntry(): T {
            return (next!!.value as T)
                .also { next = if (iterator.hasNext()) iterator.next() else null }
        }

        override fun beginCompoundEntry(): CompoundEntryInfo =
            next?.let { (name, tag) -> CompoundEntryInfo(tag.type, name) } ?: CompoundEntryInfo.End

        override fun endCompound() {
            reader = parent
        }

        override fun beginCompound() {
            val entry = readEntry<NbtCompound>()
            reader = NbtCompoundReader(this, entry)
        }

        override fun beginList(): ListInfo {
            val entry = readEntry<NbtList<*>>()
            reader = NbtListReader(this, entry)
            return ListInfo(entry.elementType, entry.size)
        }

        override fun beginByteArray(): ArrayInfo {
            val entry = readEntry<NbtByteArray>()
            reader = NbtByteArrayReader(this, entry)
            return ArrayInfo(entry.size)
        }

        override fun beginIntArray(): ArrayInfo {
            val entry = readEntry<NbtIntArray>()
            reader = NbtIntArrayReader(this, entry)
            return ArrayInfo(entry.size)
        }

        override fun beginLongArray(): ArrayInfo {
            val entry = readEntry<NbtLongArray>()
            reader = NbtLongArrayReader(this, entry)
            return ArrayInfo(entry.size)
        }

        override fun readByte(): Byte = readEntry<NbtByte>().value
        override fun readShort(): Short = readEntry<NbtShort>().value
        override fun readInt(): Int = readEntry<NbtInt>().value
        override fun readLong(): Long = readEntry<NbtLong>().value
        override fun readFloat(): Float = readEntry<NbtFloat>().value
        override fun readDouble(): Double = readEntry<NbtDouble>().value
        override fun readString(): String = readEntry<NbtString>().value
    }

    private inner class NbtListReader(val parent: NbtTagReader, tag: NbtList<*>) : NbtTagReader {
        private val iterator = tag.content.iterator()
        private var next = if (iterator.hasNext()) iterator.next() else null

        private inline fun <reified T : NbtTag> readEntry(): T {
            return (next as T)
                .also { next = if (iterator.hasNext()) iterator.next() else null }
        }

        override fun beginListEntry(): Boolean =
            error("Should not be called unless size is unknown")

        override fun endList() {
            reader = parent
        }

        override fun beginCompound() {
            val entry = readEntry<NbtCompound>()
            reader = NbtCompoundReader(this, entry)
        }

        override fun beginList(): ListInfo {
            val entry = readEntry<NbtList<*>>()
            reader = NbtListReader(this, entry)
            return ListInfo(entry.elementType, entry.size)
        }

        override fun beginByteArray(): ArrayInfo {
            val entry = readEntry<NbtByteArray>()
            reader = NbtByteArrayReader(this, entry)
            return ArrayInfo(entry.size)
        }

        override fun beginIntArray(): ArrayInfo {
            val entry = readEntry<NbtIntArray>()
            reader = NbtIntArrayReader(this, entry)
            return ArrayInfo(entry.size)
        }

        override fun beginLongArray(): ArrayInfo {
            val entry = readEntry<NbtLongArray>()
            reader = NbtLongArrayReader(this, entry)
            return ArrayInfo(entry.size)
        }

        override fun readByte(): Byte = readEntry<NbtByte>().value
        override fun readShort(): Short = readEntry<NbtShort>().value
        override fun readInt(): Int = readEntry<NbtInt>().value
        override fun readLong(): Long = readEntry<NbtLong>().value
        override fun readFloat(): Float = readEntry<NbtFloat>().value
        override fun readDouble(): Double = readEntry<NbtDouble>().value
        override fun readString(): String = readEntry<NbtString>().value
    }

    private inner class NbtByteArrayReader(val parent: NbtTagReader, tag: NbtByteArray) : NbtTagReader {
        private val array = tag
        private var index = 0

        override fun beginByteArrayEntry(): Boolean =
            error("Should not be called unless size is unknown")

        override fun endByteArray() {
            reader = parent
        }

        override fun readByte(): Byte = array[index++]
    }


    private inner class NbtIntArrayReader(val parent: NbtTagReader, tag: NbtIntArray) : NbtTagReader {
        private val array = tag
        private var index = 0

        override fun beginIntArrayEntry(): Boolean =
            error("Should not be called unless size is unknown")

        override fun endIntArray() {
            reader = parent
        }

        override fun readInt(): Int = array[index++]
    }


    private inner class NbtLongArrayReader(val parent: NbtTagReader, tag: NbtLongArray) : NbtTagReader {
        private val array = tag
        private var index = 0

        override fun beginLongArrayEntry(): Boolean =
            error("Should not be called unless size is unknown")

        override fun endLongArray() {
            reader = parent
        }

        override fun readLong(): Long = array[index++]
    }
}
