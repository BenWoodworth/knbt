package net.benwoodworth.knbt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NbtFormatConfigurationTest {
    @Serializable
    @SerialName("")
    private data class TestData(
        val a: Int,
        val b: Int,
    )

    @Test
    fun Should_ignore_unknown_key_at_beginning() {
        val nbt = NbtFormat(ignoreUnknownKeys = true)

        val tag = buildNbtCompound("") {
            put("unknown", "value")
            put("a", 1)
            put("b", 2)
        }

        val expected = TestData(1, 2)

        assertEquals(expected, nbt.decodeFromNbtTag(tag))
    }

    @Test
    fun Should_ignore_unknown_key_at_middle() {
        val nbt = NbtFormat(ignoreUnknownKeys = true)

        val tag = buildNbtCompound("") {
            put("a", 1)
            put("unknown", "value")
            put("b", 2)
        }

        val expected = TestData(1, 2)

        assertEquals(expected, nbt.decodeFromNbtTag(tag))
    }

    @Test
    fun Should_ignore_unknown_key_at_end() {
        val nbt = NbtFormat(ignoreUnknownKeys = true)

        val tag = buildNbtCompound("") {
            put("a", 1)
            put("b", 2)
            put("unknown", "value")
        }

        val expected = TestData(1, 2)

        assertEquals(expected, nbt.decodeFromNbtTag(tag))
    }

    @Test
    fun Should_throw_for_unknown_keys_if_not_permitted() {
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
