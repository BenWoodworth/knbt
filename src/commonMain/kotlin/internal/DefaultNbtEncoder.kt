package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*

@OptIn(ExperimentalSerializationApi::class)
internal class DefaultNbtEncoder(
    override val nbt: NbtFormat,
    private val writer: NbtWriter,
) : AbstractNbtEncoder() {
    override val serializersModule: SerializersModule
        get() = nbt.serializersModule

    private lateinit var elementName: String
    private var encodingMapKey: Boolean = false

    private val structureTypeStack = ArrayDeque<NbtTagType>()

    private val listTypeStack = ArrayDeque<NbtTagType>() // TAG_End when uninitialized
    private var listSize: Int = 0

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean =
        when (descriptor.kind as StructureKind) {
            StructureKind.CLASS,
            StructureKind.OBJECT,
            -> {
                elementName = descriptor.getElementName(index)
                true
            }
            StructureKind.MAP -> {
                if (index % 2 == 0) encodingMapKey = true
                true
            }
            StructureKind.LIST -> true
        }

    private fun beginEncodingValue(type: NbtTagType) {
        when (val structureType = structureTypeStack.lastOrNull()) {
            null -> {
                writer.beginRootTag(type)
            }
            TAG_Compound -> {
                if (encodingMapKey) throw NbtEncodingException("Only String tag names are supported")
                writer.beginCompoundEntry(type, elementName)
            }
            TAG_List -> when (val listType = listTypeStack.last()) {
                TAG_End -> {
                    listTypeStack[listTypeStack.lastIndex] = type
                    writer.beginList(type, listSize)
                    writer.beginListEntry()
                }
                type -> {
                    writer.beginListEntry()
                }
                else -> throw NbtEncodingException("Cannot encode $type within a $TAG_List of $listType")
            }
            TAG_Byte_Array -> {
                if (type != TAG_Byte) throw NbtEncodingException("Cannot encode $type within a $TAG_Byte_Array")
                writer.beginByteArrayEntry()
            }
            TAG_Int_Array -> {
                if (type != TAG_Int) throw NbtEncodingException("Cannot encode $type within a $TAG_Int_Array")
                writer.beginIntArrayEntry()
            }
            TAG_Long_Array -> {
                if (type != TAG_Int) throw NbtEncodingException("Cannot encode $type within a $TAG_Long_Array")
                writer.beginLongArrayEntry()
            }
            else -> error("Unhandled structure type: $structureType")
        }
    }

    override fun beginCompound(descriptor: SerialDescriptor): CompositeNbtEncoder {
        beginEncodingValue(TAG_Compound)
        writer.beginCompound()
        structureTypeStack += TAG_Compound
        return this
    }

    override fun beginList(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder {
        beginEncodingValue(TAG_List)
        structureTypeStack += TAG_List
        listTypeStack += TAG_End // writer.beginList(TYPE, size) is postponed until the first element is encoded, or the list is ended
        listSize = size
        return this
    }

    override fun beginByteArray(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder {
        beginEncodingValue(TAG_Byte_Array)
        writer.beginByteArray(size)
        structureTypeStack += TAG_Byte_Array
        return this
    }

    override fun beginIntArray(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder {
        beginEncodingValue(TAG_Int_Array)
        writer.beginIntArray(size)
        structureTypeStack += TAG_Int_Array
        return this
    }

    override fun beginLongArray(descriptor: SerialDescriptor, size: Int): CompositeNbtEncoder {
        beginEncodingValue(TAG_Long_Array)
        writer.beginLongArray(size)
        structureTypeStack += TAG_Long_Array
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        when (val structureType = structureTypeStack.removeLast()) {
            TAG_Compound -> writer.endCompound()
            TAG_List -> {
                if (listTypeStack.removeLast() == TAG_End) writer.beginList(TAG_End, listSize)
                writer.endList()
            }
            TAG_Byte_Array -> writer.endByteArray()
            TAG_Int_Array -> writer.endIntArray()
            TAG_Long_Array -> writer.endLongArray()
            else -> error("Unhandled structure type: $structureType")
        }
    }

    @OptIn(ExperimentalNbtApi::class)
    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean =
        nbt.configuration.encodeDefaults

    override fun encodeByte(value: Byte) {
        beginEncodingValue(TAG_Byte)
        writer.writeByte(value)
    }

    override fun encodeShort(value: Short) {
        beginEncodingValue(TAG_Short)
        writer.writeShort(value)
    }

    override fun encodeInt(value: Int) {
        beginEncodingValue(TAG_Int)
        writer.writeInt(value)
    }

    override fun encodeLong(value: Long) {
        beginEncodingValue(TAG_Long)
        writer.writeLong(value)
    }

    override fun encodeFloat(value: Float) {
        beginEncodingValue(TAG_Float)
        writer.writeFloat(value)
    }

    override fun encodeDouble(value: Double) {
        beginEncodingValue(TAG_Double)
        writer.writeDouble(value)
    }

    override fun encodeString(value: String) {
        if (encodingMapKey) {
            encodingMapKey = false
            elementName = value
        } else {
            beginEncodingValue(TAG_String)
            writer.writeString(value)
        }
    }

    override fun encodeByteArray(value: ByteArray) {
        beginEncodingValue(TAG_Byte_Array)
        writer.writeByteArray(value)
    }

    override fun encodeIntArray(value: IntArray) {
        beginEncodingValue(TAG_Int_Array)
        writer.writeIntArray(value)
    }

    override fun encodeLongArray(value: LongArray) {
        beginEncodingValue(TAG_Long_Array)
        writer.writeLongArray(value)
    }

    override fun encodeNbtTag(value: NbtTag) {
        fun writeTag(value: NbtTag): Unit = when (value.type) {
            TAG_End -> error("Unexpected $TAG_End")
            TAG_Byte -> writer.writeByte((value as NbtByte).value)
            TAG_Double -> writer.writeDouble((value as NbtDouble).value)
            TAG_Float -> writer.writeFloat((value as NbtFloat).value)
            TAG_Int -> writer.writeInt((value as NbtInt).value)
            TAG_Long -> writer.writeLong((value as NbtLong).value)
            TAG_Short -> writer.writeShort((value as NbtShort).value)
            TAG_String -> writer.writeString((value as NbtString).value)
            TAG_Compound -> {
                writer.beginCompound()
                (value as NbtCompound).forEach { entry ->
                    writer.beginCompoundEntry(entry.value.type, entry.key)
                    writeTag(entry.value)
                }
                writer.endCompound()
            }
            TAG_List -> {
                val list = (value as NbtList<*>)
                val listType = list.elementType
                writer.beginList(listType, list.size)
                list.forEach { entry ->
                    writer.beginListEntry()
                    if (entry.type != listType) throw NbtEncodingException("Cannot encode ${entry.type} within a $TAG_List of $listType")
                    writeTag(entry)
                }
                writer.endList()
            }
            TAG_Byte_Array -> writer.writeByteArray((value as NbtByteArray).content)
            TAG_Int_Array -> writer.writeIntArray((value as NbtIntArray).content)
            TAG_Long_Array -> writer.writeLongArray((value as NbtLongArray).content)
        }

        beginEncodingValue(value.type)
        writeTag(value)
    }
}
