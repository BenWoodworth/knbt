package net.benwoodworth.knbt.external

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
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
}
