package net.benwoodworth.knbt.external

import kotlinx.serialization.builtins.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import net.benwoodworth.knbt.test.serializers.SurrogateSerializer
import kotlin.test.Test
import kotlin.test.assertContentEquals

class ListSerializerTest {
    @Test
    fun should_serialize_List_to_NbtList() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ListSerializer(Int.serializer()),
            listOf(1, 2, 3),
            NbtList(listOf(1, 2, 3).map(::NbtInt)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_List_of_Lists_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ListSerializer(ListSerializer(Byte.serializer())),
            listOf(listOf(1.toByte()), listOf()),
            buildNbtList<NbtList<*>> {
                addNbtList<NbtByte> { add(1.toByte()) }
                addNbtList<Nothing> { }
            },
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }


    @Test
    fun should_serialize_ByteArray_to_NbtByteArray() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ByteArraySerializer(),
            byteArrayOf(1, 2, 3),
            NbtByteArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_IntArray_to_NbtIntArray() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            IntArraySerializer(),
            intArrayOf(1, 2, 3),
            NbtIntArray(listOf(1, 2, 3)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_LongArray_to_NbtLongArray() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            LongArraySerializer(),
            longArrayOf(1L, 2L, 3L),
            NbtLongArray(listOf(1L, 2L, 3L)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }


    @Test
    fun should_serialize_ByteArray_to_NbtList_with_list_serializer() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            SurrogateSerializer(ByteArray::toList, List<Byte>::toByteArray),
            byteArrayOf(1, 2, 3),
            NbtList(listOf<Byte>(1, 2, 3).map(::NbtByte)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_IntArray_to_NbtList_with_list_serializer() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            SurrogateSerializer(IntArray::toList, List<Int>::toIntArray),
            intArrayOf(1, 2, 3),
            NbtList(listOf(1, 2, 3).map(::NbtInt)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_LongArray_to_NbtList_with_list_serializer() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            SurrogateSerializer(LongArray::toList, List<Long>::toLongArray),
            longArrayOf(1L, 2L, 3L),
            NbtList(listOf(1L, 2L, 3L).map(::NbtLong)),
            testDecodedValue = { value, decodedValue ->
                assertContentEquals(value, decodedValue, "decodedValue")
            }
        )
    }
}
