package net.benwoodworth.knbt.internal

import kotlinx.serialization.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.math.PI
import kotlin.test.*

class NbtReaderDecoderTest {
    @Serializable
    @SerialName("unknown-keys")
    private data class UnknownKeys(
        val int: Int,
        val string: String,
    ) {
        companion object {
            val expected = UnknownKeys(int = 42, string = "String!")
        }
    }

    private val unknownKeysTag = buildNbtCompound("unknown-keys") {
        put("int", UnknownKeys.expected.int)
        put("double", PI)
        put("string", UnknownKeys.expected.string)
    }

    @Test
    fun decoding_should_fail_on_unknown_key_if_not_ignoring() {
        val nbt = NbtFormat(ignoreUnknownKeys = false)

        assertFailsWith<NbtDecodingException> {
            nbt.decodeFromNbtTag<UnknownKeys>(unknownKeysTag)
        }
    }

    @Test
    fun decoding_should_not_fail_on_unknown_key_if_ignoring() {
        val nbt = NbtFormat(ignoreUnknownKeys = true)

        val actual = nbt.decodeFromNbtTag<UnknownKeys>(unknownKeysTag)
        assertEquals(UnknownKeys.expected, actual)
    }
}
