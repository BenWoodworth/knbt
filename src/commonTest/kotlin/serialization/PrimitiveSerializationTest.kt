package net.benwoodworth.knbt.serialization

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.fromBoolean
import net.benwoodworth.knbt.test.generators.*
import net.benwoodworth.knbt.test.parameterOfBooleans
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.toBoolean
import kotlin.test.Test

class PrimitiveSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_Byte_correctly() = parameterizeTest {
        val nbtByte by parameterOfNbtByteEdgeCases()

        defaultNbt.testSerialization(Byte.serializer(), nbtByte.value, nbtByte)
    }

    @Test
    fun should_serialize_Short_correctly() = parameterizeTest {
        val nbtShort by parameterOfNbtShortEdgeCases()

        defaultNbt.testSerialization(Short.serializer(), nbtShort.value, nbtShort)
    }

    @Test
    fun should_serialize_Int_correctly() = parameterizeTest {
        val nbtInt by parameterOfNbtIntEdgeCases()

        defaultNbt.testSerialization(Int.serializer(), nbtInt.value, nbtInt)
    }

    @Test
    fun should_serialize_Long_correctly() = parameterizeTest {
        val nbtLong by parameterOfNbtLongEdgeCases()

        defaultNbt.testSerialization(Long.serializer(), nbtLong.value, nbtLong)
    }

    @Test
    fun should_serialize_Float_correctly() = parameterizeTest {
        val nbtFloat by parameterOfNbtFloatEdgeCases()

        defaultNbt.testSerialization(Float.serializer(), nbtFloat.value, nbtFloat)
    }

    @Test
    fun should_serialize_Double_correctly() = parameterizeTest {
        val nbtDouble by parameterOfNbtDoubleEdgeCases()

        defaultNbt.testSerialization(Double.serializer(), nbtDouble.value, nbtDouble)
    }

    @Test
    fun should_serialize_booleans_according_to_NbtByte_boolean_converter() = parameterizeTest {
        val boolean by parameterOfBooleans()

        defaultNbt.testSerialization(Boolean.serializer(), boolean, NbtByte.fromBoolean(boolean))
    }

    @Test
    fun should_deserialize_booleans_according_to_NbtByte_boolean_converter() = parameterizeTest {
        val nbtByte by parameterOfNbtByteEdgeCases()

        defaultNbt.testDecoding(Boolean.serializer(), nbtByte.toBoolean(), nbtByte)
    }

    @Test
    fun should_serialize_Char_correctly() = parameterizeTest {
        // Character that appears in the NbtString edge cases, including half of a surrogate pair.
        val char by parameter {
            this@parameterizeTest.parameterOfNbtStringEdgeCases().arguments
                .flatMap { it.value.asIterable() }
        }

        defaultNbt.testSerialization(Char.serializer(), char, NbtString(char.toString()))
    }
}
