@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import data.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.tag.NbtTag
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(OkioApi::class)
class BinaryNbtReaderTest {
    @Test
    fun Should_decode_test_nbt_to_class_correctly(): Unit = assertEquals(
        expected = testClass,
        actual = Nbt.decodeFrom(testUncompressed.asSource()),
    )

    @Test
    fun Should_decode_decompressed_bigtest_nbt_to_class_correctly(): Unit = assertEquals(
        expected = bigTestClass,
        actual = Nbt.decodeFrom(bigtestUncompressed.asSource()),
    )

    @Test
    fun Should_decode_gzip_bigtest_nbt_to_class_correctly(): Unit = assertEquals(
        expected = bigTestClass,
        actual = Nbt.decodeFrom(bigtestGzip.asSource()),
    )

    @Test
    fun Should_decode_zlib_bigtest_nbt_to_class_correctly(): Unit = assertEquals(
        expected = bigTestClass,
        actual = Nbt.decodeFrom(bigtestZlib.asSource()),
    )

    @Test
    fun Should_decode_test_nbt_to_NbtTag_correctly(): Unit = assertEquals(
        expected = testTag,
        actual = Nbt.decodeFrom(testUncompressed.asSource(), NbtTag.serializer()),
    )

    @Test
    fun Should_decode_decompressed_bigtest_nbt_to_NbtTag_correctly(): Unit = assertEquals(
        expected = bigTestTag,
        actual = Nbt.decodeFrom(bigtestUncompressed.asSource(), NbtTag.serializer()),
    )

    @Test
    fun Should_decode_gzip_bigtest_nbt_to_NbtTag_correctly(): Unit = assertEquals(
        expected = bigTestTag,
        actual = Nbt.decodeFrom(bigtestGzip.asSource(), NbtTag.serializer()),
    )

    @Test
    fun Should_decode_zlib_bigtest_nbt_to_NbtTag_correctly(): Unit = assertEquals(
        expected = bigTestTag,
        actual = Nbt.decodeFrom(bigtestZlib.asSource(), NbtTag.serializer()),
    )

//    @Test
//    fun `Should_fail_when_decoding_Byte`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Byte>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_Short`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Short>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_Int`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Int>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_Long`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Long>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_Float`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Float>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_Double`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<Double>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_String`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<String>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_ByteArray`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<ByteArray>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_IntArray`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<IntArray>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_LongArray`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<LongArray>(ByteArray(0))
//        }
//    }

//    @Test
//    fun `Should_fail_when_decoding_List`() {
//        assertFailsWith<NbtDecodingException> {
//            Nbt.decodeFromByteArray<List<Byte>>(ByteArray(0))
//        }
//    }
}
