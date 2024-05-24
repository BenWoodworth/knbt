package net.benwoodworth.knbt.serialization

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.fromBoolean
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.*
import net.benwoodworth.knbt.toBoolean
import kotlin.test.Test
import kotlin.test.assertEquals

class PrimitiveSerializationTest {
    @Test
    fun should_serialize_Byte_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val nbtByte by parameterOfNbtByteEdgeCases()

        nbt.verifyEncoderOrDecoder(
            Byte.serializer(),
            nbtByte.value,
            nbtByte,
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_Short_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val nbtShort by parameterOfNbtShortEdgeCases()

        nbt.verifyEncoderOrDecoder(
            Short.serializer(),
            nbtShort.value,
            nbtShort,
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_Int_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val nbtInt by parameterOfNbtIntEdgeCases()

        nbt.verifyEncoderOrDecoder(
            Int.serializer(),
            nbtInt.value,
            nbtInt,
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_Long_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val nbtLong by parameterOfNbtLongEdgeCases()

        nbt.verifyEncoderOrDecoder(
            Long.serializer(),
            nbtLong.value,
            nbtLong,
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_Float_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val nbtFloat by parameterOfNbtFloatEdgeCases()

        nbt.verifyEncoderOrDecoder(
            Float.serializer(),
            nbtFloat.value,
            nbtFloat,
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_Double_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val nbtDouble by parameterOfNbtDoubleEdgeCases()

        nbt.verifyEncoderOrDecoder(
            Double.serializer(),
            nbtDouble.value,
            nbtDouble,
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_booleans_according_to_NbtByte_boolean_converter() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val boolean by parameterOfBooleans()

        nbt.verifyEncoderOrDecoder(
            Boolean.serializer(),
            boolean,
            NbtByte.fromBoolean(boolean),
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_deserialize_booleans_according_to_NbtByte_boolean_converter() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt()
        val nbtByte by parameterOfNbtByteEdgeCases()

        nbt.verifyDecoder(
            Boolean.serializer(),
            nbtByte,
            testDecodedValue = { decodedValue ->
                assertEquals(nbtByte.toBoolean(), decodedValue)
            }
        )
    }

    @Test
    fun should_serialize_Char_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        // Character that appears in the NbtString edge cases, potentially being an incomplete part of a surrogate pair.
        val char by parameter {
            this@parameterizeTest.parameterOfNbtStringEdgeCases().arguments
                .flatMap { it.value.asIterable() }
        }

        nbt.verifyEncoderOrDecoder(
            Char.serializer(),
            char,
            NbtString(char.toString()),
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }
}
