package net.benwoodworth.knbt.external

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.*
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtLongArray
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ArraySerializerTest {
    @Test
    fun should_serialize_ByteArray_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ByteArraySerializer(),
            byteArrayOf(1, 2, 3),
            NbtByteArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue)
            }
        )
    }

    @Test
    fun should_serialize_IntArray_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            IntArraySerializer(),
            intArrayOf(1, 2, 3),
            NbtIntArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue)
            }
        )
    }

    @Test
    fun should_serialize_LongArray_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            LongArraySerializer(),
            longArrayOf(1, 2, 3),
            NbtLongArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue)
            }
        )
    }

    @Test
    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalSerializationApi::class)
    fun should_serialize_UByteArray_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            UByteArraySerializer(),
            ubyteArrayOf(1u, 2u, 3u),
            NbtByteArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue)
            }
        )
    }

    @Test
    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalSerializationApi::class)
    fun should_serialize_UIntArray_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            UIntArraySerializer(),
            uintArrayOf(1u, 2u, 3u),
            NbtIntArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue)
            }
        )
    }

    @Test
    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalSerializationApi::class)
    fun should_serialize_ULongArray_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ULongArraySerializer(),
            ulongArrayOf(1u, 2u, 3u),
            NbtLongArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue)
            }
        )
    }
}
