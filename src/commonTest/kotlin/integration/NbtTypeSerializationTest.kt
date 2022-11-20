package net.benwoodworth.knbt.integration

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.*
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.serializers.ListSerializerWithAnnotations
import kotlin.test.Test

class NbtTypeSerializationTest : SerializationTest() {
    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T, reified TNbt : NbtTag> listSerializer(): KSerializer<List<T>> =
        ListSerializerWithAnnotations(serializer(), listOf(NbtType(TNbt::class)))

    @Test
    fun should_serialize_List_to_NbtByteArray_according_to_the_descriptor_NbtType() {
        defaultNbt.testSerialization(
            listSerializer<_, NbtByteArray>(),
            listOf<Byte>(1, 2, 3),
            NbtByteArray(byteArrayOf(1, 2, 3))
        )
    }

    @Test
    fun should_serialize_List_to_NbtIntArray_according_to_the_descriptor_NbtType() {
        defaultNbt.testSerialization(
            listSerializer<_, NbtIntArray>(),
            listOf(1, 2, 3),
            NbtIntArray(intArrayOf(1, 2, 3))
        )
    }

    @Test
    fun should_serialize_List_to_NbtLongArray_according_to_the_descriptor_NbtType() {
        defaultNbt.testSerialization(
            listSerializer<_, NbtLongArray>(),
            listOf(1L, 2L, 3L),
            NbtLongArray(longArrayOf(1L, 2L, 3L))
        )
    }
}
