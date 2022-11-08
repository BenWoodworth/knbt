package net.benwoodworth.knbt.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.decodeFromNbtTag
import net.benwoodworth.knbt.encodeToNbtTag
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.test.Test
import kotlin.test.assertEquals

class RootClassSerializerTest {
    @Serializable
    @SerialName("root-name")
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
