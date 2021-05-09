package net.benwoodworth.knbt.internal

import data.*
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.decodeFromByteArray
import net.benwoodworth.knbt.tag.NbtTag
import org.junit.Test
import kotlin.test.assertEquals

class BinaryNbtReaderTest {
    @Test
    fun `Should decode test_nbt to class correctly`(): Unit = assertEquals(
        expected = testClass,
        actual = Nbt.decodeFromByteArray(testBytes.toByteArray()),
    )

    @Test
    fun `Should decode decompressed bigtest_nbt to class correctly`(): Unit = assertEquals(
        expected = bigTestClass,
        actual = Nbt.decodeFromByteArray(bigTestBytesDecompressed.toByteArray()),
    )

    @Test
    fun `Should decode bigtest_nbt to class correctly`(): Unit = assertEquals(
        expected = bigTestClass,
        actual = Nbt.decodeFromByteArray(bigTestBytes.toByteArray()),
    )

    @Test
    fun `Should decode test_nbt to NbtTag correctly`(): Unit = assertEquals(
        expected = testTag,
        actual = Nbt.decodeFromByteArray(NbtTag.serializer(), testBytes.toByteArray()),
    )

    @Test
    fun `Should decode decompressed bigtest_nbt to NbtTag correctly`(): Unit = assertEquals(
        expected = bigTestTag,
        actual = Nbt.decodeFromByteArray(NbtTag.serializer(), bigTestBytesDecompressed.toByteArray()),
    )

    @Test
    fun `Should decode bigtest_nbt to NbtTag correctly`(): Unit = assertEquals(
        expected = bigTestTag,
        actual = Nbt.decodeFromByteArray(NbtTag.serializer(), bigTestBytes.toByteArray()),
    )

//    @Test
//    fun `Should fail when decoding Byte`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Byte>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding Short`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Short>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding Int`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Int>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding Long`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Long>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding Float`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Float>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding Double`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Double>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding String`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<String>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding ByteArray`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<ByteArray>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding IntArray`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<IntArray>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding LongArray`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<LongArray>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should fail when decoding List`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<List<Byte>>(ByteArray(0))
//        }
//    }
}
