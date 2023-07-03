package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.test.parameterize.arguments.bytes
import net.benwoodworth.knbt.test.parameterize.parameterize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringsTest {
    @Test
    fun converting_from_byte_to_hex_should_be_unsigned() = parameterize {
        val byte by parameter { bytes() }
        val hex = byte.toHex()

        assertTrue(hex.toInt(16) >= 0, "$hex >= 0")
    }

    @Test
    fun converting_from_byte_to_hex_should_be_convertable_back_to_an_equivalent_unsigned_byte() = parameterize {
        val byte by parameter { bytes() }

        assertEquals(byte.toUByte(), byte.toHex().toUByte(16))
    }

    @Test
    fun converting_from_byte_to_hex_should_return_a_two_digit_long_string() = parameterize {
        val byte by parameter { bytes() }
        val hex = byte.toHex()

        assertEquals(2, hex.length, "\"$hex\".length")
    }

    @Test
    fun converting_from_byte_to_hex_should_return_an_all_caps_string() = parameterize {
        val byte by parameter { bytes() }
        val hex = byte.toHex()

        assertEquals(hex.uppercase(), hex)
    }
}
