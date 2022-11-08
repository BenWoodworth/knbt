package net.benwoodworth.knbt.internal

import io.kotest.inspectors.forAll
import io.kotest.matchers.ints.shouldNotBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldHaveLength
import kotlin.test.Test

class StringsTest {
    private val bytes = (Byte.MIN_VALUE..Byte.MAX_VALUE).map { it.toByte() }

    @Test
    fun converting_from_byte_to_hex_should_be_unsigned() {
        bytes.forAll { byte ->
            byte.toHex().toInt(16) shouldNotBeLessThan 0
        }
    }

    @Test
    fun converting_from_byte_to_hex_should_be_convertable_back_to_an_equivalent_unsigned_byte() {
        bytes.forAll { byte ->
            byte.toHex().toUByte(16) shouldBe byte.toUByte()
        }
    }

    @Test
    fun converting_from_byte_to_hex_should_return_a_two_digit_long_string() {
        bytes.forAll { byte ->
            byte.toHex() shouldHaveLength 2
        }
    }

    @Test
    fun converting_from_byte_to_hex_should_return_an_all_caps_string() {
        bytes.forAll { byte ->
            val hex = byte.toHex()
            hex shouldBe hex.uppercase()
        }
    }
}
