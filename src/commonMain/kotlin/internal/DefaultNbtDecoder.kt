package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.tag.*


@ExperimentalSerializationApi
internal class DefaultNbtDecoder(
    private val nbt: Nbt,
    private val reader: NbtReader,
) : AbstractNbtDecoder() {
    override val serializersModule: SerializersModule
        get() = nbt.serializersModule

    private var beganEntry = false

    private val structureTypeStack = ArrayDeque<NbtTagType>()
    private val elementIndexStack = ArrayDeque<Int>()
    private val listTypeStack = ArrayDeque<NbtTagType>()
    private var collectionSize: Int = -1

    private fun nextElementIndex(): Int =
        elementIndexStack[elementIndexStack.lastIndex]++

    private lateinit var elementType: NbtTagType

    private var decodingMapKey = false
    private lateinit var elementName: String

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        when (val structureType = structureTypeStack.last()) {
            TAG_List -> {
                if (reader.beginListEntry()) nextElementIndex() else CompositeDecoder.DECODE_DONE
            }
            TAG_Byte_Array -> {
                if (reader.beginByteArrayEntry()) nextElementIndex() else CompositeDecoder.DECODE_DONE
            }
            TAG_Int_Array -> {
                if (reader.beginIntArrayEntry()) nextElementIndex() else CompositeDecoder.DECODE_DONE
            }
            TAG_Long_Array -> {
                if (reader.beginLongArrayEntry()) nextElementIndex() else CompositeDecoder.DECODE_DONE
            }
            TAG_Compound -> {
                if (descriptor.kind == StructureKind.MAP) {
                    val index = nextElementIndex()
                    if (index % 2 == 0) {
                        val info = reader.beginCompoundEntry()
                        if (info.type == TAG_End) {
                            CompositeDecoder.DECODE_DONE
                        } else {
                            decodingMapKey = true
                            elementName = info.name
                            elementType = info.type
                            index
                        }
                    } else {
                        index
                    }
                } else {
                    val info = reader.beginCompoundEntry()
                    if (info.type == TAG_End) {
                        CompositeDecoder.DECODE_DONE
                    } else {
                        elementType = info.type
                        descriptor.getElementIndex(info.name)
                    }
                }
            }
            else -> error("Unhandled structure type: $structureType")
        }

    private fun beginDecodingValue(type: NbtTagType) {
        when (val structureType = structureTypeStack.lastOrNull()) {
            null -> {
                elementType = reader.beginRootTag().type
                if (type != elementType) throw NbtDecodingException("Expected $type, but got $elementType")
            }
            TAG_Compound -> {
                if (decodingMapKey) throw NbtEncodingException("Only String tag names are supported")
                if (type != elementType) throw NbtDecodingException("Expected $type but got $elementType")
            }
            TAG_List -> {
                val listType = listTypeStack.last()
                if (type != listType) throw NbtDecodingException("Cannot decode a $type from a $TAG_List of $listType")
            }
            TAG_Byte_Array -> {
                if (type != TAG_Byte) throw NbtEncodingException("Cannot encode $type within a $TAG_Byte_Array")
            }
            TAG_Int_Array -> {
                if (type != TAG_Int) throw NbtEncodingException("Cannot encode $type within a $TAG_Int_Array")
            }
            TAG_Long_Array -> {
                if (type != TAG_Long) throw NbtEncodingException("Cannot encode $type within a $TAG_Long_Array")
            }
            else -> error("Unhandled structure type: $structureType")
        }

        beganEntry = false
    }

    override fun beginCompound(descriptor: SerialDescriptor): CompositeNbtDecoder {
        beginDecodingValue(TAG_Compound)
        reader.beginCompound()
        elementIndexStack += 0
        structureTypeStack += TAG_Compound
        collectionSize = -1
        return this
    }

    override fun beginList(descriptor: SerialDescriptor): CompositeNbtDecoder {
        beginDecodingValue(TAG_List)
        val info = reader.beginList()
        elementIndexStack += 0
        structureTypeStack += TAG_List
        listTypeStack += info.type
        collectionSize = info.size
        return this
    }

    override fun beginByteArray(descriptor: SerialDescriptor): CompositeNbtDecoder {
        beginDecodingValue(TAG_Byte_Array)
        val info = reader.beginByteArray()
        elementIndexStack += 0
        structureTypeStack += TAG_Byte_Array
        collectionSize = info.size
        return this
    }

    override fun beginIntArray(descriptor: SerialDescriptor): CompositeNbtDecoder {
        beginDecodingValue(TAG_Int_Array)
        val info = reader.beginIntArray()
        elementIndexStack += 0
        structureTypeStack += TAG_Int_Array
        collectionSize = info.size
        return this
    }

    override fun beginLongArray(descriptor: SerialDescriptor): CompositeNbtDecoder {
        beginDecodingValue(TAG_Long_Array)
        val info = reader.beginLongArray()
        elementIndexStack += 0
        structureTypeStack += TAG_Long_Array
        collectionSize = info.size
        return this
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = collectionSize

    override fun decodeSequentially(): Boolean = collectionSize >= 0

    override fun endStructure(descriptor: SerialDescriptor) {
        elementIndexStack.removeLast()

        when (val structureType = structureTypeStack.removeLast()) {
            TAG_Compound -> reader.endCompound()
            TAG_List -> {
                reader.endList()
                listTypeStack.removeLast()
            }
            TAG_Byte_Array -> reader.endByteArray()
            TAG_Int_Array -> reader.endIntArray()
            TAG_Long_Array -> reader.endLongArray()
            else -> error("Unhandled structure type: $structureType")
        }
    }

    override fun decodeByte(): Byte {
        beginDecodingValue(TAG_Byte)
        return reader.readByte()
    }

    override fun decodeShort(): Short {
        beginDecodingValue(TAG_Short)
        return reader.readShort()
    }

    override fun decodeInt(): Int {
        beginDecodingValue(TAG_Int)
        return reader.readInt()
    }

    override fun decodeLong(): Long {
        beginDecodingValue(TAG_Long)
        return reader.readLong()
    }

    override fun decodeFloat(): Float {
        beginDecodingValue(TAG_Float)
        return reader.readFloat()
    }

    override fun decodeDouble(): Double {
        beginDecodingValue(TAG_Double)
        return reader.readDouble()
    }

    override fun decodeString(): String {
        return if (decodingMapKey) {
            decodingMapKey = false
            elementName
        } else {
            beginDecodingValue(TAG_String)
            reader.readString()
        }
    }

    override fun decodeByteArray(): ByteArray {
        beginDecodingValue(TAG_Byte_Array)
        return reader.readByteArray()
    }

    override fun decodeIntArray(): IntArray {
        beginDecodingValue(TAG_Int_Array)
        return reader.readIntArray()
    }

    override fun decodeLongArray(): LongArray {
        beginDecodingValue(TAG_Long_Array)
        return reader.readLongArray()
    }

    override fun decodeNbtTag(): NbtTag {
        fun readTag(type: NbtTagType): NbtTag = when (type) {
            TAG_End -> error("Unexpected $TAG_End")
            TAG_Byte -> NbtByte(reader.readByte())
            TAG_Double -> NbtDouble(reader.readDouble())
            TAG_Float -> NbtFloat(reader.readFloat())
            TAG_Int -> NbtInt(reader.readInt())
            TAG_Long -> NbtLong(reader.readLong())
            TAG_Short -> NbtShort(reader.readShort())
            TAG_String -> NbtString(reader.readString())
            TAG_Compound -> buildNbtCompound {
                reader.beginCompound()
                while (true) {
                    val entryInfo = reader.beginCompoundEntry()
                    if (entryInfo.type == TAG_End) break
                    put(entryInfo.name, readTag(entryInfo.type))
                }
                reader.endCompound()
            }
            TAG_List -> buildNbtList<NbtTag> {
                val listInfo = reader.beginList()
                val listType = listInfo.type
                if (listInfo.size == NbtReader.UNKNOWN_SIZE) {
                    while (reader.beginListEntry()) {
                        addInternal(readTag(listType))
                    }
                } else {
                    repeat(listInfo.size) {
                        addInternal(readTag(listType))
                    }
                }
                reader.endList()
            }
            TAG_Byte_Array -> NbtByteArray(reader.readByteArray())
            TAG_Int_Array -> NbtIntArray(reader.readIntArray())
            TAG_Long_Array -> NbtLongArray(reader.readLongArray())
        }

        return if (structureTypeStack.isEmpty()) {
            readTag(reader.beginRootTag().type)
        } else {
            val tagType = when (val structureType = structureTypeStack.last()) {
                TAG_Compound -> elementType
                TAG_List -> listTypeStack.last()
                TAG_Byte_Array -> TAG_Byte
                TAG_Int_Array -> TAG_Int
                TAG_Long_Array -> TAG_Long
                else -> error("Unhandled structure type: $structureType")
            }
            beginDecodingValue(tagType)
            readTag(tagType)
        }
    }
}
