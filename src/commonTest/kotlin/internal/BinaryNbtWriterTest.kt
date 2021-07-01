@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import kotlinx.serialization.KSerializer
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.file.nbtFiles
import net.benwoodworth.knbt.tag.NbtTag
import okio.blackholeSink
import okio.buffer
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

@OptIn(OkioApi::class, ExperimentalNbtApi::class)
class BinaryNbtWriterTest {
    val nbt = Nbt {
        variant = NbtVariant.BigEndian
        compression = NbtCompression.None
    }

    @Test
    fun Should_encode_from_class_correctly() {
        nbtFiles.assertForEach { file ->
            @Suppress("UNCHECKED_CAST")
            val out = file.nbt.encodeToByteArray(file.valueSerializer as KSerializer<Any>, file.value)

            val outCompression = try {
                NbtCompression.detect(out.asSource().buffer())
            } catch (t: Throwable) {
                throw Exception("Unable to check compression type", t)
            }

            assertEquals(
                file.nbt.configuration.compression,
                outCompression,
                "Encoded with wrong compression: ${file.description}",
            )

            val tag = try {
                file.nbt.decodeFromByteArray(NbtTag.serializer(), out)
            } catch (t: Throwable) {
                throw Exception("Unable to decode compressed value", t)
            }

            assertEquals(file.nbtTag, tag, "Unable to decode encoded data correctly: ${file.description}")
        }
    }

    @Test
    fun Should_encode_from_NbtTag_correctly() {
        nbtFiles.assertForEach { file ->
            val out = file.nbt.encodeToByteArray(NbtTag.serializer(), file.nbtTag)

            val outCompression = try {
                NbtCompression.detect(out.asSource().buffer())
            } catch (t: Throwable) {
                throw Exception("Unable to check compression type", t)
            }

            assertEquals(
                file.nbt.configuration.compression,
                outCompression,
                "Encoded with wrong compression: ${file.description}",
            )

            val tag = try {
                file.nbt.decodeFromByteArray(NbtTag.serializer(), out)
            } catch (t: Throwable) {
                throw Exception("Unable to decode compressed value", t)
            }

            assertEquals(file.nbtTag, tag, "Unable to decode encoded data correctly: ${file.description}")
        }
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
    fun Should_not_close_sink() {
        nbtFiles.assertForEach { file ->
            TestSink(blackholeSink()).use { sink ->
                @Suppress("UNCHECKED_CAST")
                file.nbt.encodeTo(sink, file.valueSerializer as KSerializer<Any>, file.value)
                assertFalse(sink.isClosed, "Sink closed while decoding ${file.description}")
            }
        }
    }
}
