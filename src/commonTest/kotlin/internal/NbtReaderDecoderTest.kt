package net.benwoodworth.knbt.internal

import kotlinx.serialization.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.parameterize.parameterize
import kotlin.math.PI
import kotlin.test.*

class NbtReaderDecoderTest {
    @Serializable
    @NbtNamed("unknown-keys")
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

    @Test
    fun decoding_char_should_throw_if_string_length_is_not_1() = parameterize {
        val invalidCharNbtString by parameter {
            listOf(NbtString(""), NbtString("12"))
        }

        assertFailsWith<NbtDecodingException> {
            NbtFormat().decodeFromNbtTag<Char>(invalidCharNbtString)
        }
    }
}
