package net.benwoodworth.knbt.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.tag.*

internal fun NbtDecoder(nbt: Nbt, reader: NbtReader): NbtDecoder = RootNbtDecoder(nbt, reader)

@OptIn(ExperimentalSerializationApi::class)
private abstract class BaseNbtDecoder : AbstractNbtDecoder() {
    override val serializersModule: SerializersModule
        get() = nbt.serializersModule

    protected abstract val nbt: Nbt
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

    protected fun Throwable.withContext(path: NbtPath = getPath()): Throwable {
        val message = buildString {
            append("Error while decoding '$path'")
            message?.let { append(": ").append(it) }
        }

        return when (this) {
            is SerializationException -> NbtDecodingException(message, this)
            is Exception -> Exception(message, this)
            is Error -> Error(message, this)
            else -> Throwable(message, this)
        }
    }

    protected inline fun <T> tryWithContext(decode: () -> T): T {
        try {
            return decode()
        } catch (t: Throwable) {
            throw t.withContext()
        }
    }

    private fun expectTagType(expected: NbtTagType) {
        val actual = entryType
        if (expected != actual) {
            throw NbtDecodingException("Expected ${expected.friendlyName}, but was ${actual.friendlyName}")
        }
    }

    //region Primitive NBT types
    override fun decodeByte(): Byte = tryWithContext {
        expectTagType(TAG_Byte)
        return reader.readByte()
    }

    override fun decodeShort(): Short = tryWithContext {
        expectTagType(TAG_Short)
        return reader.readShort()
    }

    override fun decodeInt(): Int = tryWithContext {
        expectTagType(TAG_Int)
        return reader.readInt()
    }

    override fun decodeLong(): Long = tryWithContext {
        expectTagType(TAG_Long)
        return reader.readLong()
    }

    override fun decodeFloat(): Float = tryWithContext {
        expectTagType(TAG_Float)
        return reader.readFloat()
    }

    override fun decodeDouble(): Double = tryWithContext {
        expectTagType(TAG_Double)
        return reader.readDouble()
    }

    override fun decodeString(): String = tryWithContext {
        expectTagType(TAG_String)
        return reader.readString()
    }

    override fun decodeByteArray(): ByteArray = tryWithContext {
        expectTagType(TAG_Byte_Array)
        return reader.readByteArray()
    }

    override fun decodeIntArray(): IntArray = tryWithContext {
        expectTagType(TAG_Int_Array)
        return reader.readIntArray()
    }

    override fun decodeLongArray(): LongArray = tryWithContext {
        expectTagType(TAG_Long_Array)
        return reader.readLongArray()
    }
    //endregion

    //region Structure begin*() functions
    override fun beginCompound(descriptor: SerialDescriptor): CompositeNbtDecoder = tryWithContext {
        expectTagType(TAG_Compound)
        return if (descriptor.kind == StructureKind.MAP) {
            MapNbtDecoder(nbt, reader, this)
        } else {
            ClassNbtDecoder(nbt, reader, this)
        }
    }

    override fun beginList(descriptor: SerialDescriptor): CompositeNbtDecoder = tryWithContext {
        expectTagType(TAG_List)
        return ListNbtDecoder(nbt, reader, this)
    }

    override fun beginByteArray(descriptor: SerialDescriptor): CompositeNbtDecoder = tryWithContext {
        expectTagType(TAG_Byte_Array)
        return ByteArrayNbtDecoder(nbt, reader, this)
    }

    override fun beginIntArray(descriptor: SerialDescriptor): CompositeNbtDecoder = tryWithContext {
        expectTagType(TAG_Int_Array)
        return IntArrayNbtDecoder(nbt, reader, this)
    }

    override fun beginLongArray(descriptor: SerialDescriptor): CompositeNbtDecoder = tryWithContext {
        expectTagType(TAG_Long_Array)
        return LongArrayNbtDecoder(nbt, reader, this)
    }
    //endregion

    final override fun decodeNbtTag(): NbtTag = when (entryType) {
        TAG_End -> throw NbtDecodingException("Expected a value, but was Nothing")
        TAG_Byte -> NbtByte(decodeByte())
        TAG_Short -> NbtShort(decodeShort())
        TAG_Int -> NbtInt(decodeInt())
        TAG_Long -> NbtLong(decodeLong())
        TAG_Float -> NbtFloat(decodeFloat())
        TAG_Double -> NbtDouble(decodeDouble())
        TAG_Byte_Array -> NbtByteArray(decodeByteArray())
        TAG_String -> NbtString(decodeString())
        TAG_List -> decodeSerializableValue(NbtList.serializer(NbtTag.serializer()))
        TAG_Compound -> decodeSerializableValue(NbtCompound.serializer(NbtTag.serializer()))
        TAG_Int_Array -> NbtIntArray(decodeIntArray())
        TAG_Long_Array -> NbtLongArray(decodeLongArray())
    }

    //region Unsupported types
    private fun notSupported(type: String): NbtDecodingException =
        NbtDecodingException("Decoding $type values is not supported by the NBT format")

    final override fun decodeBoolean(): Boolean =
        throw notSupported("Boolean").withContext()

    final override fun decodeChar(): Char =
        throw notSupported("Char").withContext()

    final override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
        throw notSupported("Enum").withContext()
    //endregion

    //region Final super implementations
    final override fun decodeNotNullMark(): Boolean =
        super.decodeNotNullMark()

    final override fun decodeNull(): Nothing? =
        super.decodeNull()

    final override fun decodeInline(inlineDescriptor: SerialDescriptor): Decoder =
        super.decodeInline(inlineDescriptor)

    final override fun decodeValue(): Any =
        super.decodeValue()

    final override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T =
        super.decodeSerializableValue(deserializer)

    final override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>, previousValue: T?): T =
        super.decodeSerializableValue(deserializer, previousValue)

    @ExperimentalSerializationApi
    final override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder =
        super.beginStructure(descriptor)
    //endregion
}

private class RootNbtDecoder(
    override val nbt: Nbt,
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
    override val nbt: Nbt,
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
                    "${TAG_List.friendlyName}<${entryType.friendlyName}>"
                } catch (e: Exception) {
                    info.type.friendlyName
                }
            } else {
                info.type.friendlyName
            }

        if (!nbt.configuration.ignoreUnknownKeys) {
            val discardedType = discardTagAndGetTypeName()
            val message = "Encountered unknown key '${info.name}' ($discardedType)"
            throw NbtDecodingException(message).withContext(parent.getPath())
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
                @OptIn(ExperimentalSerializationApi::class)
                index = descriptor.getElementIndex(compoundEntryInfo.name)

                if (index == CompositeDecoder.UNKNOWN_NAME) {
                    handleUnknownKey(compoundEntryInfo)
                    compoundEntryInfo = reader.beginCompoundEntry()
                }
            } while (index == CompositeDecoder.UNKNOWN_NAME)

            index
        }
    }
}

private class MapNbtDecoder(
    override val nbt: Nbt,
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
    override val nbt: Nbt,
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
    override val nbt: Nbt,
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
    override val nbt: Nbt,
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
    override val nbt: Nbt,
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
