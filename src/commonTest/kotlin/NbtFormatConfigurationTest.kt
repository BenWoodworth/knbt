package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NbtFormatConfigurationTest {
    @Serializable
    @NbtNamed("")
    private data class TestData(
        val a: Int,
        val b: Int,
    )

    @Test
    fun should_ignore_unknown_key() = parameterizeTest {
        val nbt = NbtFormat(ignoreUnknownKeys = true)

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

        val expected = TestData(1, 2)

        assertEquals(expected, nbt.decodeFromNbtTag(tag))
    }

    @Test
    fun should_throw_for_unknown_keys_if_not_permitted() {
        val nbt = NbtFormat(ignoreUnknownKeys = false)

        val tag = buildNbtCompound("") {
            put("a", 1)
            put("b", 2)
            put("unknown", "value")
        }

        assertFailsWith<SerializationException> {
            nbt.decodeFromNbtTag<TestData>(tag)
        }
    }
}
