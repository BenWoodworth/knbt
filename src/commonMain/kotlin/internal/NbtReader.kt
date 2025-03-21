package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.NbtTagType.*
import kotlin.jvm.JvmInline

/**
 * An interface for reading NBT data.
 *
 * Reading begins with a call to [beginRootTag].
 *
 * A value is read with a call to one of: [beginCompound], [beginList], [beginByteArray], [beginIntArray],
 * [beginLongArray], [readByte], [readShort], [readInt], [readLong], [readFloat], [readDouble], [readString].
 */
internal interface NbtReader {
    companion object {
        const val UNKNOWN_SIZE: Int = -1
    }

    /**
     * Followed by a call to read a value of the same type.
     */
    fun beginRootTag(): RootTagInfo

    /**
     * Followed by calls to [beginCompoundEntry], then a call to [endCompound]
     */
    fun beginCompound()

    /**
     * If `type != TAG_End`, then followed by a call to read a value of the same type.
     */
    fun beginCompoundEntry(): CompoundEntryInfo

    fun endCompound()

    /**
     * Followed by calls to [beginListEntry], then [endList].
     */
    fun beginList(): ListInfo

    /**
     * If true, then followed by reading a value of the same type.
     */
    fun beginListEntry(): Boolean

    fun endList()

    /**
     * Followed by calls to [beginByteArrayEntry], then [endByteArray].
     */
    fun beginByteArray(): ArrayInfo

    /**
     * If true, then followed by a call to [readByte].
     */
    fun beginByteArrayEntry(): Boolean

    fun endByteArray()

    /**
     * Followed by calls to [beginIntArrayEntry], then [endIntArray].
     */
    fun beginIntArray(): ArrayInfo

    /**
     * If true, then followed by a call to [readInt].
     */
    fun beginIntArrayEntry(): Boolean

    fun endIntArray()

    /**
     * Followed by calls to [beginLongArrayEntry], then [endLongArray].
     */
    fun beginLongArray(): ArrayInfo

    /**
     * If true, then followed by a call to [readLong].
     */
    fun beginLongArrayEntry(): Boolean

    fun endLongArray()

    fun readByte(): Byte

    fun readShort(): Short

    fun readInt(): Int

    fun readLong(): Long

    fun readFloat(): Float

    fun readDouble(): Double

    fun readString(): String

    @JvmInline
    value class RootTagInfo(val type: NbtTagType)

    data class CompoundEntryInfo(
        val type: NbtTagType,
        val name: String,
    ) {
        companion object {
            val End = CompoundEntryInfo(TAG_End, "")
        }
    }

    data class ListInfo(
        val type: NbtTagType,
        val size: Int,
    )

    @JvmInline
    value class ArrayInfo(
        val size: Int,
    )
}

private inline fun <TArray, TEntry> NbtReader.readArray(
    createArray: (size: Int) -> TArray,
    set: TArray.(index: Int, value: TEntry) -> Unit,
    beginArray: NbtReader.() -> NbtReader.ArrayInfo,
    beginArrayEntry: NbtReader.() -> Boolean,
    readEntry: NbtReader.() -> TEntry,
    endArray: NbtReader.() -> Unit,
    toEntryArray: List<TEntry>.() -> TArray,
): TArray {
    val size = beginArray().size
    return if (size == NbtReader.UNKNOWN_SIZE) {
        arrayListOf<TEntry>().apply {
            while (beginArrayEntry()) {
                add(readEntry())
            }
            endArray()
        }.toEntryArray()
    } else {
        createArray(size).apply {
            repeat(size) { index ->
                set(index, readEntry())
            }
            endArray()
        }
    }
}

internal fun NbtReader.readByteArray(): ByteArray = readArray(
    createArray = { ByteArray(it) },
    set = { index, value -> this[index] = value },
    beginArray = { beginByteArray() },
    beginArrayEntry = { beginByteArrayEntry() },
    readEntry = { readByte() },
    endArray = { endByteArray() },
    toEntryArray = { toByteArray() },
)

internal fun NbtReader.readIntArray(): IntArray = readArray(
    createArray = { IntArray(it) },
    set = { index, value -> this[index] = value },
    beginArray = { beginIntArray() },
    beginArrayEntry = { beginIntArrayEntry() },
    readEntry = { readInt() },
    endArray = { endIntArray() },
    toEntryArray = { toIntArray() },
)

internal fun NbtReader.readLongArray(): LongArray = readArray(
    createArray = { LongArray(it) },
    set = { index, value -> this[index] = value },
    beginArray = { beginLongArray() },
    beginArrayEntry = { beginLongArrayEntry() },
    readEntry = { readLong() },
    endArray = { endLongArray() },
    toEntryArray = { toLongArray() },
)

private inline fun NbtReader.discardTagEntries(
    size: Int,
    beginEntry: NbtReader.() -> Boolean,
    discardEntry: NbtReader.() -> Unit,
) {
    if (size == NbtReader.UNKNOWN_SIZE) {
        while (beginEntry()) {
            discardEntry()
        }
    } else {
        repeat(size) {
            discardEntry()
        }
    }
}

internal fun NbtReader.discardListTag(): NbtReader.ListInfo {
    val info = beginList()
    discardTagEntries(info.size, { beginListEntry() }, { discardTag(info.type) })
    endList()
    return info
}

internal fun NbtReader.discardTag(type: NbtTagType) {
    when (type) {
        TAG_End -> error("Unexpected $TAG_End")
        TAG_Byte -> readByte()
        TAG_Short -> readShort()
        TAG_Int -> readInt()
        TAG_Long -> readLong()
        TAG_Float -> readFloat()
        TAG_Double -> readDouble()
        TAG_Byte_Array -> {
            val info = beginByteArray()
            discardTagEntries(info.size, { beginByteArrayEntry() }, { readByte() })
            endByteArray()
        }
        TAG_String -> readString()
        TAG_List -> discardListTag()
        TAG_Compound -> {
            beginCompound()
            while (true) {
                val entryInfo = beginCompoundEntry()
                if (entryInfo.type == TAG_End) break
                discardTag(entryInfo.type)
            }
            endCompound()
        }
        TAG_Int_Array -> {
            val info = beginIntArray()
            discardTagEntries(info.size, { beginIntArrayEntry() }, { readInt() })
            endIntArray()
        }
        TAG_Long_Array -> {
            val info = beginLongArray()
            discardTagEntries(info.size, { beginLongArrayEntry() }, { readLong() })
            endLongArray()
        }
    }
}
