package net.benwoodworth.knbt.internal

import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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
    fun decoding_char_should_throw_if_string_length_is_not_1() = runTest {
        checkAll(
            Arb.string(0..10)
                .filter { it.length != 1 }
                .map(::NbtString)
        ) { invalidCharNbtString ->
            assertFailsWith<NbtDecodingException> {
                NbtFormat().decodeFromNbtTag<Char>(invalidCharNbtString)
            }
        }
    }
}
