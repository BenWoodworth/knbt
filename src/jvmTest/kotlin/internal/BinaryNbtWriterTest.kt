package net.benwoodworth.knbt.internal

import data.*
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtEncodingException
import net.benwoodworth.knbt.encodeToByteArray
import net.benwoodworth.knbt.tag.NbtTag
import org.junit.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BinaryNbtWriterTest {
    @Test
    fun `Should encode test_nbt from class correctly`(): Unit = assertContentEquals(
        expected = testBytes.toByteArray(),
        actual = Nbt.encodeToByteArray(testClass),
    )

    @Test
    fun `Should encode bigtest_nbt from class correctly`(): Unit = assertContentEquals(
        expected = bigTestBytes.toByteArray(),
        actual = Nbt.encodeToByteArray(bigTestClass),
    )

    @Test
    fun `Should encode test_nbt from NbtTag correctly`(): Unit = assertContentEquals(
        expected = testBytes.toByteArray(),
        actual = Nbt.encodeToByteArray(NbtTag.serializer(), testTag),
    )

    @Test
    fun `Should encode bigtest_nbt from NbtTag correctly`(): Unit = assertContentEquals(
        expected = bigTestBytes.toByteArray(),
        actual = Nbt.encodeToByteArray(NbtTag.serializer(), bigTestTag),
    )

    @Test
    fun `Should fail when decoding Byte`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray<Byte>(0)
        }
    }

    @Test
    fun `Should fail when decoding Short`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray<Short>(0)
        }
    }

    @Test
    fun `Should fail when decoding Int`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(0)
        }
    }

    @Test
    fun `Should fail when decoding Long`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(0L)
        }
    }

    @Test
    fun `Should fail when decoding Float`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(0.0f)
        }
    }

    @Test
    fun `Should fail when decoding Double`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(0.0)
        }
    }

    @Test
    fun `Should fail when decoding String`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray("string")
        }
    }

    @Test
    fun `Should fail when decoding ByteArray`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(byteArrayOf(1, 2, 3))
        }
    }

    @Test
    fun `Should fail when decoding IntArray`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(intArrayOf(1, 2, 3))
        }
    }

    @Test
    fun `Should fail when decoding LongArray`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(longArrayOf(1, 2, 3))
        }
    }

    @Test
    fun `Should fail when decoding List`() {
        assertFailsWith<NbtEncodingException> {
            Nbt.encodeToByteArray(listOf<Byte>(1, 2, 3))
        }
    }
}
