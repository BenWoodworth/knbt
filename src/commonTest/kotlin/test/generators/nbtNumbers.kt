package net.benwoodworth.knbt.test.generators

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.*

fun ParameterizeScope.parameterOfNbtByteEdgeCases() = parameter {
    sequenceOf(
        NbtByte(0),
        NbtByte(1),
        NbtByte(-1),
        NbtByte(Byte.MAX_VALUE),
        NbtByte(Byte.MIN_VALUE),

        NbtByte(2), // Boolean edge case
    )
}

fun ParameterizeScope.parameterOfNbtShortEdgeCases() = parameter {
    sequenceOf(
        NbtShort(0),
        NbtShort(1),
        NbtShort(-1),
        NbtShort(Short.MAX_VALUE),
        NbtShort(Short.MIN_VALUE),
    )
}

fun ParameterizeScope.parameterOfNbtIntEdgeCases() = parameter {
    sequenceOf(
        NbtInt(0),
        NbtInt(1),
        NbtInt(-1),
        NbtInt(Int.MAX_VALUE),
        NbtInt(Int.MIN_VALUE),
    )
}

fun ParameterizeScope.parameterOfNbtLongEdgeCases() = parameter {
    sequenceOf(
        NbtLong(0),
        NbtLong(1),
        NbtLong(-1),
        NbtLong(Long.MAX_VALUE),
        NbtLong(Long.MIN_VALUE),
    )
}

fun ParameterizeScope.parameterOfNbtFloatEdgeCases() = parameter {
    sequenceOf(
        NbtFloat(0.0f),
        NbtFloat(-0.0f), // Different zero in binary representation

        NbtFloat(Float.MIN_VALUE),
        NbtFloat(-Float.MIN_VALUE),

        NbtFloat(Float.MAX_VALUE),
        NbtFloat(-Float.MAX_VALUE),

        NbtFloat(Float.POSITIVE_INFINITY),
        NbtFloat(Float.NEGATIVE_INFINITY),

        NbtFloat(Float.NaN),
        NbtFloat(-Float.NaN), // Different NaN in binary representation
    )
}

fun ParameterizeScope.parameterOfNbtDoubleEdgeCases() = parameter {
    sequenceOf(
        NbtDouble(0.0),
        NbtDouble(-0.0), // Different zero in binary representation

        NbtDouble(Double.MIN_VALUE),
        NbtDouble(-Double.MIN_VALUE),

        NbtDouble(Double.MAX_VALUE),
        NbtDouble(-Double.MAX_VALUE),

        NbtDouble(Double.POSITIVE_INFINITY),
        NbtDouble(Double.NEGATIVE_INFINITY),

        NbtDouble(Double.NaN),
        NbtDouble(-Double.NaN), // Different NaN in binary representation
    )
}
