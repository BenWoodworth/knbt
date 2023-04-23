package net.benwoodworth.knbt.internal

import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.bytes
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StringsTest {
    @Test
    fun converting_from_byte_to_hex_should_be_unsigned() = runTest {
        checkAll(Exhaustive.bytes()) { byte ->
            val hex = byte.toHex()
            assertTrue(hex.toInt(16) >= 0, "$hex >= 0")
        }
    }

    @Test
    fun converting_from_byte_to_hex_should_be_convertable_back_to_an_equivalent_unsigned_byte() = runTest {
        checkAll(Exhaustive.bytes()) { byte ->
            assertEquals(byte.toUByte(), byte.toHex().toUByte(16))
        }
    }

    @Test
    fun converting_from_byte_to_hex_should_return_a_two_digit_long_string() = runTest {
        checkAll(Exhaustive.bytes()) { byte ->
            val hex = byte.toHex()
            assertEquals(2, hex.length, "\"$hex\".length")
        }
    }

    @Test
    fun converting_from_byte_to_hex_should_return_an_all_caps_string() = runTest {
        checkAll(Exhaustive.bytes()) { byte ->
            val hex = byte.toHex()
            assertEquals(hex.uppercase(), hex)
        }
    }
}
