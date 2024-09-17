package net.benwoodworth.knbt.external

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.*
import net.benwoodworth.knbt.*
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
            buildNbtList<NbtByte> { add(1); add(2); add(3) },
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            IntArraySerializer(),
            intArrayOf(1, 2, 3),
            buildNbtList<NbtInt> { add(1); add(2); add(3) },
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            LongArraySerializer(),
            longArrayOf(1, 2, 3),
            buildNbtList<NbtLong> { add(1); add(2); add(3) },
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            UByteArraySerializer(),
            ubyteArrayOf(1u, 2u, 3u),
            buildNbtList<NbtByte> { add(1); add(2); add(3) },
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            UIntArraySerializer(),
            uintArrayOf(1u, 2u, 3u),
            buildNbtList<NbtInt> { add(1); add(2); add(3) },
            ::assertContentEquals
        ),
        ArraySerializerTestCase(
            ULongArraySerializer(),
            ulongArrayOf(1u, 2u, 3u),
            buildNbtList<NbtLong> { add(1); add(2); add(3) },
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
