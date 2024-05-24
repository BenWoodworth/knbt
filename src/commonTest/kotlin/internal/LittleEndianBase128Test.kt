package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.reportedAs
import okio.Buffer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

@OptIn(ExperimentalUnsignedTypes::class)
class LittleEndianBase128Test {
    private data class LEB128Parameters(val ulong: ULong, val bytes: UByteArray)

    private val leb128TestValues = sequenceOf(
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
    fun should_write_LEB128_correctly() = parameterizeTest {
        val value by parameter(leb128TestValues)
            .reportedAs(this, "ulong") { it.ulong }

        val (ulong, bytes) = value

        val actualBytes = Buffer()
            .apply { writeLEB128(ulong) }
            .readByteArray().toUByteArray()

        assertContentEquals(bytes.toBinary(), actualBytes.toBinary())
    }

    @Test
    fun should_read_LEB128_correctly() = parameterizeTest {
        val value by parameter(leb128TestValues)
            .reportedAs(this, "bytes") { it.bytes.toBinary() }

        val (ulong, bytes) = value

        val actualULong = Buffer()
            .apply { write(bytes.toByteArray()) }
            .readLEB128(10)

        assertEquals(ulong, actualULong)
    }

    private data class ZigZagParameters(val long: Long, val zigZagULong: ULong)

    private val zigZagTestValues = sequenceOf(
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
    fun should_ZigZag_encode_correctly() = parameterizeTest {
        val value by parameter(zigZagTestValues)
            .reportedAs(this, "long") { it.long }

        val (long, zigZagULong) = value

        assertEquals(zigZagULong, long.zigZagEncode())
    }

    @Test
    fun should_ZigZag_decode_correctly() = parameterizeTest {
        val value by parameter(zigZagTestValues)
            .reportedAs(this, "long") { it.zigZagULong }

        val (long, zigZagULong) = value

        assertEquals(long, zigZagULong.zigZagDecode())
    }
}
