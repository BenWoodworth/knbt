package net.benwoodworth.knbt.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.AbstractNbtDecoder
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.internal.NbtTagType.*

@OptIn(ExperimentalSerializationApi::class)
internal abstract class BaseNbtDecoder : AbstractNbtDecoder() {
    override val serializersModule: SerializersModule
        get() = nbt.serializersModule

    protected abstract val context: SerializationNbtContext
    protected abstract val reader: NbtReader
    protected abstract val parent: BaseNbtDecoder?
    protected abstract val entryType: NbtTagType

    protected var elementListKind: NbtListKind? = null

    private var decodedNbtNameInfo: NbtReader.CompoundEntryInfo? = null

    private fun decodeNbtTagTypeMarker(): NbtTagType =
        decodedNbtNameInfo?.type ?: entryType

    private fun beginDecodingValue(type: NbtTagType) {
        val actualType = decodeNbtTagTypeMarker()

        if (type != actualType) {
            throw NbtDecodingException(context, "Expected $type, but was $actualType")
        }
    }

    private fun endDecodingValue() {
        endNamedTagIfNamed()
    }

    /**
     * Called in [decodeSerializableValue] before deserializing, that way the decoded [NbtName] is available in case the
     * deserializer needs it.
     */
    private fun beginNamedTagIfNamed(descriptor: SerialDescriptor) {
        if (decodedNbtNameInfo != null) return // Already decoded
        val name = descriptor.nbtName ?: return

        beginDecodingValue(TAG_Compound) // TODO Needed? Problematic?
        reader.beginCompound()

        val entry = reader.beginCompoundEntry()
        when {
            entry.type == TAG_End ->
                throw NbtDecodingException(context, "Expected tag named '$name', but got none")

            entry.name != name && !descriptor.nbtNameIsDynamic ->
                throw NbtDecodingException(context, "Expected tag named '$name', but got '${entry.name}'")
        }

        decodedNbtNameInfo = entry
    }

    /**
     * Called after a value finishes decoding,
     */
    private fun endNamedTagIfNamed() {
        val name = decodedNbtNameInfo?.name ?: return

        val entry = reader.beginCompoundEntry()
        if (entry.type != TAG_End) {
            throw NbtDecodingException(context, "Expected tag named '$name', but got additional entry '${entry.name}'")
        }

        reader.endCompound()
        decodedNbtNameInfo = null
    }

    //region Primitive NBT types
    override fun decodeByte(): Byte {
        beginDecodingValue(TAG_Byte)
        return reader.readByte()
            .also { endDecodingValue() }
    }

    override fun decodeBoolean(): Boolean =
        decodeByte() != 0.toByte()

    override fun decodeShort(): Short {
        beginDecodingValue(TAG_Short)
        return reader.readShort()
            .also { endDecodingValue() }
    }

    override fun decodeInt(): Int {
        beginDecodingValue(TAG_Int)
        return reader.readInt()
            .also { endDecodingValue() }
    }

    override fun decodeLong(): Long {
        beginDecodingValue(TAG_Long)
        return reader.readLong()
            .also { endDecodingValue() }
    }

    override fun decodeFloat(): Float {
        beginDecodingValue(TAG_Float)
        return reader.readFloat()
            .also { endDecodingValue() }
    }

    override fun decodeDouble(): Double {
        beginDecodingValue(TAG_Double)
        return reader.readDouble()
            .also { endDecodingValue() }
    }

    override fun decodeString(): String {
        beginDecodingValue(TAG_String)
        return reader.readString()
            .also { endDecodingValue() }
    }

    override fun decodeChar(): Char {
        beginDecodingValue(TAG_String)
        val string = reader.readString()

        if (string.length != 1) {
            val message = "Expected TAG_String with length 1, but got ${NbtString(string)} (length ${string.length}"
            throw NbtDecodingException(context, message)
        }

        return string[0]
            .also { endDecodingValue() }
    }
    //endregion

    //region Structure begin*() functions
    final override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        when (descriptor.kind) {
            StructureKind.LIST -> when (elementListKind ?: descriptor.getNbtListKind(context)) {
                NbtListKind.List -> beginList()
                NbtListKind.ByteArray -> beginByteArray()
                NbtListKind.IntArray -> beginIntArray()
                NbtListKind.LongArray -> beginLongArray()
            }

            is PolymorphicKind -> throw UnsupportedOperationException(
                "Unable to serialize type with serial name '${descriptor.serialName}'. " +
                        "beginning structures with polymorphic serial kinds is not supported."
            )

            else -> beginCompound(descriptor)
        }

