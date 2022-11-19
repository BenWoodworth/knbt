package net.benwoodworth.knbt.integration

import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtLongArray
import kotlin.test.Test

class ArraySerializationTest : SerializationTest() {
    @Test
    fun should_serialize_ByteArray_correctly() {
        assertSerializesCorrectly(
            byteArrayOf(1, 2, 3),
            NbtByteArray(byteArrayOf(1, 2, 3)),
            ByteArray::contentEquals
        )
    }

    @Test
    fun should_serialize_IntArray_correctly() {
        assertSerializesCorrectly(
            intArrayOf(1, 2, 3),
            NbtIntArray(intArrayOf(1, 2, 3)),
            IntArray::contentEquals
        )
    }

    @Test
    fun should_serialize_LongArray_correctly() {
        assertSerializesCorrectly(
            longArrayOf(1, 2, 3),
            NbtLongArray(longArrayOf(1, 2, 3)),
            LongArray::contentEquals,
        )
    }
}
