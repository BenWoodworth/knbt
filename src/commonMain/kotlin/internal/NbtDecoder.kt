package net.benwoodworth.knbt.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*

internal fun NbtDecoder(nbt: NbtFormat, reader: NbtReader): NbtDecoder = RootNbtDecoder(nbt, reader)

@OptIn(ExperimentalSerializationApi::class)
private abstract class BaseNbtDecoder : AbstractNbtDecoder() {
    override val serializersModule: SerializersModule
        get() = nbt.serializersModule

    protected abstract val reader: NbtReader
    protected abstract val parent: BaseNbtDecoder?
    protected abstract val entryType: NbtTagType

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

    //region Primitive NBT types
    override fun decodeByte(): Byte {
        expectTagType(TAG_Byte)
        return tryWithPath { reader.readByte() }
    }

    override fun decodeBoolean(): Boolean {
        expectTagType(TAG_Byte)
        return tryWithPath {
            when (val byte = reader.readByte()) {
                0.toByte() -> false
                1.toByte() -> true
                else -> throw NbtDecodingException("Expected TAG_Byte to be a Boolean (0 or 1), but was $byte")
            }
        }
    }

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

    override fun decodeByteArray(): ByteArray {
        expectTagType(TAG_Byte_Array)
        return tryWithPath { reader.readByteArray() }
    }

    override fun decodeIntArray(): IntArray {
        expectTagType(TAG_Int_Array)
        return tryWithPath { reader.readIntArray() }
    }

    override fun decodeLongArray(): LongArray {
        expectTagType(TAG_Long_Array)
        return tryWithPath { reader.readLongArray() }
    }
    //endregion

    //region Structure begin*() functions
    override fun beginCompound(descriptor: SerialDescriptor): CompositeNbtDecoder {
        expectTagType(TAG_Compound)
        return tryWithPath {
            if (descriptor.kind == StructureKind.MAP) {
                MapNbtDecoder(nbt, reader, this)
            } else {
                ClassNbtDecoder(nbt, reader, this)
            }
        }
    }

    override fun beginList(descriptor: SerialDescriptor): CompositeNbtDecoder {
        expectTagType(TAG_List)
        return tryWithPath { ListNbtDecoder(nbt, reader, this) }
    }

    override fun beginByteArray(descriptor: SerialDescriptor): CompositeNbtDecoder {
        expectTagType(TAG_Byte_Array)
        return tryWithPath { ByteArrayNbtDecoder(nbt, reader, this) }
    }

    override fun beginIntArray(descriptor: SerialDescriptor): CompositeNbtDecoder {
        expectTagType(TAG_Int_Array)
        return tryWithPath { IntArrayNbtDecoder(nbt, reader, this) }
    }

    override fun beginLongArray(descriptor: SerialDescriptor): CompositeNbtDecoder {
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
        TAG_Byte_Array -> NbtByteArray(decodeByteArray())
        TAG_String -> NbtString(decodeString())
        TAG_List -> decodeSerializableValue(NbtList.serializer(NbtTag.serializer()))
        TAG_Compound -> decodeSerializableValue(NbtCompound.serializer())
        TAG_Int_Array -> NbtIntArray(decodeIntArray())
        TAG_Long_Array -> NbtLongArray(decodeLongArray())
    }

    //region Unsupported types
    private fun notSupported(type: String, path: NbtPath? = null): NbtDecodingException =
        NbtDecodingException("Decoding $type values is not supported by the NBT format", path ?: getPath())

    final override fun decodeChar(): Char =
        throw notSupported("Char")

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

    final override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        super.decodeSerializableValue(deserializer)

    final override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>, previousValue: T?): T =
        super.decodeSerializableValue(deserializer, previousValue)

    final override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        super.beginStructure(descriptor)
    //endregion
}

private class RootNbtDecoder(
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

    override fun endStructure(descriptor: SerialDescriptor): Unit = reader.endCompound()
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
                } catch (_: Exception) {
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

        return if (compoundEntryInfo.type == TAG_End) {
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
    private val arrayInfo = reader.beginList()

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
    private val arrayInfo = reader.beginList()

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
    private val arrayInfo = reader.beginList()

    override val elementCount: Int
        get() = arrayInfo.size

    override val entryType: NbtTagType
        get() = TAG_Long

    override fun beginEntry(): Boolean = reader.beginLongArrayEntry()

    override fun endStructure(descriptor: SerialDescriptor): Unit = reader.endLongArray()
}
