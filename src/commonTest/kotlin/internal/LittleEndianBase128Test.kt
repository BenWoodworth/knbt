package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.parameterizeTest
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalUnsignedTypes::class)
class LittleEndianBase128Test {
    private class LEB128TestCase(val ulong: ULong, val bytes: UByteArray) {
        override fun toString(): String {
            val ulongString = ulong.toString(2)
            val bytesString = bytes.joinToString(", ", "[", "]") { it.toString(2) }

            return "LEB128TestCase(ulong=$ulongString, bytes=$bytesString)"
        }
    }

    private val leb128TestCases: List<LEB128TestCase> = listOf(
        LEB128TestCase(0b00000000uL, ubyteArrayOf(0b00000000u)),
        LEB128TestCase(0b00000001uL, ubyteArrayOf(0b00000001u)),
        LEB128TestCase(0b01111111uL, ubyteArrayOf(0b01111111u)),
        LEB128TestCase(0b10000000uL, ubyteArrayOf(0b10000000u, 0b00000001u)),
        LEB128TestCase(0b11111111uL, ubyteArrayOf(0b11111111u, 0b00000001u)),
        LEB128TestCase(0b1111111111111111uL, ubyteArrayOf(0b11111111u, 0b11111111u, 0b00000011u)),
    )

    private fun UByteArray.toBinary(): Array<String> =
        map { it.toString(2).padStart(8, '0') }.toTypedArray()

    @Test
    fun Should_write_LEB128_correctly() = parameterizeTest {
        val testCase by parameter(leb128TestCases)

        val actualBytes = Buffer()
            .apply { writeLEB128(testCase.ulong) }
            .readByteArray().toUByteArray()

        assertContentEquals(testCase.bytes.toBinary(), actualBytes.toBinary())
    }

    @Test
    fun Should_read_LEB128_correctly() = parameterizeTest {
        val testCase by parameter(leb128TestCases)

        val actualULong = Buffer()
            .apply { write(testCase.bytes.toByteArray()) }
            .readLEB128(10)

        assertEquals(testCase.ulong, actualULong)
    }

    private data class ZigZagTestCase(val long: Long, val zigZagULong: ULong)

    private val zigZagTestCases = listOf(
        ZigZagTestCase(0L, 0uL),
        ZigZagTestCase(-1L, 1uL),
        ZigZagTestCase(1L, 2uL),
        ZigZagTestCase(-2L, 3uL),
        ZigZagTestCase(2147483647L, 4294967294uL),
        ZigZagTestCase(-2147483648L, 4294967295uL),
        ZigZagTestCase(Long.MAX_VALUE, ULong.MAX_VALUE - 1uL),
        ZigZagTestCase(Long.MIN_VALUE, ULong.MAX_VALUE),
    )

    @Test
    fun Should_ZigZag_encode_correctly() = parameterizeTest {
        val testCase by parameter(zigZagTestCases)

        assertEquals(testCase.zigZagULong, testCase.long.zigZagEncode())
    }

    @Test
    fun Should_ZigZag_decode_correctly() = parameterizeTest {
        val testCase by parameter(zigZagTestCases)

        assertEquals(testCase.long, testCase.zigZagULong.zigZagDecode())
    }
}
