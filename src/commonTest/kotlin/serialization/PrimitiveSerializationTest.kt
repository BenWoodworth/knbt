package net.benwoodworth.knbt.serialization

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.bytes
import io.kotest.property.exhaustive.map
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.*
import kotlin.test.Test

class PrimitiveSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_Byte_correctly() = runTest {
        checkAll(Exhaustive.bytes()) { byte ->
            defaultNbt.testSerialization(Byte.serializer(), byte, NbtByte(byte))
        }
    }

    @Test
    fun should_serialize_Short_correctly() = runTest {
        checkAll { short: Short ->
            defaultNbt.testSerialization(Short.serializer(), short, NbtShort(short))
        }
    }

    @Test
    fun should_serialize_Int_correctly() = runTest {
        checkAll(Arb.int()) { int: Int ->
            defaultNbt.testSerialization(Int.serializer(), int, NbtInt(int))
        }
    }

    @Test
    fun should_serialize_Long_correctly() = runTest {
        checkAll { long: Long ->
            defaultNbt.testSerialization(Long.serializer(), long, NbtLong(long))
        }
    }

    @Test
    fun should_serialize_Float_correctly() = runTest {
        checkAll { float: Float ->
            defaultNbt.testSerialization(Float.serializer(), float, NbtFloat(float))
        }
    }

    @Test
    fun should_serialize_Double_correctly() = runTest {
        checkAll { double: Double ->
            defaultNbt.testSerialization(Double.serializer(), double, NbtDouble(double))
        }
    }

    @Test
    fun should_serialize_booleans_according_to_NbtByte_boolean_converter() = runTest {
        checkAll(Exhaustive.boolean()) { boolean ->
            defaultNbt.testSerialization(Boolean.serializer(), boolean, NbtByte.fromBoolean(boolean))
        }
    }

    @Test
    fun should_deserialize_booleans_according_to_NbtByte_boolean_converter() = runTest {
        checkAll(Exhaustive.bytes().map(::NbtByte)) { nbtByte ->
            defaultNbt.testDecoding(Boolean.serializer(), nbtByte.toBoolean(), nbtByte)
        }
    }

    @Test
    fun should_serialize_Char_correctly() = runTest {
        checkAll(Arb.char(Char.MIN_VALUE..Char.MAX_VALUE)) { char ->
            defaultNbt.testSerialization(Char.serializer(), char, NbtString(char.toString()))
        }
    }
}
