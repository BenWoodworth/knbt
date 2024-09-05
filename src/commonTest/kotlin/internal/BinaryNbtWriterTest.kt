package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.okio.detect
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import net.benwoodworth.knbt.test.TestSink
import net.benwoodworth.knbt.test.asSource
import net.benwoodworth.knbt.test.file.nbtFiles
import net.benwoodworth.knbt.test.parameterizeTest
import okio.blackholeSink
import okio.buffer
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(OkioApi::class)
class BinaryNbtWriterTest {
    val nbt = Nbt {
        variant = NbtVariant.Java
        compression = NbtCompression.None
    }

    @Test
    fun should_encode_from_class_correctly() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        @Suppress("UNCHECKED_CAST")
        val out = nbtFile.nbt.encodeToByteArray(nbtFile.valueSerializer as KSerializer<Any>, nbtFile.value)

        val outCompression = try {
            NbtCompression.detect(out.asSource().buffer())
        } catch (t: Throwable) {
            throw Exception("Unable to check compression type", t)
        }

        assertEquals(
            nbtFile.nbt.configuration.compression,
            outCompression,
            "Encoded with wrong compression",
        )

        val tag = try {
            nbtFile.nbt.decodeFromByteArray<NbtNamed<NbtTag>>(out)
        } catch (t: Throwable) {
            throw Exception("Unable to decode compressed value", t)
        }

        assertEquals(nbtFile.nbtTag, tag, "Unable to decode encoded data correctly")
    }

    @Test
    fun should_encode_from_NbtTag_correctly() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        val out = nbtFile.nbt.encodeToByteArray(nbtFile.nbtTag)

        val outCompression = try {
            NbtCompression.detect(out.asSource().buffer())
        } catch (t: Throwable) {
            throw Exception("Unable to check compression type", t)
        }

        assertEquals(
            nbtFile.nbt.configuration.compression,
            outCompression,
            "Encoded with wrong compression",
        )

        val tag = try {
            nbtFile.nbt.decodeFromByteArray<NbtNamed<NbtTag>>(out)
        } catch (t: Throwable) {
            throw Exception("Unable to decode compressed value", t)
        }

        assertEquals(nbtFile.nbtTag, tag, "Unable to decode encoded data correctly")
    }

    @Test
    fun should_not_close_sink() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        TestSink(blackholeSink()).use { sink ->
            @Suppress("UNCHECKED_CAST")
            nbt.encodeToBufferedSink(nbtFile.valueSerializer as KSerializer<Any>, nbtFile.value, sink.buffer())
            assertFalse(sink.isClosed, "Sink closed while decoding")
        }
    }
}
