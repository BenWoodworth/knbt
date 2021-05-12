@file:Suppress("TestFunctionName")

package net.benwoodworth.knbt.internal

import data.*
import net.benwoodworth.knbt.*
import kotlin.test.Test

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
}
