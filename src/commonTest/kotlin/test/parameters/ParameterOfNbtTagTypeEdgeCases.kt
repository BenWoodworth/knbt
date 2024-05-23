package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType

/**
 * A zero/empty [NbtTag] of each type.
 */
fun ParameterizeScope.parameterOfNbtTagSubtypeEdgeCases() = parameter {
    NbtTagType.entries.asSequence().mapNotNull { type ->
        when (type) {
            NbtTagType.TAG_End -> null
            NbtTagType.TAG_Byte -> NbtByte(0)
            NbtTagType.TAG_Short -> NbtShort(0)
            NbtTagType.TAG_Int -> NbtInt(0)
            NbtTagType.TAG_Long -> NbtLong(0L)
            NbtTagType.TAG_Float -> NbtFloat(0.0f)
            NbtTagType.TAG_Double -> NbtDouble(0.0)
            NbtTagType.TAG_Byte_Array -> NbtByteArray(emptyList())
            NbtTagType.TAG_String -> NbtString("")
            NbtTagType.TAG_List -> NbtList(emptyList())
            NbtTagType.TAG_Compound -> NbtCompound(emptyMap())
            NbtTagType.TAG_Int_Array -> NbtIntArray(emptyList())
            NbtTagType.TAG_Long_Array -> NbtIntArray(emptyList())
        }
    }
}
