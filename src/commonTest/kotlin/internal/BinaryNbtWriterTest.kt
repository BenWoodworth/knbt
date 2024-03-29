@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.file.nbtFiles
import okio.blackholeSink
import okio.buffer
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

@OptIn(OkioApi::class)
class BinaryNbtWriterTest {
    val nbt = Nbt {
        variant = NbtVariant.Java
        compression = NbtCompression.None
    }

    @Test
    fun Should_encode_from_class_correctly() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        @Suppress("UNCHECKED_CAST")
        val out = nbt.encodeToByteArray(nbtFile.valueSerializer as KSerializer<Any>, nbtFile.value)

        val outCompression = try {
            NbtCompression.detect(out.asSource().buffer())
        } catch (t: Throwable) {
            throw Exception("Unable to check compression type", t)
        }

        assertEquals(
            nbt.configuration.compression,
            outCompression,
            "Encoded with wrong compression",
        )

        val tag = try {
            nbt.decodeFromByteArray(NbtTag.serializer(), out)
        } catch (t: Throwable) {
            throw Exception("Unable to decode compressed value", t)
        }

        assertEquals(nbtFile.nbtTag, tag, "Unable to decode encoded data correctly")
    }

    @Test
    fun Should_encode_from_NbtTag_correctly() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        val out = nbt.encodeToByteArray(NbtTag.serializer(), nbtFile.nbtTag)

        val outCompression = try {
            NbtCompression.detect(out.asSource().buffer())
        } catch (t: Throwable) {
            throw Exception("Unable to check compression type", t)
        }

        assertEquals(
            nbt.configuration.compression,
            outCompression,
            "Encoded with wrong compression",
        )

        val tag = try {
            nbt.decodeFromByteArray(NbtTag.serializer(), out)
        } catch (t: Throwable) {
            throw Exception("Unable to decode compressed value", t)
        }

        assertEquals(nbtFile.nbtTag, tag, "Unable to decode encoded data correctly")
    }

    @Test
    fun Should_fail_when_decoding_Byte() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray<Byte>(0)
        }
    }

    @Test
    fun Should_fail_when_decoding_Short() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray<Short>(0)
        }
    }

    @Test
    fun Should_fail_when_decoding_Int() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray(0)
        }
    }

    @Test
    fun Should_fail_when_decoding_Long() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray(0L)
        }
    }

    @Test
    fun Should_fail_when_decoding_Float() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray(0.0f)
        }
    }

    @Test
    fun Should_fail_when_decoding_Double() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray(0.0)
        }
    }

    @Test
    fun Should_fail_when_decoding_String() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray("string")
        }
    }

    @Test
    fun Should_fail_when_decoding_ByteArray() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray(byteArrayOf(1, 2, 3))
        }
    }

    @Test
    fun Should_fail_when_decoding_IntArray() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray(intArrayOf(1, 2, 3))
        }
    }

    @Test
    fun Should_fail_when_decoding_LongArray() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray(longArrayOf(1, 2, 3))
        }
    }

    @Test
    fun Should_fail_when_decoding_List() {
        assertFailsWith<NbtEncodingException> {
            nbt.encodeToByteArray(listOf<Byte>(1, 2, 3))
        }
    }

    @Test
    fun Should_not_close_sink() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        TestSink(blackholeSink()).use { sink ->
            @Suppress("UNCHECKED_CAST")
            nbt.encodeToSink(nbtFile.valueSerializer as KSerializer<Any>, nbtFile.value, sink)
            assertFalse(sink.isClosed, "Sink closed while decoding")
        }
    }
}
