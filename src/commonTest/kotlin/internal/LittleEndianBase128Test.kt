package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.assertForEach
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalUnsignedTypes::class)
class LittleEndianBase128Test {
    private val leb128TestValues: List<Pair<ULong, UByteArray>> = listOf(
        0b00000000uL to ubyteArrayOf(0b00000000u),
        0b00000001uL to ubyteArrayOf(0b00000001u),
        0b01111111uL to ubyteArrayOf(0b01111111u),
        0b10000000uL to ubyteArrayOf(0b10000000u, 0b00000001u),
        0b11111111uL to ubyteArrayOf(0b11111111u, 0b00000001u),
        0b1111111111111111uL to ubyteArrayOf(0b11111111u, 0b11111111u, 0b00000011u),
    )

    private fun UByteArray.toBinary(): Array<String> =
        map { it.toString(2).padStart(8, '0') }.toTypedArray()

    @Test
    fun Should_write_LEB128_correctly() {
        leb128TestValues.assertForEach { (ulong, expectedBytes) ->
            val actualBytes = Buffer()
                .apply { writeLEB128(ulong) }
                .readByteArray().toUByteArray()

            assertContentEquals(expectedBytes.toBinary(), actualBytes.toBinary(), "Wrote $ulong incorrectly")
        }
    }

    @Test
    fun Should_read_LEB128_correctly() {
        leb128TestValues.assertForEach { (expectedULong, bytes) ->
            val actualULong = Buffer()
                .apply { write(bytes.toByteArray()) }
                .readLEB128(10)

            assertEquals(expectedULong, actualULong, "Read ${bytes.toBinary().contentToString()} incorrectly")
        }
    }

    private val zigZagTestValues: List<Pair<Long, ULong>> = listOf(
        0L to 0uL,
        -1L to 1uL,
        1L to 2uL,
        -2L to 3uL,
        2147483647L to 4294967294uL,
        -2147483648L to 4294967295uL,
        Long.MAX_VALUE to ULong.MAX_VALUE - 1uL,
        Long.MIN_VALUE to ULong.MAX_VALUE
    )

    @Test
    fun Should_ZigZag_encode_correctly() {
        zigZagTestValues.assertForEach { (long, ulong) ->
            assertEquals(ulong, long.zigZagEncode(), "Should ZigZag encode $long to $ulong")
        }
    }

    @Test
    fun Should_ZigZag_decode_correctly() {
        zigZagTestValues.assertForEach { (long, ulong) ->
            assertEquals(long, ulong.zigZagDecode(), "Should ZigZag decode $ulong to $long")
        }
    }
}
