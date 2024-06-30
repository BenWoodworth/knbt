package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NbtFormatConfigurationTest {
    @Serializable
    @NbtName("")
    private data class TestData(
        val a: Int,
        val b: Int,
    )

    @Test
    fun should_ignore_unknown_key() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt { ignoreUnknownKeys = true }

        val entries = mutableListOf<Pair<String, NbtTag>>(
            "a" to NbtInt(1),
            "b" to NbtInt(2),
        )

        val unknownKey = "unknown" to NbtString("value")
        val unknownKeyIndex by parameter(0..entries.size)

        entries.add(unknownKeyIndex, unknownKey)
        val tag = buildNbtCompound("") {
            entries.forEach { (name, value) ->
                put(name, value)
            }
        }

        nbt.verifyDecoder(
            TestData.serializer(),
            tag,
            testDecodedValue = { decodedValue ->
                assertEquals(TestData(1, 2), decodedValue)
            }
        )
    }

    @Test
    fun should_throw_for_unknown_keys_if_not_permitted() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt { ignoreUnknownKeys = false }

        val tag = buildNbtCompound("") {
            put("a", 1)
            put("b", 2)
            put("unknown", "value")
        }

        assertFailsWith<SerializationException> {
            nbt.verifyDecoder(
                TestData.serializer(),
                tag
            )
        }
    }
}
