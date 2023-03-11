package net.benwoodworth.knbt.internal

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.throwable.shouldHaveMessage
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtVariant.Java
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.test.TestSource
import net.benwoodworth.knbt.test.file.nbtFiles
import net.benwoodworth.knbt.test.mocks.VerifyingBinarySourceMock
import net.benwoodworth.knbt.test.parameterize
import okio.buffer
import okio.use
import kotlin.test.*

@OptIn(OkioApi::class)
class BinaryNbtReaderTest {
    @Test
    fun should_throw_decoding_exception_when_reading_invalid_tag_type_ID_from_compound_entry() {
        VerifyingBinarySourceMock
            .create {
                readByte() returns 0xAB.toByte() // beginCompoundEntry type
            }
            .verify { source ->
                val reader = BinaryNbtReader(source)
                reader.beginRootTag()
                reader.beginCompound()

                val error = shouldThrow<NbtDecodingException> {
                    reader.beginCompoundEntry()
                }

                error shouldHaveMessage "Unknown NBT tag type ID: 0xAB"
            }
    }

    @Test
    fun should_throw_decoding_exception_when_reading_invalid_tag_type_ID_from_list_entry_type() {
        VerifyingBinarySourceMock
            .create {
                readByte() returns NbtTagType.TAG_List.id // beginCompoundEntry type
                readString() returns "ListWithInvalidType" // beginCompoundEntry name

                readByte() returns 0xCD.toByte() // beginList type
            }
            .verify { source ->
                val reader = BinaryNbtReader(source)
                reader.beginRootTag()
                reader.beginCompound()
                reader.beginCompoundEntry()

                val error = shouldThrow<NbtDecodingException> {
                    reader.beginList()
                }

                error shouldHaveMessage "Unknown NBT tag type ID: 0xCD"
            }
    }

    @Test
    fun should_decode_to_class_correctly() = parameterize(nbtFiles) {
        assertEquals(
            expected = value,
            actual = asSource().use { source ->
                nbt.decodeFromBufferedSource(valueSerializer, source.buffer())
            },
        )
    }

    @Test
    fun should_decode_to_NbtTag_correctly() = parameterize(nbtFiles) {
        assertEquals(
            expected = nbtTag,
            actual = asSource().use { source ->
                nbt.decodeFromBufferedSource(source.buffer())
            },
        )
    }

    @Test
    fun should_not_read_more_from_source_than_necessary() = parameterize(nbtFiles) {
        TestSource(asSource()).use { source ->
            nbt.decodeFromBufferedSource<NbtTag>(source.buffer())
            assertFalse(source.readPastEnd)
        }
    }

    @Test
    fun should_not_close_source() = parameterize(nbtFiles) {
        TestSource(asSource()).use { source ->
            nbt.decodeFromBufferedSource<NbtTag>(source.buffer())
            assertFalse(source.isClosed)
        }
    }

    @Test
    fun should_fail_with_incorrect_NbtCompression_and_specify_mismatched_compressions() {
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
