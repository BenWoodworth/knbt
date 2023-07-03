package net.benwoodworth.knbt.serialization

import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.parameterize.arguments.*
import net.benwoodworth.knbt.test.parameterize.parameterize
import kotlin.test.Test

class PrimitiveSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_Byte_correctly() = parameterize {
        val byte by parameter { bytes() }

        defaultNbt.testSerialization(Byte.serializer(), byte, NbtByte(byte))
    }

    @Test
    fun should_serialize_Short_correctly() = parameterize {
        val short by parameter { shortEdgeCases() }

        defaultNbt.testSerialization(Short.serializer(), short, NbtShort(short))
    }

    @Test
    fun should_serialize_Int_correctly() = parameterize {
        val int by parameter { intEdgeCases() }

        defaultNbt.testSerialization(Int.serializer(), int, NbtInt(int))
    }

    @Test
    fun should_serialize_Long_correctly() = parameterize {
        val long by parameter { longEdgeCases() }

        defaultNbt.testSerialization(Long.serializer(), long, NbtLong(long))
    }

    @Test
    fun should_serialize_Float_correctly() = parameterize {
        val float by parameter { floatEdgeCases() }

        defaultNbt.testSerialization(Float.serializer(), float, NbtFloat(float))
    }

    @Test
    fun should_serialize_Double_correctly() = parameterize {
        val double by parameter { doubleEdgeCases() }

        defaultNbt.testSerialization(Double.serializer(), double, NbtDouble(double))
    }

    @Test
    fun should_serialize_booleans_according_to_NbtByte_boolean_converter() = parameterize {
        val boolean by parameter { booleans() }

        defaultNbt.testSerialization(Boolean.serializer(), boolean, NbtByte.fromBoolean(boolean))
    }

    @Test
    fun should_deserialize_booleans_according_to_NbtByte_boolean_converter() = parameterize {
        val nbtByte by parameter { bytes().map(::NbtByte) }

        defaultNbt.testDecoding(Boolean.serializer(), nbtByte.toBoolean(), nbtByte)
    }

    @Test
    fun should_serialize_Char_correctly() = parameterize {
        val char by parameter { chars() }

        defaultNbt.testSerialization(
            Char.serializer(),
            char,
            NbtString(char.toString())
        )
    }
}
