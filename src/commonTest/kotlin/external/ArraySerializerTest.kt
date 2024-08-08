package net.benwoodworth.knbt.external

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.*
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtLongArray
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ArraySerializerTest {
    private class ArraySerializerTestCase<T>(
        val serializer: KSerializer<T>,
        val array: T,
        val tag: NbtTag,
        val assertContentEquals: (expected: T, actual: T) -> Unit
    ) {
        override fun toString() = serializer.descriptor.toString()
    }

    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalSerializationApi::class)
    private val arraySerializerTestCases = listOf(
        ArraySerializerTestCase(
            ByteArraySerializer(),
            byteArrayOf(1, 2, 3),
            NbtByteArray(listOf(1, 2, 3)),
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            IntArraySerializer(),
            intArrayOf(1, 2, 3),
            NbtIntArray(listOf(1, 2, 3)),
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            LongArraySerializer(),
            longArrayOf(1, 2, 3),
            NbtLongArray(listOf(1, 2, 3)),
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            UByteArraySerializer(),
            ubyteArrayOf(1u, 2u, 3u),
            NbtByteArray(listOf(1, 2, 3)),
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            UIntArraySerializer(),
            uintArrayOf(1u, 2u, 3u),
            NbtIntArray(listOf(1, 2, 3)),
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            ULongArraySerializer(),
            ulongArrayOf(1u, 2u, 3u),
            NbtLongArray(listOf(1, 2, 3)),
            ::assertContentEquals
        ),
    )

    @Test
    fun should_serialize_array_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val arraySerializer by parameter(arraySerializerTestCases)

        fun <T> ArraySerializerTestCase<T>.test() {
            nbt.verifyEncoderOrDecoder(
                serializer,
                array,
                tag,
                testDecodedValue = { value, decodedValue ->
                    assertContentEquals(value, decodedValue)
                }
            )
        }
        arraySerializer.test() // KT-68606
    }
}
