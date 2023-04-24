package net.benwoodworth.knbt.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.internal.AbstractPolymorphicSerializer
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*

@OptIn(ExperimentalSerializationApi::class)
internal abstract class BaseNbtDecoder : AbstractNbtDecoder() {
    override val serializersModule: SerializersModule
        get() = nbt.serializersModule

    protected abstract val reader: NbtReader
    protected abstract val parent: BaseNbtDecoder?
    protected abstract val entryType: NbtTagType

    protected var elementListKind: NbtListKind? = null

    protected abstract fun getPathNode(): NbtPath.Node

    fun getPath(): NbtPath {
        val path = mutableListOf<NbtPath.Node>()

        var decoder: BaseNbtDecoder? = this
        while (decoder != null) {
            path.add(decoder.getPathNode())
            decoder = decoder.parent
        }

        return NbtPath(path.asReversed())
    }

    protected inline fun <R> tryWithPath(block: () -> R): R = tryWithPath(::getPath, block)

    private fun expectTagType(expected: NbtTagType) {
        val actual = entryType
        if (expected != actual) {
            throw NbtDecodingException("Expected $expected, but was $actual", getPath())
        }
    }

    private fun beginNamedTagIfNamed(descriptor: SerialDescriptor) {
        val name = descriptor.nbtNamed ?: return

        expectTagType(TAG_Compound)
        reader.beginCompound()

        val entry = reader.beginCompoundEntry()
        when {
            entry.type == TAG_End -> throw NbtDecodingException("Expected tag named '$name', but got none")
            entry.name != name -> throw NbtDecodingException("Expected tag named '$name', but got '${entry.name}'")
        }
    }

    protected fun endNamedTagIfNamed(descriptor: SerialDescriptor) {
        val name = descriptor.nbtNamed ?: return

        val entry = reader.beginCompoundEntry()
        if (entry.type != TAG_End) {
            throw NbtDecodingException("Expected tag named '$name', but got additional entry '${entry.name}'")
        }

        reader.endCompound()
    }

    //region Primitive NBT types
    override fun decodeByte(): Byte {
        expectTagType(TAG_Byte)
        return tryWithPath { reader.readByte() }
    }

    override fun decodeBoolean(): Boolean =
        decodeByte() != 0.toByte()

    override fun decodeShort(): Short {
        expectTagType(TAG_Short)
        return tryWithPath { reader.readShort() }
    }

    override fun decodeInt(): Int {
        expectTagType(TAG_Int)
        return tryWithPath { reader.readInt() }
    }

    override fun decodeLong(): Long {
        expectTagType(TAG_Long)
        return tryWithPath { reader.readLong() }
    }

    override fun decodeFloat(): Float {
        expectTagType(TAG_Float)
        return tryWithPath { reader.readFloat() }
    }

    override fun decodeDouble(): Double {
        expectTagType(TAG_Double)
        return tryWithPath { reader.readDouble() }
    }

    override fun decodeString(): String {
        expectTagType(TAG_String)
        return tryWithPath { reader.readString() }
    }

    override fun decodeChar(): Char {
        expectTagType(TAG_String)
        val string = tryWithPath { reader.readString() }

        if (string.length != 1) {
            throw NbtDecodingException("Expected TAG_String with length 1, but got ${NbtString(string)} (length ${string.length}")
        }

        return string[0]
    }

    private fun decodeByteArray(): ByteArray {
        expectTagType(TAG_Byte_Array)
        return tryWithPath { reader.readByteArray() }
    }

    private fun decodeIntArray(): IntArray {
        expectTagType(TAG_Int_Array)
        return tryWithPath { reader.readIntArray() }
    }

    private fun decodeLongArray(): LongArray {
        expectTagType(TAG_Long_Array)
        return tryWithPath { reader.readLongArray() }
    }
    //endregion

