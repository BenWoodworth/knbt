package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader.*

internal class TreeNbtReader(tag: NbtNamed<NbtTag>) : NbtReader {
    private var reader: NbtTagReader = RootNbtTagReader(tag)

    override fun beginRootTag(): NamedTagInfo = reader.beginRootTag()
    override fun beginCompound() = reader.beginCompound()
    override fun beginCompoundEntry(): NamedTagInfo = reader.beginCompoundEntry()
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
        fun beginRootTag(): NamedTagInfo = error("${this::class} does not support beginRootTag()")
        fun beginCompound(): Unit = error("${this::class} does not support beginCompound()")
        fun beginCompoundEntry(): NamedTagInfo = error("${this::class} does not support beginCompoundEntry()")
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

    private inner class RootNbtTagReader(private val tag: NbtNamed<NbtTag>) : NbtTagReader {
        override fun beginRootTag(): NamedTagInfo = NamedTagInfo(tag.value.type, tag.name)

        override fun beginCompound() {
            reader = NbtCompoundReader(this, tag.value as NbtCompound)
        }

        override fun beginList(): ListInfo {
            val nbtList = tag.nbtList
            reader = NbtListReader(this, nbtList)
            return ListInfo(nbtList.elementType, nbtList.size)
        }

        override fun beginByteArray(): ArrayInfo {
            val nbtByteArray = tag.nbtByteArray
            reader = NbtByteArrayReader(this, nbtByteArray)
            return ArrayInfo(nbtByteArray.size)
        }

        override fun beginIntArray(): ArrayInfo {
            val nbtIntArray = tag.nbtIntArray
            reader = NbtIntArrayReader(this, nbtIntArray)
            return ArrayInfo(nbtIntArray.size)
        }

        override fun beginLongArray(): ArrayInfo {
            val nbtLongArray = tag.nbtLongArray
            reader = NbtLongArrayReader(this, nbtLongArray)
            return ArrayInfo(nbtLongArray.size)
        }

        override fun readByte(): Byte = tag.nbtByte.value
        override fun readShort(): Short = tag.nbtShort.value
        override fun readInt(): Int = tag.nbtInt.value
        override fun readLong(): Long = tag.nbtLong.value
        override fun readFloat(): Float = tag.nbtFloat.value
        override fun readDouble(): Double = tag.nbtDouble.value
        override fun readString(): String = tag.nbtString.value
    }

    private inner class NbtCompoundReader(val parent: NbtTagReader, tag: NbtCompound) : NbtTagReader {
        private val iterator = tag.content.iterator()
        private var next = if (iterator.hasNext()) iterator.next() else null

        private inline fun <reified T : NbtTag> readEntry(): T {
            return (next!!.value as T)
                .also { next = if (iterator.hasNext()) iterator.next() else null }
        }

        override fun beginCompoundEntry(): NamedTagInfo =
            next?.let { (name, tag) -> NamedTagInfo(tag.type, name) } ?: NamedTagInfo.End

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
