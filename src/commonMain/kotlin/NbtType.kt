package net.benwoodworth.knbt

import kotlin.reflect.KClass

/**
 * An [NbtTag]'s type.
 * In [BinaryNbtFormat]s, this is a single [id] byte defining the contents of the tag's payload of the tag.
 */
@Suppress("EnumEntryName")
public enum class NbtType(public val id: Byte) {
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

internal fun Byte.toNbtTypeOrNull(): NbtType? =
    NbtType.entries.getOrNull(this.toInt())

internal fun KClass<out NbtTag>.toNbtType(): NbtType =
    when (simpleName) { // String cases so it's optimized to a jump table
        "NbtByte" -> NbtType.TAG_Byte
        "NbtShort" -> NbtType.TAG_Short
        "NbtInt" -> NbtType.TAG_Int
        "NbtLong" -> NbtType.TAG_Long
        "NbtFloat" -> NbtType.TAG_Float
        "NbtDouble" -> NbtType.TAG_Double
        "NbtByteArray" -> NbtType.TAG_Byte_Array
        "NbtString" -> NbtType.TAG_String
        "NbtList" -> NbtType.TAG_List
        "NbtCompound" -> NbtType.TAG_Compound
        "NbtIntArray" -> NbtType.TAG_Int_Array
        "NbtLongArray" -> NbtType.TAG_Long_Array
        else -> NbtType.TAG_End // "Nothing", or "Void" on JVM
    }
