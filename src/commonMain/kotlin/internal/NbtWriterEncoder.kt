package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.AbstractNbtEncoder
import net.benwoodworth.knbt.ExperimentalNbtApi
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.internal.NbtTagType.*

@OptIn(ExperimentalSerializationApi::class)
internal class NbtWriterEncoder(
    override val nbt: NbtFormat,
    private val context: SerializationNbtContext,
    writer: NbtWriter,
) : AbstractNbtEncoder() {
    override val serializersModule: SerializersModule
        get() = nbt.serializersModule

    private lateinit var elementName: String
    private var encodingMapKey: Boolean = false

    private val structureTypeStack = ArrayDeque<NbtTagType>()

    private var serializerListKind: NbtListKind? = null
    private val listTypeStack = ArrayDeque<NbtTagType>() // TAG_End when uninitialized
    private var listSize: Int = 0

    private var nbtNameToWrite: String? = null
    private var nbtNameToWriteWasDynamicallyEncoded = false
    private val writtenNbtNameStack = ArrayDeque<String?>()

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
            serializerListKind = descriptor.getElementNbtListKind(context, index)
        }

        return true
    }

    private fun beginEncodingValue(type: NbtTagType) {
        beginNamedTagIfNamed()

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

    private fun endEncodingValue() {
        endNamedTagIfNamed()
    }

    private fun beginNamedTagIfNamed() {
        val nbtName = nbtNameToWrite

        writtenNbtNameStack.addLast(null)
        nbtNameToWrite = null
        nbtNameToWriteWasDynamicallyEncoded = false

        if (nbtName != null) {
            beginEncodingValue(TAG_Compound)
            writer.beginCompound()

            structureTypeStack += TAG_Compound
            elementName = nbtName
            writtenNbtNameStack += nbtName
        }
    }

    private fun endNamedTagIfNamed() {
        val nbtName = writtenNbtNameStack.removeLast()

        if (nbtName != null) {
            structureTypeStack.removeLast()
            writer.endCompound()
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder =
        when (descriptor.kind) {
            is PolymorphicKind -> throw UnsupportedOperationException(
                "Unable to serialize type with serial name '${descriptor.serialName}'. " +
                        "beginning structures with polymorphic serial kinds is not supported."
            )

            else -> beginCompound()
        }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder =
        if (descriptor.kind == StructureKind.LIST) {
            when (serializerListKind ?: descriptor.getNbtListKind(context)) {
                NbtListKind.List -> beginList(collectionSize)
                NbtListKind.ByteArray -> beginByteArray(collectionSize)
                NbtListKind.IntArray -> beginIntArray(collectionSize)
                NbtListKind.LongArray -> beginLongArray(collectionSize)
            }
        } else {
            beginStructure(descriptor)
        }

    override fun endStructure(descriptor: SerialDescriptor): Unit =
        when (val structureType = structureTypeStack.removeLast()) {
            TAG_Compound -> endCompound()
            TAG_List -> endList()
            TAG_Byte_Array -> endByteArray()
            TAG_Int_Array -> endIntArray()
            TAG_Long_Array -> endLongArray()
            else -> error("Unhandled structure type: $structureType")
        }

    private fun beginCompound(): CompositeEncoder {
        beginEncodingValue(TAG_Compound)
        writer.beginCompound()
        structureTypeStack += TAG_Compound
        return this
    }

    private fun endCompound() {
        writer.endCompound()
        endEncodingValue()
    }

    private fun beginList(size: Int): CompositeEncoder {
        beginEncodingValue(TAG_List)
        structureTypeStack += TAG_List
        listTypeStack += TAG_End // writer.beginList(TYPE, size) is postponed until the first element is encoded, or the list is ended
        listSize = size
        return this
    }

    private fun endList() {
        if (listTypeStack.removeLast() == TAG_End) writer.beginList(TAG_End, listSize)
        writer.endList()
        endEncodingValue()
    }

    private fun beginByteArray(size: Int): CompositeEncoder {
        beginEncodingValue(TAG_Byte_Array)
        writer.beginByteArray(size)
        structureTypeStack += TAG_Byte_Array
        return this
    }

    private fun endByteArray() {
        writer.endByteArray()
        endEncodingValue()
    }

    private fun beginIntArray(size: Int): CompositeEncoder {
        beginEncodingValue(TAG_Int_Array)
        writer.beginIntArray(size)
        structureTypeStack += TAG_Int_Array
        return this
    }

    private fun endIntArray() {
        writer.endIntArray()
        endEncodingValue()
    }

    private fun beginLongArray(size: Int): CompositeEncoder {
        beginEncodingValue(TAG_Long_Array)
        writer.beginLongArray(size)
        structureTypeStack += TAG_Long_Array
        return this
    }

    private fun endLongArray() {
        writer.endLongArray()
        endEncodingValue()
    }

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean =
        nbt.configuration.encodeDefaults

    override fun encodeByte(value: Byte) {
        beginEncodingValue(TAG_Byte)
        writer.writeByte(value)
        endEncodingValue()
    }

    override fun encodeBoolean(value: Boolean) {
        beginEncodingValue(TAG_Byte)
        writer.writeByte(if (value) 1 else 0)
        endEncodingValue()
    }

    override fun encodeShort(value: Short) {
        beginEncodingValue(TAG_Short)
        writer.writeShort(value)
        endEncodingValue()
    }

    override fun encodeInt(value: Int) {
        beginEncodingValue(TAG_Int)
        writer.writeInt(value)
        endEncodingValue()
    }

    override fun encodeLong(value: Long) {
        beginEncodingValue(TAG_Long)
        writer.writeLong(value)
        endEncodingValue()
    }

    override fun encodeFloat(value: Float) {
        beginEncodingValue(TAG_Float)
        writer.writeFloat(value)
        endEncodingValue()
    }

    override fun encodeDouble(value: Double) {
        beginEncodingValue(TAG_Double)
        writer.writeDouble(value)
        endEncodingValue()
    }

    override fun encodeString(value: String) {
        if (encodingMapKey) {
            encodingMapKey = false
            elementName = value
        } else {
            beginEncodingValue(TAG_String)
            writer.writeString(value)
            endEncodingValue()
        }
    }

    override fun encodeChar(value: Char): Unit =
        encodeString(value.toString())

    @ExperimentalNbtApi
    override fun encodeNbtName(name: String) {
        context.checkDynamicallySerializingNbtName()

        if (!nbtNameToWriteWasDynamicallyEncoded) {
            nbtNameToWrite = name
            nbtNameToWriteWasDynamicallyEncoded = true
        }
    }

    override fun encodeNbtTag(tag: NbtTag) {
        beginEncodingValue(tag.type)
        writer.writeNbtTag(context, tag)
        endEncodingValue()
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) {
        if (nbtNameToWrite == null) {
            nbtNameToWrite = serializer.descriptor.nbtName
        }

        return when {
            serializer is AbstractPolymorphicSerializer<*> ->
                throw UnsupportedOperationException(
                    "Unable to serialize type with serial name '${serializer.descriptor.serialName}'. " +
                            "The builtin polymorphic serializers are not yet supported."
                )

            else ->
                context.decorateValueSerialization(serializer.descriptor) {
                    serializer.serialize(this, value)
                }
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
