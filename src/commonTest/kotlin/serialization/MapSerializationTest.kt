package net.benwoodworth.knbt.serialization

import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import kotlin.test.Test
import kotlin.test.assertEquals

class MapSerializationTest {
    @Test
    fun should_serialize_compound_with_no_entries_to_Map_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            MapSerializer(String.serializer(), Int.serializer()),
            mapOf(),
            buildNbtCompound { },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_compound_with_one_entry_to_Map_correctly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            MapSerializer(String.serializer(), Int.serializer()),
            mapOf("property" to 7),
            buildNbtCompound {
                put("property", 7)
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }
}
