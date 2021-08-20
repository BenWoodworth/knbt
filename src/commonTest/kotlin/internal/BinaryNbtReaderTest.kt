@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtVariant.Companion.Java
import net.benwoodworth.knbt.file.nbtFiles
import okio.use
import kotlin.test.*

@OptIn(OkioApi::class)
class BinaryNbtReaderTest {
    @Test
    fun Should_decode_to_class_correctly() {
        nbtFiles.assertForEach { file ->
            assertEquals(
                expected = file.value,
                actual = file.asSource().use { source ->
                    file.nbt.decodeFromSource(file.valueSerializer, source)
                },
                message = "Read class incorrectly while decoding ${file.description}"
            )
        }
    }

    @Test
    fun Should_decode_to_NbtTag_correctly() {
        nbtFiles.assertForEach { file ->
            assertEquals(
                expected = file.nbtTag,
                actual = file.asSource().use { source ->
                    file.nbt.decodeFromSource(NbtTag.serializer(), source)
                },
                message = "Read NbtTag incorrectly while decoding ${file.description}"
            )
        }
    }

    @Test
    fun Should_not_read_more_from_source_than_necessary() {
        nbtFiles.assertForEach { file ->
            TestSource(file.asSource()).use { source ->
                file.nbt.decodeFromSource(NbtTag.serializer(), source)
                assertFalse(source.readPastEnd, "Source read past end while decoding ${file.description}")
            }
        }
    }

    @Test
    fun Should_not_close_source() {
        nbtFiles.assertForEach { file ->
            TestSource(file.asSource()).use { source ->
                file.nbt.decodeFromSource(NbtTag.serializer(), source)
                assertFalse(source.isClosed, "Source closed while decoding ${file.description}")
            }
        }
    }

    @Test
    fun Should_fail_with_incorrect_NbtCompression_and_specify_mismatched_compressions() {
        val data = buildNbtCompound("root") {
            put("string", "String!")
        }

        val compressions = listOf(
            NbtCompression.None,
            NbtCompression.Gzip,
            NbtCompression.Zlib,
        )

        fun test(configured: NbtCompression, actual: NbtCompression) {
            val decodingNbt = Nbt {
                variant = Java
                compression = configured
            }

            val encodingNbt = Nbt(decodingNbt) {
                compression = actual
            }

            val encoded = encodingNbt.encodeToByteArray(NbtTag.serializer(), data)

            val error = assertFailsWith<NbtDecodingException> {
                decodingNbt.decodeFromByteArray(NbtTag.serializer(), encoded)
            }

            val errorMessage = error.message
            assertNotNull(errorMessage)
            assertContains(
                errorMessage,
                configured.toString(),
                message = "Error message should contain configured compression name"
            )
            assertContains(
                errorMessage,
                actual.toString(),
                message = "Error message should contain actual compression name"
            )
        }

        compressions
            .flatMap { a -> compressions.map { b -> a to b } }
            .filter { (a, b) -> a !== b }
            .assertForEach { (a, b) -> test(a, b) }
    }
}