    private fun beginCompound(descriptor: SerialDescriptor): CompositeDecoder {
        beginDecodingValue(TAG_Compound)
        return if (descriptor.kind == StructureKind.MAP) {
            MapNbtDecoder(nbt, context, reader, this, ::endDecodingValue)
        } else {
            ClassNbtDecoder(nbt, context, reader, this, ::endDecodingValue)
        }
    }

    private fun beginList(): CompositeDecoder {
        beginDecodingValue(TAG_List)
        return ListNbtDecoder(nbt, context, reader, this, ::endDecodingValue)
    }

    private fun beginByteArray(): CompositeDecoder {
        beginDecodingValue(TAG_Byte_Array)
        return ByteArrayNbtDecoder(nbt, context, reader, this, ::endDecodingValue)
    }

    private fun beginIntArray(): CompositeDecoder {
        beginDecodingValue(TAG_Int_Array)
        return IntArrayNbtDecoder(nbt, context, reader, this, ::endDecodingValue)
    }

    private fun beginLongArray(): CompositeDecoder {
        beginDecodingValue(TAG_Long_Array)
        return LongArrayNbtDecoder(nbt, context, reader, this, ::endDecodingValue)
    }
    //endregion

    final override fun decodeNbtTag(): NbtTag {
        val tagType = decodeNbtTagTypeMarker()

        beginDecodingValue(tagType)
        return reader.readNbtTag(tagType)
            .also { endDecodingValue() }
            ?: throw NbtDecodingException(context, "Expected a value, but was Nothing")
    }

    //region Unsupported types
    private fun notSupported(type: String): NbtDecodingException =
        NbtDecodingException(context, "Decoding $type values is not supported by the NBT format")

    final override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
        throw notSupported("Enum")
    //endregion

    //region Final super implementations
    final override fun decodeNotNullMark(): Boolean =
        super.decodeNotNullMark()

    final override fun decodeNull(): Nothing? =
        super.decodeNull()

    final override fun decodeInline(descriptor: SerialDescriptor): Decoder =
        super.decodeInline(descriptor)

    final override fun decodeValue(): Any =
        super.decodeValue()

    final override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>, previousValue: T?): T =
        super.decodeSerializableValue(deserializer, previousValue)
    //endregion

    @OptIn(InternalSerializationApi::class)
    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T {
        return when {
            deserializer is AbstractPolymorphicSerializer<*> ->
                throw UnsupportedOperationException(
                    "Unable to serialize type with serial name '${deserializer.descriptor.serialName}'. " +
                            "The builtin polymorphic serializers are not yet supported."
                )

            else -> super.decodeSerializableValue(deserializer)
        }
    }

    override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T {
        elementListKind = descriptor
            .takeIf { it.getElementDescriptor(index).kind == StructureKind.LIST }
            ?.getElementNbtListKind(context, index)
            ?: descriptor.getElementDescriptor(index).getNbtListKind(context)

        return decodeSerializableValue(deserializer)
    }
}

internal class NbtReaderDecoder(
    override val nbt: NbtFormat,
    override val context: SerializationNbtContext,
    override val reader: NbtReader
) : BaseNbtDecoder() {
    override val parent: Nothing? = null

    private val rootTagInfo = reader.beginRootTag()

    override val entryType: NbtTagType
        get() = rootTagInfo.type

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = 0

    @ExperimentalSerializationApi
    override fun decodeSequentially(): Boolean {
        return super.decodeSequentially()
    }
}

private abstract class CompoundNbtDecoder : BaseNbtDecoder() {
    protected abstract val compoundEntryInfo: NbtReader.CompoundEntryInfo
    protected abstract val onEndStructure: () -> Unit

    override fun endStructure(descriptor: SerialDescriptor) {
        reader.endCompound()
        onEndStructure()
    }
}

private class ClassNbtDecoder(
    override val nbt: NbtFormat,
    override val context: SerializationNbtContext,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
    override val onEndStructure: () -> Unit,
) : CompoundNbtDecoder() {
    override lateinit var compoundEntryInfo: NbtReader.CompoundEntryInfo

    override val entryType: NbtTagType
        get() = compoundEntryInfo.type

    init {
        reader.beginCompound()
    }

    private fun handleUnknownKey(info: NbtReader.CompoundEntryInfo) {
        fun discardTagAndGetTypeName(): String =
            if (info.type == TAG_List) {
                try {
                    val entryType = reader.discardListTag().type
                    "${TAG_List}<${entryType}>"
                } catch (e: Exception) {
                    info.type.toString()
                }
            } else {
                info.type.toString()
            }

        if (!nbt.configuration.ignoreUnknownKeys) {
            val discardedType = discardTagAndGetTypeName()
            val message = "Encountered unknown key '${info.name}' ($discardedType)"
            throw NbtDecodingException(context, message)
        }

        reader.discardTag(info.type)
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        compoundEntryInfo = reader.beginCompoundEntry()

        val index = if (compoundEntryInfo.type == TAG_End) {
            CompositeDecoder.DECODE_DONE
        } else {
            var index: Int

            do {
                index = descriptor.getElementIndex(compoundEntryInfo.name)

                if (index == CompositeDecoder.UNKNOWN_NAME) {
                    handleUnknownKey(compoundEntryInfo)
                    compoundEntryInfo = reader.beginCompoundEntry()

                    if (compoundEntryInfo.type == TAG_End) {
                        index = CompositeDecoder.DECODE_DONE
                    }
                }
            } while (index == CompositeDecoder.UNKNOWN_NAME)

            index
        }

        if (index >= 0 && descriptor.getElementDescriptor(index).kind == StructureKind.LIST) {
            elementListKind = descriptor.getElementNbtListKind(context, index)
        }

        return index
    }
}

