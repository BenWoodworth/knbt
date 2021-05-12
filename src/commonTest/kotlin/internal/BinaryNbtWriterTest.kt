@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import data.bigTestClass
import data.testClass
import net.benwoodworth.knbt.*
import okio.buffer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class BinaryNbtWriterTest {
    private val nbtGzip = Nbt { compression = NbtCompression.Gzip }
    private val nbtZlib = Nbt { compression = NbtCompression.Zlib }

    @Test
    fun Should_encode_test_nbt_from_class_correctly(): Unit = assertContentEquals(
        expected = testUncompressed.toByteArray(),
        actual = Nbt.encodeToByteArray(testClass),
    )

    @Test
    @OptIn(OkioApi::class)
    fun Should_encode_test_nbt_gzip_from_class_correctly() {
        val out = nbtGzip.encodeToByteArray(testClass)
        assertContentEquals(
            expected = testUncompressed.toByteArray(),
            actual = out.asSource().asGzipSource().buffer().readByteArray(),
        )
    }

    @Test
    @OptIn(OkioApi::class)
    fun Should_encode_test_nbt_zlib_from_class_correctly() {
        val out = nbtZlib.encodeToByteArray(testClass).asSource()
        assertContentEquals(
            expected = testUncompressed.toByteArray(),
            actual = out.asZlibSource().buffer().readByteArray(),
        )
    }

    @Test
    fun Should_encode_bigtest_nbt_from_class_correctly(): Unit = assertContentEquals(
        expected = bigtestUncompressed.toByteArray(),
        actual = Nbt.encodeToByteArray(bigTestClass),
    )

    @Test
    @OptIn(OkioApi::class)
    fun Should_encode_bigtest_nbt_gzip_from_class_correctly() {
        val out = nbtGzip.encodeToByteArray(bigTestClass).asSource()
        assertContentEquals(
            expected = bigtestUncompressed.toByteArray(),
            actual = out.asGzipSource().buffer().readByteArray(),
        )
    }

    @Test
    @OptIn(OkioApi::class)
    fun Should_encode_bigtest_nbt_zlib_from_class_correctly() {
        val out = nbtZlib.encodeToByteArray(bigTestClass).asSource()
        assertContentEquals(
            expected = bigtestUncompressed.toByteArray(),
            actual = out.asZlibSource().buffer().readByteArray(),
        )
    }

    @Test
    fun Should_fail_when_decoding_Byte() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray<Byte>(0)
        }
    }

    @Test
    fun Should_fail_when_decoding_Short() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray<Short>(0)
        }
    }

    @Test
    fun Should_fail_when_decoding_Int() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(0)
        }
    }

    @Test
    fun Should_fail_when_decoding_Long() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(0L)
        }
    }

    @Test
    fun Should_fail_when_decoding_Float() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(0.0f)
        }
    }

    @Test
    fun Should_fail_when_decoding_Double() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(0.0)
        }
    }

    @Test
    fun Should_fail_when_decoding_String() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray("string")
        }
    }

    @Test
    fun Should_fail_when_decoding_ByteArray() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(byteArrayOf(1, 2, 3))
        }
    }

    @Test
    fun Should_fail_when_decoding_IntArray() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(intArrayOf(1, 2, 3))
        }
    }

    @Test
    fun Should_fail_when_decoding_LongArray() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(longArrayOf(1, 2, 3))
        }
    }

    @Test
    fun Should_fail_when_decoding_List() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(listOf<Byte>(1, 2, 3))
        }
    }
}
