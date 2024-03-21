@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtVariant.Java
import net.benwoodworth.knbt.file.nbtFiles
import okio.use
import kotlin.test.*

@OptIn(OkioApi::class)
class BinaryNbtReaderTest {
    @Test
    fun Should_decode_to_class_correctly() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        assertEquals(
            expected = nbtFile.value,
            actual = nbtFile.asSource().use { source ->
                nbtFile.nbt.decodeFromSource(nbtFile.valueSerializer, source)
            },
        )
    }

    @Test
    fun Should_decode_to_NbtTag_correctly() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        assertEquals(
            expected = nbtFile.nbtTag,
            actual = nbtFile.asSource().use { source ->
                nbtFile.nbt.decodeFromSource(NbtTag.serializer(), source)
            },
        )
    }

    @Test
    fun Should_not_read_more_from_source_than_necessary() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        TestSource(nbtFile.asSource()).use { source ->
            nbtFile.nbt.decodeFromSource(NbtTag.serializer(), source)
            assertFalse(source.readPastEnd)
        }
    }

    @Test
    fun Should_not_close_source() = parameterizeTest {
        val nbtFile by parameter(nbtFiles)

        TestSource(nbtFile.asSource()).use { source ->
            nbtFile.nbt.decodeFromSource(NbtTag.serializer(), source)
            assertFalse(source.isClosed)
        }
    }

    @Test
    fun Should_fail_with_incorrect_NbtCompression_and_specify_mismatched_compressions() = parameterizeTest {
        val data = buildNbtCompound("root") {
            put("string", "String!")
        }

        val compressions = setOf(
            NbtCompression.None,
            NbtCompression.Gzip,
            NbtCompression.Zlib,
        )

        val configuredCompression by parameter(compressions)
        val fileCompression by parameter {
            compressions - configuredCompression
        }

        val decodingNbt = Nbt {
            variant = Java
            compression = configuredCompression
        }

        val encodingNbt = Nbt(decodingNbt) {
            compression = fileCompression
        }

        val encoded = encodingNbt.encodeToByteArray(NbtTag.serializer(), data)

        val error = assertFailsWith<NbtDecodingException> {
            decodingNbt.decodeFromByteArray(NbtTag.serializer(), encoded)
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