private class MapNbtDecoder(
    override val nbt: NbtFormat,
    override val context: SerializationNbtContext,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
    override val onEndStructure: () -> Unit,
) : CompoundNbtDecoder() {
    private var index = 0
    private var decodeMapKey: Boolean = false

    override lateinit var compoundEntryInfo: NbtReader.CompoundEntryInfo

    override val entryType: NbtTagType
        get() = if (decodeMapKey) TAG_String else compoundEntryInfo.type

    init {
        reader.beginCompound()
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        if (index % 2 == 0) {
            compoundEntryInfo = reader.beginCompoundEntry()
            if (compoundEntryInfo.type == TAG_End) {
                CompositeDecoder.DECODE_DONE
            } else {
                decodeMapKey = true
                index++
            }
        } else {
            index++
        }

    override fun decodeString(): String =
        if (decodeMapKey) {
            decodeMapKey = false
            compoundEntryInfo.name
        } else {
            super.decodeString()
        }
}

private abstract class ListLikeNbtDecoder : BaseNbtDecoder() {
    protected abstract val elementCount: Int

    private var index: Int = 0

    protected abstract fun beginEntry(): Boolean

    final override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = elementCount

    final override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        val done = when {
            elementCount == NbtReader.UNKNOWN_SIZE -> !beginEntry()
            else -> elementCount == index
        }

        return if (!done) index++ else CompositeDecoder.DECODE_DONE
    }

    @ExperimentalSerializationApi
    final override fun decodeSequentially(): Boolean = elementCount >= 0
}

private class ListNbtDecoder(
    override val nbt: NbtFormat,
    override val context: SerializationNbtContext,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
    private val onEndStructure: () -> Unit,
) : ListLikeNbtDecoder() {
    private val listInfo = reader.beginList()

    override val elementCount: Int
        get() = listInfo.size

    override val entryType: NbtTagType
        get() = listInfo.type

    override fun beginEntry(): Boolean = reader.beginListEntry()

    override fun endStructure(descriptor: SerialDescriptor) {
        reader.endList()
        onEndStructure()
    }
}

private class ByteArrayNbtDecoder(
    override val nbt: NbtFormat,
    override val context: SerializationNbtContext,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
    private val onEndStructure: () -> Unit,
) : ListLikeNbtDecoder() {
    private val arrayInfo = reader.beginByteArray()

    override val elementCount: Int
        get() = arrayInfo.size

    override val entryType: NbtTagType
        get() = TAG_Byte

    override fun beginEntry(): Boolean = reader.beginByteArrayEntry()

    override fun endStructure(descriptor: SerialDescriptor) {
        reader.endByteArray()
        onEndStructure()
    }
}

private class IntArrayNbtDecoder(
    override val nbt: NbtFormat,
    override val context: SerializationNbtContext,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
    private val onEndStructure: () -> Unit,
) : ListLikeNbtDecoder() {
    private val arrayInfo = reader.beginIntArray()

    override val elementCount: Int
        get() = arrayInfo.size

    override val entryType: NbtTagType
        get() = TAG_Int

    override fun beginEntry(): Boolean = reader.beginIntArrayEntry()

    override fun endStructure(descriptor: SerialDescriptor) {
        reader.endIntArray()
        onEndStructure()
    }
}

private class LongArrayNbtDecoder(
    override val nbt: NbtFormat,
    override val context: SerializationNbtContext,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
    private val onEndStructure: () -> Unit,
) : ListLikeNbtDecoder() {
    private val arrayInfo = reader.beginLongArray()

    override val elementCount: Int
        get() = arrayInfo.size

    override val entryType: NbtTagType
        get() = TAG_Long

    override fun beginEntry(): Boolean = reader.beginLongArrayEntry()

    override fun endStructure(descriptor: SerialDescriptor) {
        reader.endLongArray()
        onEndStructure()
    }
}