    //region Structure begin*() functions
    final override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        when (descriptor.kind) {
            StructureKind.LIST -> when (elementListKind ?: descriptor.nbtListKind) {
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
        beginNamedTagIfNamed(descriptor)

        expectTagType(TAG_Compound)
        return tryWithPath {
            if (descriptor.kind == StructureKind.MAP) {
                MapNbtDecoder(nbt, reader, this)
            } else {
                ClassNbtDecoder(nbt, reader, this)
            }
        }
    }

    private fun beginList(): CompositeDecoder {
        expectTagType(TAG_List)
        return tryWithPath { ListNbtDecoder(nbt, reader, this) }
    }

    private fun beginByteArray(): CompositeDecoder {
        expectTagType(TAG_Byte_Array)
        return tryWithPath { ByteArrayNbtDecoder(nbt, reader, this) }
    }

    private fun beginIntArray(): CompositeDecoder {
        expectTagType(TAG_Int_Array)
        return tryWithPath { IntArrayNbtDecoder(nbt, reader, this) }
    }

    private fun beginLongArray(): CompositeDecoder {
        expectTagType(TAG_Long_Array)
        return tryWithPath { LongArrayNbtDecoder(nbt, reader, this) }
    }
    //endregion

    final override fun decodeNbtTag(): NbtTag = when (entryType) {
        TAG_End -> throw NbtDecodingException("Expected a value, but was Nothing", getPath())
        TAG_Byte -> NbtByte(decodeByte())
        TAG_Short -> NbtShort(decodeShort())
        TAG_Int -> NbtInt(decodeInt())
        TAG_Long -> NbtLong(decodeLong())
        TAG_Float -> NbtFloat(decodeFloat())
        TAG_Double -> NbtDouble(decodeDouble())
        TAG_Byte_Array -> NbtByteArray(decodeByteArray().asList())
        TAG_String -> NbtString(decodeString())
        TAG_List -> decodeSerializableValue(NbtList.serializer(NbtTag.serializer()))
        TAG_Compound -> decodeSerializableValue(NbtCompound.serializer())
        TAG_Int_Array -> NbtIntArray(decodeIntArray().asList())
        TAG_Long_Array -> NbtLongArray(decodeLongArray().asList())
    }

    //region Unsupported types
    private fun notSupported(type: String, path: NbtPath? = null): NbtDecodingException =
        NbtDecodingException("Decoding $type values is not supported by the NBT format", path ?: getPath())

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
        fun isArraySerializer(arraySerializer: SerializationStrategy<*>, arrayKind: NbtListKind): Boolean =
            deserializer == arraySerializer && (elementListKind == null || elementListKind == arrayKind)

        @Suppress("UNCHECKED_CAST")
        return when {
            isArraySerializer(ByteArraySerializer(), NbtListKind.ByteArray) -> decodeByteArray() as T
            isArraySerializer(IntArraySerializer(), NbtListKind.IntArray) -> decodeIntArray() as T
            isArraySerializer(LongArraySerializer(), NbtListKind.LongArray) -> decodeLongArray() as T

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
            ?.getElementNbtListKind(index)
            ?: descriptor.getElementDescriptor(index).nbtListKind

        return decodeSerializableValue(deserializer)
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal class NbtReaderDecoder(
    override val nbt: NbtFormat,
    override val reader: NbtReader,
) : BaseNbtDecoder() {
    override val parent: Nothing? = null

    private val rootTagInfo = reader.beginRootTag()

    override val entryType: NbtTagType
        get() = rootTagInfo.type

    override fun getPathNode(): NbtPath.Node =
        NbtPath.RootNode(entryType)

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = 0

    @ExperimentalSerializationApi
    override fun decodeSequentially(): Boolean {
        return super.decodeSequentially()
    }
}

private abstract class CompoundNbtDecoder : BaseNbtDecoder() {
    protected abstract val compoundEntryInfo: NbtReader.CompoundEntryInfo

    override fun getPathNode(): NbtPath.Node =
        NbtPath.NameNode(compoundEntryInfo.name, entryType)

    override fun endStructure(descriptor: SerialDescriptor) {
        reader.endCompound()
        endNamedTagIfNamed(descriptor)
    }
}

private class ClassNbtDecoder(
    override val nbt: NbtFormat,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
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
            throw NbtDecodingException(message, parent.getPath())
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
                @OptIn(ExperimentalSerializationApi::class)
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

        @OptIn(ExperimentalSerializationApi::class)
        if (index >= 0 && descriptor.getElementDescriptor(index).kind == StructureKind.LIST) {
            elementListKind = descriptor.getElementNbtListKind(index)
        }

        return index
    }
}

private class MapNbtDecoder(
    override val nbt: NbtFormat,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
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

    override fun getPathNode(): NbtPath.Node =
        NbtPath.IndexNode(index, entryType)

    protected abstract fun beginEntry(): Boolean

    final override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = elementCount

    final override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        if (beginEntry()) index++ else CompositeDecoder.DECODE_DONE

    @ExperimentalSerializationApi
    final override fun decodeSequentially(): Boolean = elementCount >= 0
}

private class ListNbtDecoder(
    override val nbt: NbtFormat,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
) : ListLikeNbtDecoder() {
    private val listInfo = reader.beginList()

    override val elementCount: Int
        get() = listInfo.size

    override val entryType: NbtTagType
        get() = listInfo.type

    override fun beginEntry(): Boolean = reader.beginListEntry()

    override fun endStructure(descriptor: SerialDescriptor): Unit = reader.endList()
}

private class ByteArrayNbtDecoder(
    override val nbt: NbtFormat,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
) : ListLikeNbtDecoder() {
    private val arrayInfo = reader.beginByteArray()

    override val elementCount: Int
        get() = arrayInfo.size

    override val entryType: NbtTagType
        get() = TAG_Byte

    override fun beginEntry(): Boolean = reader.beginByteArrayEntry()

    override fun endStructure(descriptor: SerialDescriptor): Unit = reader.endByteArray()
}

private class IntArrayNbtDecoder(
    override val nbt: NbtFormat,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
) : ListLikeNbtDecoder() {
    private val arrayInfo = reader.beginIntArray()

    override val elementCount: Int
        get() = arrayInfo.size

    override val entryType: NbtTagType
        get() = TAG_Int

    override fun beginEntry(): Boolean = reader.beginIntArrayEntry()

    override fun endStructure(descriptor: SerialDescriptor): Unit = reader.endIntArray()
}

private class LongArrayNbtDecoder(
    override val nbt: NbtFormat,
    override val reader: NbtReader,
    override val parent: BaseNbtDecoder,
) : ListLikeNbtDecoder() {
    private val arrayInfo = reader.beginLongArray()

    override val elementCount: Int
        get() = arrayInfo.size

    override val entryType: NbtTagType
        get() = TAG_Long

    override fun beginEntry(): Boolean = reader.beginLongArrayEntry()

    override fun endStructure(descriptor: SerialDescriptor): Unit = reader.endLongArray()
}
