@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.tag.NbtTag
import okio.use
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@OptIn(OkioApi::class)
class BinaryNbtReaderTest {
    @Test
    fun Should_decode_to_class_correctly() {
        nbtFiles.assertForEach { file ->
            assertEquals(
                expected = file.value,
                actual = file.asSource().use { source ->
                    Nbt.decodeFrom(source, file.valueSerializer)
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
                    Nbt.decodeFrom(source, NbtTag.serializer())
                },
                message = "Read NbtTag incorrectly while decoding ${file.description}"
            )
        }
    }

    @Test
    fun Should_not_read_more_from_source_than_necessary() {
        nbtFiles.assertForEach { file ->
            TestSource(file.asSource()).use { source ->
                Nbt.decodeFrom(source, NbtTag.serializer())
                assertFalse(source.readPastEnd, "Source read past end while decoding ${file.description}")
            }
        }
    }

    @Test
    fun Should_not_close_source() {
        nbtFiles.assertForEach { file ->
            TestSource(file.asSource()).use { source ->
                Nbt.decodeFrom(source, NbtTag.serializer())
                assertFalse(source.isClosed, "Source closed while decoding ${file.description}")
            }
        }
    }
}
