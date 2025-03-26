package net.benwoodworth.knbt

import kotlin.reflect.KClass

/**
 * An [NbtTag]'s type.
 * In [BinaryNbtFormat]s, this is a single [id] byte defining the contents of the payload of the tag.
 */
@Suppress("EnumEntryName")
public enum class NbtTagType(public val id: Byte) {
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

internal fun Byte.toNbtTagTypeOrNull(): NbtTagType? =
    NbtTagType.entries.getOrNull(this.toInt())

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
