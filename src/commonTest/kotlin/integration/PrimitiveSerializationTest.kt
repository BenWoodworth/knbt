package net.benwoodworth.knbt.integration

import net.benwoodworth.knbt.*
import kotlin.test.Test

class PrimitiveSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_Byte_correctly() {
        defaultNbt.testSerialization(4.toByte(), NbtByte(4))
    }

    @Test
    fun should_serialize_Short_correctly() {
        defaultNbt.testSerialization(5.toShort(), NbtShort(5))
    }

    @Test
    fun should_serialize_Int_correctly() {
        defaultNbt.testSerialization(6, NbtInt(6))
    }

    @Test
    fun should_serialize_Long_correctly() {
        defaultNbt.testSerialization(7L, NbtLong(7L))
    }

    @Test
    fun should_serialize_Float_correctly() {
        defaultNbt.testSerialization(3.14f, NbtFloat(3.14f))
    }

    @Test
    fun should_serialize_Double_correctly() {
        defaultNbt.testSerialization(3.14, NbtDouble(3.14))
    }
}
