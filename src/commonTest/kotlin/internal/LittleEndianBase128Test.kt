package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.test.parameterize.parameterize
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalUnsignedTypes::class)
class LittleEndianBase128Test {
    private class LEB128Parameters(val ulong: ULong, val bytes: UByteArray)

    private val leb128TestValues: List<LEB128Parameters> = listOf(
        LEB128Parameters(0b00000000uL, ubyteArrayOf(0b00000000u)),
        LEB128Parameters(0b00000001uL, ubyteArrayOf(0b00000001u)),
        LEB128Parameters(0b01111111uL, ubyteArrayOf(0b01111111u)),
        LEB128Parameters(0b10000000uL, ubyteArrayOf(0b10000000u, 0b00000001u)),
        LEB128Parameters(0b11111111uL, ubyteArrayOf(0b11111111u, 0b00000001u)),
        LEB128Parameters(0b1111111111111111uL, ubyteArrayOf(0b11111111u, 0b11111111u, 0b00000011u)),
    )

    private fun UByteArray.toBinary(): Array<String> =
        map { it.toString(2).padStart(8, '0') }.toTypedArray()

    @Test
    fun should_write_LEB128_correctly() = parameterize {
        val testValues by parameter { leb128TestValues }

        val actualBytes = Buffer()
            .apply { writeLEB128(testValues.ulong) }
            .readByteArray().toUByteArray()

        assertContentEquals(testValues.bytes.toBinary(), actualBytes.toBinary())
    }

    @Test
    fun should_read_LEB128_correctly() = parameterize {
        val testValues by parameter { leb128TestValues }

        val actualULong = Buffer()
            .apply { write(testValues.bytes.toByteArray()) }
            .readLEB128(10)

        assertEquals(testValues.ulong, actualULong)
    }

    private data class ZigZagParameters(val long: Long, val zigZagULong: ULong)

    private val zigZagTestValues = listOf(
        ZigZagParameters(0L, 0uL),
        ZigZagParameters(-1L, 1uL),
        ZigZagParameters(1L, 2uL),
        ZigZagParameters(-2L, 3uL),
        ZigZagParameters(2147483647L, 4294967294uL),
        ZigZagParameters(-2147483648L, 4294967295uL),
        ZigZagParameters(Long.MAX_VALUE, ULong.MAX_VALUE - 1uL),
        ZigZagParameters(Long.MIN_VALUE, ULong.MAX_VALUE),
    )

    @Test
    fun should_ZigZag_encode_correctly() = parameterize {
        val testValues by parameter { zigZagTestValues }

        assertEquals(testValues.zigZagULong, testValues.long.zigZagEncode())
    }

    @Test
    fun should_ZigZag_decode_correctly() = parameterize {
        val testValues by parameter { zigZagTestValues }

        assertEquals(testValues.long, testValues.zigZagULong.zigZagDecode())
    }
}
