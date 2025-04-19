package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtType

/**
 * A zero/empty [NbtTag] of each type.
 */
fun ParameterizeScope.parameterOfNbtTagSubtypeEdgeCases() = parameter {
    NbtType.entries.asSequence().mapNotNull { type ->
        when (type) {
            NbtType.TAG_End -> null
            NbtType.TAG_Byte -> NbtByte(0)
            NbtType.TAG_Short -> NbtShort(0)
            NbtType.TAG_Int -> NbtInt(0)
            NbtType.TAG_Long -> NbtLong(0L)
            NbtType.TAG_Float -> NbtFloat(0.0f)
            NbtType.TAG_Double -> NbtDouble(0.0)
            NbtType.TAG_Byte_Array -> NbtByteArray(emptyList())
            NbtType.TAG_String -> NbtString("")
            NbtType.TAG_List -> NbtList(emptyList())
            NbtType.TAG_Compound -> NbtCompound(emptyMap())
            NbtType.TAG_Int_Array -> NbtIntArray(emptyList())
            NbtType.TAG_Long_Array -> NbtIntArray(emptyList())
        }
    }
}
