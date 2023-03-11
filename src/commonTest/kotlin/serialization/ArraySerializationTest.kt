package net.benwoodworth.knbt.serialization

import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtLongArray
import kotlin.test.Test

class ArraySerializationTest : SerializationTest() {
    @Test
    fun should_serialize_ByteArray_correctly() {
        defaultNbt.testSerialization(
            byteArrayOf(1, 2, 3),
            NbtByteArray(byteArrayOf(1, 2, 3)),
            ByteArray::asList
        )
    }

    @Test
    fun should_serialize_IntArray_correctly() {
        defaultNbt.testSerialization(
            intArrayOf(1, 2, 3),
            NbtIntArray(intArrayOf(1, 2, 3)),
            IntArray::asList
        )
    }

    @Test
    fun should_serialize_LongArray_correctly() {
        defaultNbt.testSerialization(
            longArrayOf(1, 2, 3),
            NbtLongArray(longArrayOf(1, 2, 3)),
            LongArray::asList,
        )
    }
}