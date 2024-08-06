package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*

/**
 * An interface for writing NBT data.
 *
 * Writing begins with a call to [beginRootTag].
 *
 * A value is written with a call to one of: [beginCompound], [beginList], [beginByteArray], [beginIntArray],
 * [beginLongArray], [writeByte], [writeShort], [writeInt], [writeLong], [writeFloat], [writeDouble], [writeString].
 */
internal interface NbtWriter {
    /**
     * Followed by a call to write a value of the same type.
     */
    fun beginRootTag(type: NbtTagType)

    /**
     * Followed by calls to [beginCompoundEntry], then a call to [endCompound]
     */
    fun beginCompound()

    /**
     * Followed by a call to write a value of the same type.
     */
    fun beginCompoundEntry(type: NbtTagType, name: String)

    fun endCompound()

    /**
     * Followed by calls to [beginListEntry], then [endList].
     */
    fun beginList(type: NbtTagType, size: Int)

    /**
     * Followed by a call to write a value of the same type.
     */
    fun beginListEntry()

    fun endList()

    /**
     * Followed by calls to [beginByteArrayEntry], then [endByteArray].
     */
    fun beginByteArray(size: Int)

    /**
     * Followed by a call to [writeByte].
     */
    fun beginByteArrayEntry()

    fun endByteArray()

    /**
     * Followed by calls to [beginIntArrayEntry], then [endIntArray].
     */
    fun beginIntArray(size: Int)

    /**
     * Followed by a call to [writeInt].
     */
    fun beginIntArrayEntry()

    fun endIntArray()

    /**
     * Followed by calls to [beginLongArrayEntry], then [endLongArray].
     */
    fun beginLongArray(size: Int)

    /**
     * Followed by a call to [writeLong].
     */
    fun beginLongArrayEntry()

    fun endLongArray()

    fun writeByte(value: Byte)

    fun writeShort(value: Short)

    fun writeInt(value: Int)

    fun writeLong(value: Long)

    fun writeFloat(value: Float)

    fun writeDouble(value: Double)

    fun writeString(value: String)
}

internal fun NbtWriter.writeByteArray(value: ByteArray) {
    beginByteArray(value.size)
    value.forEach {
        beginByteArrayEntry()
        writeByte(it)
    }
    endByteArray()
}

internal fun NbtWriter.writeIntArray(value: IntArray) {
    beginIntArray(value.size)
    value.forEach {
        beginIntArrayEntry()
        writeInt(it)
    }
    endIntArray()
}

internal fun NbtWriter.writeLongArray(value: LongArray) {
    beginLongArray(value.size)
    value.forEach {
        beginLongArrayEntry()
        writeLong(it)
    }
    endLongArray()
}

internal fun NbtWriter.writeNbtTag(context: NbtContext, value: NbtTag): Unit = when (value.type) {
    TAG_End -> error("Unexpected $TAG_End")
    TAG_Byte -> writeByte((value as NbtByte).value)
    TAG_Double -> writeDouble((value as NbtDouble).value)
    TAG_Float -> writeFloat((value as NbtFloat).value)
    TAG_Int -> writeInt((value as NbtInt).value)
    TAG_Long -> writeLong((value as NbtLong).value)
    TAG_Short -> writeShort((value as NbtShort).value)
    TAG_String -> writeString((value as NbtString).value)
    TAG_Compound -> {
        beginCompound()
        (value as NbtCompound).content.forEach { (key, value) ->
            beginCompoundEntry(value.type, key)
            writeNbtTag(context, value)
        }
        endCompound()
    }

    TAG_List -> {
        val list = (value as NbtList<*>)
        val listType = list.elementType
        beginList(listType, list.size)
        list.content.forEach { entry ->
            beginListEntry()

            if (entry.type != listType) {
                val message = "Cannot encode ${entry.type} within a $TAG_List of $listType"
                throw NbtEncodingException(context, message)
            }

            writeNbtTag(context, entry)
        }
        endList()
    }

    TAG_Byte_Array -> {
        val array = (value as NbtByteArray)
        beginByteArray(array.size)
        array.content.forEach { entry ->
            beginByteArrayEntry()
            writeByte(entry)
        }
        endByteArray()
    }

    TAG_Int_Array -> {
        val array = (value as NbtIntArray)
        beginIntArray(array.size)
        array.content.forEach { entry ->
            beginIntArrayEntry()
            writeInt(entry)
        }
        endIntArray()
    }

    TAG_Long_Array -> {
        val array = (value as NbtLongArray)
        beginLongArray(array.size)
        array.content.forEach { entry ->
            beginLongArrayEntry()
            writeLong(entry)
        }
        endLongArray()
    }
}
