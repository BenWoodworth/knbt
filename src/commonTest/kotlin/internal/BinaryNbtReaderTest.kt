@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import data.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.tag.NbtTag
import okio.use
import kotlin.test.Test
import kotlin.test.assertFalse

@OptIn(OkioApi::class)
class BinaryNbtReaderTest {
    @Test
    fun Should_decode_uncompressed_test_nbt_to_class_correctly(): Unit = assertStructureEquals(
        expected = testClass,
        actual = Nbt.decodeFrom(testUncompressed.asSource()),
    )

    @Test
    fun Should_decode_gzip_test_nbt_to_class_correctly(): Unit = assertStructureEquals(
        expected = testClass,
        actual = Nbt.decodeFrom(testGzip.asSource()),
    )

    @Test
    fun Should_decode_zlib_test_nbt_to_class_correctly(): Unit = assertStructureEquals(
        expected = testClass,
        actual = Nbt.decodeFrom(testZlib.asSource()),
    )

    @Test
    fun Should_decode_uncompressed_bigtest_nbt_to_class_correctly(): Unit = assertStructureEquals(
        expected = bigTestClass,
        actual = Nbt.decodeFrom(bigtestUncompressed.asSource()),
    )

    @Test
    fun Should_decode_gzip_bigtest_nbt_to_class_correctly(): Unit = assertStructureEquals(
        expected = bigTestClass,
        actual = Nbt.decodeFrom(bigtestGzip.asSource()),
    )

    @Test
    fun Should_decode_zlib_bigtest_nbt_to_class_correctly(): Unit = assertStructureEquals(
        expected = bigTestClass,
        actual = Nbt.decodeFrom(bigtestZlib.asSource()),
    )

    @Test
    fun Should_not_read_more_from_source_than_necessary() {
        fun test(file: NbtFile, fileName: String) {
            TestSource(file.asSource()).use { source ->
                Nbt.decodeFrom(source, NbtTag.serializer())
                assertFalse(source.readPastEnd, "Source read past end while decoding $fileName")
            }
        }

        test(testUncompressed, "test.nbt uncompressed")
        test(testGzip, "test.nbt gzip")
        test(testZlib, "test.nbt zlib")
        test(bigtestUncompressed, "bigtest.nbt uncompressed")
        test(bigtestGzip, "bigtest.nbt gzip")
        test(bigtestZlib, "bigtest.nbt zlib")
    }

    @Test
    fun Should_not_close_source() {
        fun test(file: NbtFile, fileName: String) {
            TestSource(file.asSource()).use { source ->
                Nbt.decodeFrom(source, NbtTag.serializer())
                assertFalse(source.isClosed, "Source closed while decoding $fileName")
            }
        }

        test(testUncompressed, "test.nbt uncompressed")
        test(testGzip, "test.nbt gzip")
        test(testZlib, "test.nbt zlib")
        test(bigtestUncompressed, "bigtest.nbt uncompressed")
        test(bigtestGzip, "bigtest.nbt gzip")
        test(bigtestZlib, "bigtest.nbt zlib")
    }
}
