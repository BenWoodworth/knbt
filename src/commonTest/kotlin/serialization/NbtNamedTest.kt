package net.benwoodworth.knbt.serialization

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtNamedTest {
    @Serializable
    @NbtNamed("root-name")
    private data class TestNbtClass(
        val string: String,
        val int: Int,
    )

    private val testNbt = TestNbtClass(
        string = "string",
        int = 42,
    )

    private val testNbtTag = buildNbtCompound("root-name") {
        put("string", "string")
        put("int", 42)
    }

    @Test
    fun should_encode_correctly() {
        assertEquals(testNbtTag, NbtFormat().encodeToNbtTag(testNbt))
    }

    @Test
    fun should_decode_correctly() {
        assertEquals(testNbt, NbtFormat().decodeFromNbtTag(testNbtTag))
    }
}
