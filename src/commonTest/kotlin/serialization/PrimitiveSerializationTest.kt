package net.benwoodworth.knbt.serialization

import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.boolean
import io.kotest.property.exhaustive.bytes
import io.kotest.property.exhaustive.map
import kotlinx.coroutines.test.runTest
import net.benwoodworth.knbt.*
import kotlin.test.Test

class PrimitiveSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_Byte_correctly() = runTest {
        checkAll(Exhaustive.bytes()) { byte ->
            defaultNbt.testSerialization(byte, NbtByte(byte))
        }
    }

    @Test
    fun should_serialize_Short_correctly() = runTest {
        checkAll { short: Short ->
            defaultNbt.testSerialization(short, NbtShort(short))
        }
    }

    @Test
    fun should_serialize_Int_correctly() = runTest {
        checkAll(Arb.int()) { int: Int ->
            defaultNbt.testSerialization(int, NbtInt(int))
        }
    }

    @Test
    fun should_serialize_Long_correctly() = runTest {
        checkAll { long: Long ->
            defaultNbt.testSerialization(long, NbtLong(long))
        }
    }

    @Test
    fun should_serialize_Float_correctly() = runTest {
        checkAll { float: Float ->
            defaultNbt.testSerialization(float, NbtFloat(float))
        }
    }

    @Test
    fun should_serialize_Double_correctly() = runTest {
        checkAll { double: Double ->
            defaultNbt.testSerialization(double, NbtDouble(double))
        }
    }

    @Test
    fun should_serialize_booleans_according_to_NbtByte_boolean_converter() = runTest {
        checkAll(Exhaustive.boolean()) { boolean ->
            defaultNbt.testSerialization(boolean, NbtByte.fromBoolean(boolean))
        }
    }

    @Test
    fun should_deserialize_booleans_according_to_NbtByte_boolean_converter() = runTest {
        checkAll(Exhaustive.bytes().map(::NbtByte)) { nbtByte ->
            defaultNbt.testDeserialization(nbtByte, nbtByte.toBoolean())
        }
    }
}
