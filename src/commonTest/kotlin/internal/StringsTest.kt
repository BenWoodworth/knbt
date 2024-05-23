package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.test.parameterOfBytes
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringsTest {
    @Test
    fun converting_from_byte_to_hex_should_be_unsigned() = parameterizeTest {
        val byte by parameterOfBytes()

        val hex = byte.toHex()
        assertTrue(hex.toInt(16) >= 0, "$hex >= 0")
    }

    @Test
    fun converting_from_byte_to_hex_should_be_convertable_back_to_an_equivalent_unsigned_byte() = parameterizeTest {
        val byte by parameterOfBytes()

        assertEquals(byte.toUByte(), byte.toHex().toUByte(16))
    }

    @Test
    fun converting_from_byte_to_hex_should_return_a_two_digit_long_string() = parameterizeTest {
        val byte by parameterOfBytes()

        val hex = byte.toHex()
        assertEquals(2, hex.length, "\"$hex\".length")
    }

    @Test
    fun converting_from_byte_to_hex_should_return_an_all_caps_string() = parameterizeTest {
        val byte by parameterOfBytes()

        val hex = byte.toHex()
        assertEquals(hex.uppercase(), hex)
    }
}
