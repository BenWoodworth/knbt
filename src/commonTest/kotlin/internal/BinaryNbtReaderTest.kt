@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtVariant.Java
import net.benwoodworth.knbt.file.nbtFiles
import okio.use
import kotlin.test.*

@OptIn(OkioApi::class)
class BinaryNbtReaderTest {
    @Test
    fun Should_decode_to_class_correctly() = parameterize(nbtFiles) {
        assertEquals(
            expected = value,
            actual = asSource().use { source ->
                nbt.decodeFromSource(valueSerializer, source)
            },
        )
    }

    @Test
    fun Should_decode_to_NbtTag_correctly() = parameterize(nbtFiles) {
        assertEquals(
            expected = nbtTag,
            actual = asSource().use { source ->
                nbt.decodeFromSource(source)
            },
        )
    }

    @Test
    fun Should_not_read_more_from_source_than_necessary() = parameterize(nbtFiles) {
        TestSource(asSource()).use { source ->
            nbt.decodeFromSource<NbtTag>(source)
            assertFalse(source.readPastEnd)
        }
    }

    @Test
    fun Should_not_close_source() = parameterize(nbtFiles) {
        TestSource(asSource()).use { source ->
            nbt.decodeFromSource<NbtTag>(source)
            assertFalse(source.isClosed)
        }
    }

    @Test
    fun Should_fail_with_incorrect_NbtCompression_and_specify_mismatched_compressions() {
        data class Parameters(
            val configuredCompression: NbtCompression,
            val fileCompression: NbtCompression,
        )

        val data = buildNbtCompound("root") {
            put("string", "String!")
        }

        val compressions = listOf(
            NbtCompression.None,
            NbtCompression.Gzip,
            NbtCompression.Zlib,
        )

        val mismatchedCompressions = compressions
            .flatMap { a -> compressions.map { b -> Parameters(a, b) } }
            .filter { (a, b) -> a !== b }

        parameterize(mismatchedCompressions, { "Configured: $configuredCompression, File: $fileCompression" }) {
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
}
