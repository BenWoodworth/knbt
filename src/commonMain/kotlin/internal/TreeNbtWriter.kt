package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*

internal class TreeNbtWriter(tagConsumer: (NbtTag) -> Unit) : NbtWriter {
    private var writer: NbtTagWriter = RootNbtTagWriter(tagConsumer)

    override fun beginRootTag(type: NbtTagType): Unit = writer.beginRootTag(type)
    override fun beginCompound(): Unit = writer.beginCompound()
    override fun beginCompoundEntry(type: NbtTagType, name: String): Unit = writer.beginCompoundEntry(type, name)
    override fun endCompound(): Unit = writer.endCompound()
    override fun beginList(type: NbtTagType, size: Int): Unit = writer.beginList(type, size)
    override fun beginListEntry(): Unit = writer.beginListEntry()
    override fun endList(): Unit = writer.endList()
    override fun beginByteArray(size: Int): Unit = writer.beginByteArray(size)
    override fun beginByteArrayEntry(): Unit = writer.beginByteArrayEntry()
    override fun endByteArray(): Unit = writer.endByteArray()
    override fun beginIntArray(size: Int): Unit = writer.beginIntArray(size)
    override fun beginIntArrayEntry(): Unit = writer.beginIntArrayEntry()
    override fun endIntArray(): Unit = writer.endIntArray()
    override fun beginLongArray(size: Int): Unit = writer.beginLongArray(size)
    override fun beginLongArrayEntry(): Unit = writer.beginLongArrayEntry()
    override fun endLongArray(): Unit = writer.endLongArray()
    override fun writeByte(value: Byte): Unit = writer.writeByte(value)
    override fun writeShort(value: Short): Unit = writer.writeShort(value)
    override fun writeInt(value: Int): Unit = writer.writeInt(value)
    override fun writeLong(value: Long): Unit = writer.writeLong(value)
    override fun writeFloat(value: Float): Unit = writer.writeFloat(value)
    override fun writeDouble(value: Double): Unit = writer.writeDouble(value)
    override fun writeString(value: String): Unit = writer.writeString(value)

    private sealed interface NbtTagWriter {
        fun consumeTag(tag: NbtTag): Unit = error("${this::class} does not support consumeTag()")

        fun beginRootTag(type: NbtTagType): Unit = error("${this::class} does not support beginRootTag()")
        fun beginCompound(): Unit = error("${this::class} does not support beginCompound()")
        fun beginCompoundEntry(type: NbtTagType, name: String): Unit =
            error("${this::class} does not support beginCompoundEntry()")

        fun endCompound(): Unit = error("${this::class} does not support endCompound()")
        fun beginList(type: NbtTagType, size: Int): Unit = error("${this::class} does not support beginList()")
        fun beginListEntry(): Unit = error("${this::class} does not support beginListEntry()")
        fun endList(): Unit = error("${this::class} does not support endList()")
        fun beginByteArray(size: Int): Unit = error("${this::class} does not support beginByteArray()")
        fun beginByteArrayEntry(): Unit = error("${this::class} does not support beginByteArrayEntry()")
        fun endByteArray(): Unit = error("${this::class} does not support endByteArray()")
        fun beginIntArray(size: Int): Unit = error("${this::class} does not support beginIntArray()")
        fun beginIntArrayEntry(): Unit = error("${this::class} does not support beginIntArrayEntry()")
        fun endIntArray(): Unit = error("${this::class} does not support endIntArray()")
        fun beginLongArray(size: Int): Unit = error("${this::class} does not support beginLongArray()")
        fun beginLongArrayEntry(): Unit = error("${this::class} does not support beginLongArrayEntry()")
        fun endLongArray(): Unit = error("${this::class} does not support endLongArray()")
        fun writeByte(value: Byte): Unit = error("${this::class} does not support writeByte()")
        fun writeShort(value: Short): Unit = error("${this::class} does not support writeShort()")
        fun writeInt(value: Int): Unit = error("${this::class} does not support writeInt()")
        fun writeLong(value: Long): Unit = error("${this::class} does not support writeLong()")
        fun writeFloat(value: Float): Unit = error("${this::class} does not support writeFloat()")
        fun writeDouble(value: Double): Unit = error("${this::class} does not support writeDouble()")
        fun writeString(value: String): Unit = error("${this::class} does not support writeString()")
    }

    private inner class RootNbtTagWriter(private val tagConsumer: (NbtTag) -> Unit) : NbtTagWriter {
        override fun consumeTag(tag: NbtTag): Unit = tagConsumer(tag)

        override fun beginRootTag(type: NbtTagType): Unit = Unit

        override fun beginCompound() {
            writer = NbtCompoundWriter(this)
        }

        override fun beginList(type: NbtTagType, size: Int) {
            writer = NbtListWriter(this, size)
        }

        override fun beginByteArray(size: Int) {
            writer = NbtByteArrayWriter(this, size)
        }

        override fun beginIntArray(size: Int) {
            writer = NbtIntArrayWriter(this, size)
        }

        override fun beginLongArray(size: Int) {
            writer = NbtLongArrayWriter(this, size)
        }

        override fun writeByte(value: Byte): Unit = consumeTag(NbtByte(value))
        override fun writeShort(value: Short): Unit = consumeTag(NbtShort(value))
        override fun writeInt(value: Int): Unit = consumeTag(NbtInt(value))
        override fun writeLong(value: Long): Unit = consumeTag(NbtLong(value))
        override fun writeFloat(value: Float): Unit = consumeTag(NbtFloat(value))
        override fun writeDouble(value: Double): Unit = consumeTag(NbtDouble(value))
        override fun writeString(value: String): Unit = consumeTag(NbtString(value))
    }

