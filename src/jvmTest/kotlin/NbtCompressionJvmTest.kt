package net.benwoodworth.knbt

import net.benwoodworth.knbt.file.NbtTestFile
import net.benwoodworth.knbt.file.nbtFiles
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals

@OptIn(ExperimentalNbtApi::class)
class NbtCompressionJvmTest {
    @Test
    fun Should_correctly_detect_compression_from_InputStream() {
        fun testStream(stream: InputStream, expected: NbtCompression) {
            val bytes = stream.readBytes()

            bytes.inputStream().use { byteStream ->
                val actual = NbtCompression.detect(byteStream)
                assert(actual == expected) { "Expected $expected, but was $actual" }

                val readBytes = byteStream.readBytes()
                assertContentEquals(bytes, readBytes, "Detecting compression should not advance the stream")
            }
        }

        fun testResource(resource: String, expected: NbtCompression): Unit =
            testStream(this::class.java.getResourceAsStream(resource)!!, expected)

        fun testFile(file: NbtTestFile<*>): Unit =
            testStream(file.toByteArray().inputStream(), file.nbt.configuration.compression)

        testResource("/uncompressed-nbt/bedrock/level-headerless-0.nbt", NbtCompression.None)
        testResource("/uncompressed-nbt/bedrock/level-headerless-1.nbt", NbtCompression.None)
        testResource("/uncompressed-nbt/java/level-0.nbt", NbtCompression.None)
        testResource("/uncompressed-nbt/java/level-1.nbt", NbtCompression.None)
        testResource("/uncompressed-nbt/java/map-0.nbt", NbtCompression.None)
        testResource("/uncompressed-nbt/java/map-1.nbt", NbtCompression.None)
        testResource("/uncompressed-nbt/java/map-2.nbt", NbtCompression.None)
        testResource("/uncompressed-nbt/java/player.nbt", NbtCompression.None)
        testResource("/uncompressed-nbt/java/raids.nbt", NbtCompression.None)

        nbtFiles.forEach(::testFile)
    }
}
