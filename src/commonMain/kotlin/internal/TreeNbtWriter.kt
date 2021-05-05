package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.tag.*

internal class TreeNbtWriter(
    private val tagConsumer: (NbtTag) -> Unit,
) : NbtWriter {
    private val structureTypeStack = ArrayDeque<NbtTagType>()
    private val structureStack = ArrayDeque<Any?>()
    private val elementNameStack = ArrayDeque<String>()
    private var arrayIndex = 0

    private fun pushTag(tag: NbtTag) {
        @Suppress("UNCHECKED_CAST")
        when (val structureType = structureTypeStack.lastOrNull()) {
            null -> tagConsumer(tag)
            TAG_Compound -> (structureStack.last() as MutableMap<String, NbtTag>)[elementNameStack.removeLast()] = tag
            TAG_List -> (structureStack.last() as MutableList<NbtTag>) += tag
            TAG_Byte_Array -> (structureStack.last() as ByteArray)[arrayIndex++] = (tag as NbtByte).value
            TAG_Int_Array -> (structureStack.last() as IntArray)[arrayIndex++] = (tag as NbtInt).value
            TAG_Long_Array -> (structureStack.last() as LongArray)[arrayIndex++] = (tag as NbtLong).value
            else -> error("Unexpected structure type: $structureType")
        }
    }

    private fun <TValue> beginStructure(tagType: NbtTagType, value: TValue) {
        structureTypeStack += tagType
        structureStack += value
    }

    private inline fun <reified TValue> endStructure(createTag: (TValue) -> NbtTag) {
        structureTypeStack.removeLast()
        pushTag(createTag(structureStack.removeLast() as TValue))
    }

    override fun beginRootTag(type: NbtTagType): Unit = Unit

    override fun beginCompound(): Unit =
        beginStructure(TAG_Compound, LinkedHashMap<String, NbtTag>())

    override fun beginCompoundEntry(type: NbtTagType, name: String) {
        elementNameStack += name
    }

    override fun endCompound(): Unit =
        endStructure<MutableMap<String, NbtTag>> { NbtCompound(it) }

    override fun beginList(type: NbtTagType, size: Int): Unit =
        beginStructure(TAG_List, ArrayList<NbtTag>(size))

    override fun beginListEntry(): Unit = Unit

    override fun endList(): Unit =
        endStructure<MutableList<NbtTag>> { NbtList(it) }

    override fun beginByteArray(size: Int): Unit =
        beginStructure(TAG_Byte_Array, ByteArray(size)).also { arrayIndex = 0 }

    override fun beginByteArrayEntry(): Unit = Unit

    override fun endByteArray(): Unit =
        endStructure<ByteArray> { NbtByteArray(it) }

    override fun beginIntArray(size: Int): Unit =
        beginStructure(TAG_Int_Array, IntArray(size)).also { arrayIndex = 0 }

    override fun beginIntArrayEntry(): Unit = Unit

    override fun endIntArray(): Unit =
        endStructure<IntArray> { NbtIntArray(it) }

    override fun beginLongArray(size: Int): Unit =
        beginStructure(TAG_Long_Array, LongArray(size)).also { arrayIndex = 0 }

    override fun beginLongArrayEntry(): Unit = Unit

    override fun endLongArray(): Unit =
        endStructure<LongArray> { NbtLongArray(it) }

    override fun writeByte(value: Byte): Unit =
        pushTag(NbtByte(value))

    override fun writeShort(value: Short): Unit =
        pushTag(NbtShort(value))

    override fun writeInt(value: Int): Unit =
        pushTag(NbtInt(value))

    override fun writeLong(value: Long): Unit =
        pushTag(NbtLong(value))

    override fun writeFloat(value: Float): Unit =
        pushTag(NbtFloat(value))

    override fun writeDouble(value: Double): Unit =
        pushTag(NbtDouble(value))

    override fun writeString(value: String): Unit =
        pushTag(NbtString(value))
}
