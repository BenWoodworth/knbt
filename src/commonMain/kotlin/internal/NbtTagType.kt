package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.InternalNbtApi
import net.benwoodworth.knbt.NbtTag
import kotlin.reflect.KClass

/**
 * For internal use only. Will be marked as internal eventually.
 * @suppress
 */
@Suppress("EnumEntryName")
@InternalNbtApi
public enum class NbtTagType(internal val id: Byte) {
    TAG_End(0),
    TAG_Byte(1),
    TAG_Short(2),
    TAG_Int(3),
    TAG_Long(4),
    TAG_Float(5),
    TAG_Double(6),
    TAG_Byte_Array(7),
    TAG_String(8),
    TAG_List(9),
    TAG_Compound(10),
    TAG_Int_Array(11),
    TAG_Long_Array(12),
}

// NbtTagType entries for quick lookup, with the ID equalling the list index
// Can be replaced with this in the future: https://youtrack.jetbrains.com/issue/KT-48872
private val tags = NbtTagType.values().asList()

internal fun Byte.toNbtTagTypeOrNull(): NbtTagType? =
    tags.getOrNull(this.toInt())

internal fun KClass<out NbtTag>.toNbtTagType(): NbtTagType =
    when (simpleName) { // String cases so it's optimized to a jump table
        "NbtByte" -> NbtTagType.TAG_Byte
        "NbtShort" -> NbtTagType.TAG_Short
        "NbtInt" -> NbtTagType.TAG_Int
        "NbtLong" -> NbtTagType.TAG_Long
        "NbtFloat" -> NbtTagType.TAG_Float
        "NbtDouble" -> NbtTagType.TAG_Double
        "NbtByteArray" -> NbtTagType.TAG_Byte_Array
        "NbtString" -> NbtTagType.TAG_String
        "NbtList" -> NbtTagType.TAG_List
        "NbtCompound" -> NbtTagType.TAG_Compound
        "NbtIntArray" -> NbtTagType.TAG_Int_Array
        "NbtLongArray" -> NbtTagType.TAG_Long_Array
        else -> NbtTagType.TAG_End // "Nothing", or "Void" on JVM
    }
