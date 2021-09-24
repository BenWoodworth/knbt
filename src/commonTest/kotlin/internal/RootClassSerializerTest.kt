package net.benwoodworth.knbt.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.ExperimentalNbtApi
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalNbtApi::class)
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
    fun Should_encode_correctly() {
        assertEquals(testNbtTag, NbtFormat().encodeToNbtTag(TestNbtClass.serializer(), testNbt))
    }

    @Test
    fun Should_decode_correctly() {
        assertEquals(testNbt, NbtFormat().decodeFromNbtTag(TestNbtClass.serializer(), testNbtTag))
    }
}
