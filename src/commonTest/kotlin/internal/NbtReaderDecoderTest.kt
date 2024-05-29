package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.NbtName
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertFailsWith

class NbtReaderDecoderTest {
    @Serializable
    @NbtName("unknown-keys")
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
    fun decoding_should_fail_on_unknown_key_if_not_ignoring() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt { ignoreUnknownKeys = false }

        assertFailsWith<NbtDecodingException> {
            nbt.verifyDecoder(UnknownKeys.serializer(), unknownKeysTag)
        }
    }

    @Test
    fun decoding_should_not_fail_on_unknown_key_if_ignoring() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt { ignoreUnknownKeys = true }

        nbt.verifyDecoder(UnknownKeys.serializer(), unknownKeysTag)
    }

    @Test
    fun decoding_char_should_throw_if_string_length_is_not_1() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt()

        val invalidCharNbtString by parameter {
            (0..10)
                .filter { length -> length != 1 }
                .map { length -> NbtString("x".repeat(length)) }
        }

        assertFailsWith<NbtDecodingException> {
            nbt.verifyDecoder(Char.serializer(), invalidCharNbtString)
        }
    }
}
