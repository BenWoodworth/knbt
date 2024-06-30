package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*

@OptIn(ExperimentalSerializationApi::class)
internal class NbtWriterEncoder(
    override val nbt: NbtFormat,
    private val context: SerializationNbtContext,
    writer: NbtWriter,
) : AbstractNbtEncoder() {
    override val serializersModule: SerializersModule
        get() = nbt.serializersModule

    private var currentDescriptor: SerialDescriptor? = null

    private lateinit var elementName: String
    private var encodingMapKey: Boolean = false

    private val structureTypeStack = ArrayDeque<NbtTagType>()

    private var elementListKind: NbtListKind? = null
    private val listTypeStack = ArrayDeque<NbtTagType>() // TAG_End when uninitialized
    private var listSize: Int = 0

    private val writer: NbtWriter =
        if (nbt.capabilities.namedRoot) RootNameVerifyingNbtWriter(writer)
        else writer

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        when (descriptor.kind as StructureKind) {
            StructureKind.CLASS,
            StructureKind.OBJECT,
            -> {
                elementName = descriptor.getElementName(index)
            }

            StructureKind.MAP -> {
                if (index % 2 == 0) encodingMapKey = true
            }

            StructureKind.LIST -> {}
        }

        if (descriptor.getElementDescriptor(index).kind == StructureKind.LIST) {
            elementListKind = descriptor.getElementNbtListKind(context, index)
        }

        return true
    }

    private fun beginEncodingValue(type: NbtTagType) {
        context.onBeginValue()

        currentDescriptor = null

        when (val structureType = structureTypeStack.lastOrNull()) {
            null -> {
                writer.beginRootTag(type)
            }

            TAG_Compound -> {
                if (encodingMapKey) throw NbtEncodingException(context, "Only String tag names are supported")
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

                else -> throw NbtEncodingException(context, "Cannot encode $type within a $TAG_List of $listType")
            }

            TAG_Byte_Array -> {
                if (type != TAG_Byte) {
                    throw NbtEncodingException(context, "Cannot encode $type within a $TAG_Byte_Array")
                }
                writer.beginByteArrayEntry()
            }

            TAG_Int_Array -> {
                if (type != TAG_Int) {
                    throw NbtEncodingException(context, "Cannot encode $type within a $TAG_Int_Array")
                }
                writer.beginIntArrayEntry()
            }

            TAG_Long_Array -> {
                if (type != TAG_Long) {
                    throw NbtEncodingException(context, "Cannot encode $type within a $TAG_Long_Array")
                }
                writer.beginLongArrayEntry()
            }

            else -> error("Unhandled structure type: $structureType")
        }
    }

    private fun beginNamedTagIfNamed(descriptor: SerialDescriptor) {
        val name = descriptor.nbtName ?: return

        beginEncodingValue(TAG_Compound)
        writer.beginCompound()

        structureTypeStack += TAG_Compound
        elementName = name
    }

    private fun endNamedTagIfNamed(descriptor: SerialDescriptor) {
        if (descriptor.nbtName == null) return

        structureTypeStack.removeLast()
        writer.endCompound()
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        when (descriptor.kind) {
            is PolymorphicKind -> throw UnsupportedOperationException(
                "Unable to serialize type with serial name '${descriptor.serialName}'. " +
                        "beginning structures with polymorphic serial kinds is not supported."
            )

            else -> beginCompound(descriptor)
        }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
        if (descriptor.kind == StructureKind.LIST) {
            when (elementListKind ?: descriptor.getNbtListKind(context)) {
                NbtListKind.List -> beginList(collectionSize)
                NbtListKind.ByteArray -> beginByteArray(collectionSize)
                NbtListKind.IntArray -> beginIntArray(collectionSize)
                NbtListKind.LongArray -> beginLongArray(collectionSize)
            }
        } else {
            beginStructure(descriptor)
        }

    private fun beginCompound(descriptor: SerialDescriptor): CompositeEncoder {
        beginNamedTagIfNamed(descriptor)
        beginEncodingValue(TAG_Compound)
        writer.beginCompound()
        structureTypeStack += TAG_Compound
        return this
    }

    private fun beginList(size: Int): CompositeEncoder {
        beginEncodingValue(TAG_List)
        structureTypeStack += TAG_List
        listTypeStack += TAG_End // writer.beginList(TYPE, size) is postponed until the first element is encoded, or the list is ended
        listSize = size
        return this
    }

    private fun beginByteArray(size: Int): CompositeEncoder {
        beginEncodingValue(TAG_Byte_Array)
        writer.beginByteArray(size)
        structureTypeStack += TAG_Byte_Array
        return this
    }

    private fun beginIntArray(size: Int): CompositeEncoder {
        beginEncodingValue(TAG_Int_Array)
        writer.beginIntArray(size)
        structureTypeStack += TAG_Int_Array
        return this
    }

    private fun beginLongArray(size: Int): CompositeEncoder {
        beginEncodingValue(TAG_Long_Array)
        writer.beginLongArray(size)
        structureTypeStack += TAG_Long_Array
        return this
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        when (val structureType = structureTypeStack.removeLast()) {
            TAG_Compound -> {
                writer.endCompound()
                endNamedTagIfNamed(descriptor)
            }

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

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean =
        nbt.configuration.encodeDefaults

    override fun encodeByte(value: Byte) {
        beginEncodingValue(TAG_Byte)
        writer.writeByte(value)
    }

    override fun encodeBoolean(value: Boolean) {
        beginEncodingValue(TAG_Byte)
        writer.writeByte(if (value) 1 else 0)
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

    override fun encodeChar(value: Char): Unit =
        encodeString(value.toString())

    private fun encodeByteArray(value: ByteArray) {
        beginEncodingValue(TAG_Byte_Array)
        writer.writeByteArray(value)
    }

    private fun encodeIntArray(value: IntArray) {
        beginEncodingValue(TAG_Int_Array)
        writer.writeIntArray(value)
    }

    private fun encodeLongArray(value: LongArray) {
        beginEncodingValue(TAG_Long_Array)
        writer.writeLongArray(value)
    }

    override fun encodeNbtTag(tag: NbtTag) {
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
                (value as NbtCompound).content.forEach { (key, value) ->
                    writer.beginCompoundEntry(value.type, key)
                    writeTag(value)
                }
                writer.endCompound()
            }

            TAG_List -> {
                val list = (value as NbtList<*>)
                val listType = list.elementType
                writer.beginList(listType, list.size)
                list.content.forEach { entry ->
                    writer.beginListEntry()

                    if (entry.type != listType) {
                        val message = "Cannot encode ${entry.type} within a $TAG_List of $listType"
                        throw NbtEncodingException(context, message)
                    }

                    writeTag(entry)
                }
                writer.endList()
            }

            TAG_Byte_Array -> {
                val array = (value as NbtByteArray)
                writer.beginByteArray(array.size)
                array.content.forEach { entry ->
                    writer.beginByteArrayEntry()
                    writer.writeByte(entry)
                }
                writer.endByteArray()
            }

            TAG_Int_Array -> {
                val array = (value as NbtIntArray)
                writer.beginIntArray(array.size)
                array.content.forEach { entry ->
                    writer.beginIntArrayEntry()
                    writer.writeInt(entry)
                }
                writer.endIntArray()
            }

            TAG_Long_Array -> {
                val array = (value as NbtLongArray)
                writer.beginLongArray(array.size)
                array.content.forEach { entry ->
                    writer.beginLongArrayEntry()
                    writer.writeLong(entry)
                }
                writer.endLongArray()
            }
        }

        beginEncodingValue(tag.type)
        writeTag(tag)
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        context.onBeginSerializableValue(serializer.descriptor)

        fun isArraySerializer(arraySerializer: SerializationStrategy<*>, arrayKind: NbtListKind): Boolean =
            (elementListKind == null || elementListKind == arrayKind) && serializer == arraySerializer

        return when {
            isArraySerializer(ByteArraySerializer(), NbtListKind.ByteArray) -> encodeByteArray(value as ByteArray)
            isArraySerializer(IntArraySerializer(), NbtListKind.IntArray) -> encodeIntArray(value as IntArray)
            isArraySerializer(LongArraySerializer(), NbtListKind.LongArray) -> encodeLongArray(value as LongArray)

            serializer is AbstractPolymorphicSerializer<*> ->
                throw UnsupportedOperationException(
                    "Unable to serialize type with serial name '${serializer.descriptor.serialName}'. " +
                            "The builtin polymorphic serializers are not yet supported."
                )

            else -> super.encodeSerializableValue(serializer, value)
        }
    }

    private inner class RootNameVerifyingNbtWriter(
        private val writer: NbtWriter
    ) : NbtWriter by writer {
        private var compoundNesting = 0
        private var wroteRootEntry = false

        private inline fun verify(value: Boolean) {
            if (!value) {
                val message = "The ${nbt.name} format only supports $TAG_Compound with one entry"
                throw NbtEncodingException(context, message)
            }
        }

        override fun beginRootTag(type: NbtTagType) {
            verify(type == TAG_Compound)

            writer.beginRootTag(type)
        }

        override fun beginCompound() {
            writer.beginCompound()
            compoundNesting++
        }

        override fun beginCompoundEntry(type: NbtTagType, name: String) {
            if (compoundNesting == 1) {
                verify(!wroteRootEntry)
                wroteRootEntry = true
            }

            writer.beginCompoundEntry(type, name)
        }

        override fun endCompound() {
            verify(compoundNesting != 1 || wroteRootEntry)
            compoundNesting--

            writer.endCompound()
        }
    }
}
