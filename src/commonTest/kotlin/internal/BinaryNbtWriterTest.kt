@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import data.*
import net.benwoodworth.knbt.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class BinaryNbtWriterTest {
    @Test
    fun Should_encode_test_nbt_from_class_correctly(): Unit = assertContentEquals(
        expected = testUncompressed.toByteArray(),
        actual = Nbt.encodeToByteArray(testClass),
    )

    @Test
    fun Should_encode_bigtest_nbt_from_class_correctly(): Unit = assertContentEquals(
        expected = bigtestUncompressed.toByteArray(),
        actual = Nbt.encodeToByteArray(bigTestClass),
    )

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
