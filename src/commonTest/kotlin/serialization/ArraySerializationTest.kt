package net.benwoodworth.knbt.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.*
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtLongArray
import kotlin.test.Test

class ArraySerializationTest : SerializationTest() {
    @Test
    fun should_serialize_ByteArray_correctly() {
        defaultNbt.testSerialization(
            ByteArraySerializer(),
            byteArrayOf(1, 2, 3),
            NbtByteArray(listOf(1, 2, 3)),
            ByteArray::asList
        )
    }

    @Test
    fun should_serialize_IntArray_correctly() {
        defaultNbt.testSerialization(
            IntArraySerializer(),
            intArrayOf(1, 2, 3),
            NbtIntArray(listOf(1, 2, 3)),
            IntArray::asList
        )
    }

    @Test
    fun should_serialize_LongArray_correctly() {
        defaultNbt.testSerialization(
            LongArraySerializer(),
            longArrayOf(1, 2, 3),
            NbtLongArray(listOf(1, 2, 3)),
            LongArray::asList,
        )
    }

    @Test
    fun should_serialize_UByteArray_correctly() {
        @OptIn(ExperimentalUnsignedTypes::class, ExperimentalSerializationApi::class)
        defaultNbt.testSerialization(
            UByteArraySerializer(),
            ubyteArrayOf(1u, 2u, 3u),
            NbtByteArray(listOf(1, 2, 3)),
            UByteArray::asList
        )
    }

    @Test
    fun should_serialize_UIntArray_correctly() {
        @OptIn(ExperimentalUnsignedTypes::class, ExperimentalSerializationApi::class)
        defaultNbt.testSerialization(
            UIntArraySerializer(),
            uintArrayOf(1u, 2u, 3u),
            NbtIntArray(listOf(1, 2, 3)),
            UIntArray::asList
        )
    }

    @Test
    fun should_serialize_ULongArray_correctly() {
        @OptIn(ExperimentalUnsignedTypes::class, ExperimentalSerializationApi::class)
        defaultNbt.testSerialization(
            ULongArraySerializer(),
            ulongArrayOf(1u, 2u, 3u),
            NbtLongArray(listOf(1, 2, 3)),
            ULongArray::asList,
        )
    }
}
