package net.benwoodworth.knbt.serialization

import kotlinx.serialization.builtins.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.serializers.SurrogateSerializer
import kotlin.test.Test

class ListSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_List_to_NbtList() {
        defaultNbt.testSerialization(
            ListSerializer(Int.serializer()),
            listOf(1, 2, 3),
            NbtList(listOf(1, 2, 3).map(::NbtInt)),
        )
    }

    @Test
    fun should_serialize_List_of_Lists_correctly() {
        defaultNbt.testSerialization(
            ListSerializer(ListSerializer(Byte.serializer())),
            listOf(listOf(1.toByte()), listOf()),
            buildNbtList<NbtList<*>> {
                addNbtList<NbtByte> { add(1.toByte()) }
                addNbtList<Nothing> { }
            },
        )
    }


    @Test
    fun should_serialize_ByteArray_to_NbtByteArray() {
        defaultNbt.testSerialization(
            ByteArraySerializer(),
            byteArrayOf(1, 2, 3),
            NbtByteArray(listOf(1, 2, 3)),
            ByteArray::asList
        )
    }

    @Test
    fun should_serialize_IntArray_to_NbtIntArray() {
        defaultNbt.testSerialization(
            IntArraySerializer(),
            intArrayOf(1, 2, 3),
            NbtIntArray(listOf(1, 2, 3)),
            IntArray::asList
        )
    }

    @Test
    fun should_serialize_LongArray_to_NbtLongArray() {
        defaultNbt.testSerialization(
            LongArraySerializer(),
            longArrayOf(1L, 2L, 3L),
            NbtLongArray(listOf(1L, 2L, 3L)),
            LongArray::asList
        )
    }


    @Test
    fun should_serialize_ByteArray_to_NbtList_with_list_serializer() {
        defaultNbt.testSerialization(
            SurrogateSerializer(ByteArray::toList, List<Byte>::toByteArray),
            byteArrayOf(1, 2, 3),
            NbtList(listOf<Byte>(1, 2, 3).map(::NbtByte)),
            ByteArray::asList
        )
    }

    @Test
    fun should_serialize_IntArray_to_NbtList_with_list_serializer() {
        defaultNbt.testSerialization(
            SurrogateSerializer(IntArray::toList, List<Int>::toIntArray),
            intArrayOf(1, 2, 3),
            NbtList(listOf(1, 2, 3).map(::NbtInt)),
            IntArray::asList
        )
    }

    @Test
    fun should_serialize_LongArray_to_NbtList_with_list_serializer() {
        defaultNbt.testSerialization(
            SurrogateSerializer(LongArray::toList, List<Long>::toLongArray),
            longArrayOf(1L, 2L, 3L),
            NbtList(listOf(1L, 2L, 3L).map(::NbtLong)),
            LongArray::asList
        )
    }
}