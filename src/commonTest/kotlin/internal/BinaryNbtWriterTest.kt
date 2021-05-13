@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import data.bigTestClass
import data.bigTestTag
import data.testClass
import data.testTag
import kotlinx.serialization.KSerializer
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.tag.NbtTag
import okio.blackholeSink
import okio.buffer
import okio.use
import kotlin.test.*

@OptIn(OkioApi::class)
class BinaryNbtWriterTest {
    @Test
    fun Should_encode_from_class_correctly() {
        nbtFiles.assertForEach { file ->
            val nbt = Nbt { compression = file.compression }

            @Suppress("UNCHECKED_CAST")
            val out = nbt.encodeToByteArray(file.valueSerializer as KSerializer<Any>, file.value)

            val outCompression = out.asSource().buffer().peekNbtCompression()
            assertEquals(file.compression, outCompression, "Encoded with wrong compression: ${file.description}")

            val tag = nbt.decodeFromByteArray(NbtTag.serializer(), out)
            assertEquals(file.nbtTag, tag, "Unable to decode encoded data correctly: ${file.description}")
        }
    }

    @Test
    fun Should_encode_from_NbtTag_correctly() {
        nbtFiles.assertForEach { file ->
            val nbt = Nbt { compression = file.compression }

            @Suppress("UNCHECKED_CAST")
            val out = nbt.encodeToByteArray(NbtTag.serializer(), file.nbtTag)

            val outCompression = out.asSource().buffer().peekNbtCompression()
            assertEquals(file.compression, outCompression, "Encoded with wrong compression: ${file.description}")

            val tag = nbt.decodeFromByteArray(NbtTag.serializer(), out)
            assertEquals(file.nbtTag, tag, "Unable to decode encoded data correctly: ${file.description}")
        }
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

    @Test
    fun Should_not_close_sink() {
        nbtFiles.assertForEach { file ->
            val nbt = Nbt { compression = file.compression }

            TestSink(blackholeSink()).use { sink ->
                @Suppress("UNCHECKED_CAST")
                nbt.encodeTo(sink, file.valueSerializer as KSerializer<Any>, file.value)
                assertFalse(sink.isClosed, "Sink closed while decoding ${file.description}")
            }
        }
    }
}