    private inner class NbtCompoundWriter(private val parent: NbtTagWriter) : NbtTagWriter {
        private val builder = NbtCompoundBuilder()
        private lateinit var entryName: String

        override fun consumeTag(tag: NbtTag) {
            builder.put(entryName, tag)
        }

        override fun beginCompoundEntry(type: NbtTagType, name: String) {
            entryName = name
        }

        override fun endCompound() {
            writer = parent
            parent.consumeTag(builder.build())
        }

        override fun beginCompound() {
            writer = NbtCompoundWriter(this)
        }

        override fun beginList(type: NbtTagType, size: Int) {
            writer = NbtListWriter(this, size)
        }

        override fun beginByteArray(size: Int) {
            writer = NbtByteArrayWriter(this, size)
        }

        override fun beginIntArray(size: Int) {
            writer = NbtIntArrayWriter(this, size)
        }

        override fun beginLongArray(size: Int) {
            writer = NbtLongArrayWriter(this, size)
        }

        override fun writeByte(value: Byte): Unit = consumeTag(NbtByte(value))
        override fun writeShort(value: Short): Unit = consumeTag(NbtShort(value))
        override fun writeInt(value: Int): Unit = consumeTag(NbtInt(value))
        override fun writeLong(value: Long): Unit = consumeTag(NbtLong(value))
        override fun writeFloat(value: Float): Unit = consumeTag(NbtFloat(value))
        override fun writeDouble(value: Double): Unit = consumeTag(NbtDouble(value))
        override fun writeString(value: String): Unit = consumeTag(NbtString(value))
    }

    private inner class NbtListWriter(private val parent: NbtTagWriter, size: Int) : NbtTagWriter {
        private val builder = NbtListBuilder<NbtTag>(size)

        override fun consumeTag(tag: NbtTag) {
            builder.addInternal(tag)
        }

        override fun beginListEntry(): Unit = Unit

        override fun endList() {
            writer = parent
            parent.consumeTag(builder.build())
        }

        override fun beginCompound() {
            writer = NbtCompoundWriter(this)
        }

        override fun beginList(type: NbtTagType, size: Int) {
            writer = NbtListWriter(this, size)
        }

        override fun beginByteArray(size: Int) {
            writer = NbtByteArrayWriter(this, size)
        }

        override fun beginIntArray(size: Int) {
            writer = NbtIntArrayWriter(this, size)
        }

        override fun beginLongArray(size: Int) {
            writer = NbtLongArrayWriter(this, size)
        }

        override fun writeByte(value: Byte): Unit = consumeTag(NbtByte(value))
        override fun writeShort(value: Short): Unit = consumeTag(NbtShort(value))
        override fun writeInt(value: Int): Unit = consumeTag(NbtInt(value))
        override fun writeLong(value: Long): Unit = consumeTag(NbtLong(value))
        override fun writeFloat(value: Float): Unit = consumeTag(NbtFloat(value))
        override fun writeDouble(value: Double): Unit = consumeTag(NbtDouble(value))
        override fun writeString(value: String): Unit = consumeTag(NbtString(value))
    }

    private inner class NbtByteArrayWriter(private val parent: NbtTagWriter, size: Int) : NbtTagWriter {
        private val array = ByteArray(size)
        private var index = 0

        override fun beginByteArrayEntry(): Unit = Unit

        override fun endByteArray() {
            writer = parent
            parent.consumeTag(NbtByteArray(array))
        }

        override fun writeByte(value: Byte) {
            array[index++] = value
        }
    }

    private inner class NbtIntArrayWriter(private val parent: NbtTagWriter, size: Int) : NbtTagWriter {
        private val array = IntArray(size)
        private var index = 0

        override fun beginIntArrayEntry(): Unit = Unit

        override fun endIntArray() {
            writer = parent
            parent.consumeTag(NbtIntArray(array))
        }

        override fun writeInt(value: Int) {
            array[index++] = value
        }
    }

    private inner class NbtLongArrayWriter(private val parent: NbtTagWriter, size: Int) : NbtTagWriter {
        private val array = LongArray(size)
        private var index = 0

        override fun beginLongArrayEntry(): Unit = Unit

        override fun endLongArray() {
            writer = parent
            parent.consumeTag(NbtLongArray(array))
        }

        override fun writeLong(value: Long) {
            array[index++] = value
        }
    }
}
