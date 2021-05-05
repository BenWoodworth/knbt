package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.InternalNbtApi
import net.benwoodworth.knbt.NbtException

@Suppress("EnumEntryName")
@InternalNbtApi
/**
 * For internal use only. Will be marked as internal eventually.
 */
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
    ;

    internal companion object {
        fun fromId(id: Byte): NbtTagType = when (id) {
            0.toByte() -> TAG_End
            1.toByte() -> TAG_Byte
            2.toByte() -> TAG_Short
            3.toByte() -> TAG_Int
            4.toByte() -> TAG_Long
            5.toByte() -> TAG_Float
            6.toByte() -> TAG_Double
            7.toByte() -> TAG_Byte_Array
            8.toByte() -> TAG_String
            9.toByte() -> TAG_List
            10.toByte() -> TAG_Compound
            11.toByte() -> TAG_Int_Array
            12.toByte() -> TAG_Long_Array
            else -> {
                val hex = id.toUByte().toString(16).uppercase().padStart(2, '0')
                throw NbtException("Unknown NBT tag type ID: 0x$hex")
            }
        }
    }
}
