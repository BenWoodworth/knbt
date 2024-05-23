package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtVariant.Java
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.test.TestSource
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.file.nbtFiles
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfNbtCompressions
import okio.buffer
import okio.use
import kotlin.test.*

@OptIn(OkioApi::class)
class BinaryNbtReaderTest {
    @Test
    fun should_decode_to_class_correctly() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        assertEquals(
            expected = nbtFile.value,
            actual = nbtFile.asSource().use { source ->
                nbtFile.nbt.decodeFromBufferedSource(nbtFile.valueSerializer, source.buffer())
            },
        )
    }

    @Test
    fun should_decode_to_NbtTag_correctly() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        assertEquals(
            expected = nbtFile.nbtTag,
            actual = nbtFile.asSource().use { source ->
                nbtFile.nbt.decodeFromBufferedSource(source.buffer())
            },
        )
    }

    @Test
    fun should_not_read_more_from_source_than_necessary() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        TestSource(nbtFile.asSource()).use { source ->
            nbtFile.nbt.decodeFromBufferedSource<NbtTag>(source.buffer())
            assertFalse(source.readPastEnd)
        }
    }

    @Test
    fun should_not_close_source() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        TestSource(nbtFile.asSource()).use { source ->
            nbtFile.nbt.decodeFromBufferedSource<NbtTag>(source.buffer())
            assertFalse(source.isClosed)
        }
    }

    @Test
    fun should_fail_with_incorrect_NbtCompression_and_specify_mismatched_compressions() = parameterizeTest {
        val data = buildNbtCompound("root") {
            put("string", "String!")
        }

        val configuredCompression by parameterOfNbtCompressions()
        val fileCompression by parameterOfNbtCompressions()
        assume(configuredCompression != fileCompression)

        val decodingNbt = Nbt {
            variant = Java
            compression = configuredCompression
        }

        val encodingNbt = Nbt(decodingNbt) {
            compression = fileCompression
        }

        val encoded = encodingNbt.encodeToByteArray<NbtTag>(data)

        val error = assertFailsWith<NbtDecodingException> {
            decodingNbt.decodeFromByteArray<NbtTag>(encoded)
        }

        val errorMessage = error.message
        assertNotNull(errorMessage)
        assertContains(
            errorMessage,
            configuredCompression.toString(),
        )
        assertContains(
            errorMessage,
            fileCompression.toString(),
        )
    }
}
