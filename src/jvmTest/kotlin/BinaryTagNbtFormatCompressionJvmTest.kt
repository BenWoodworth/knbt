package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.test.file.NbtTestFile
import net.benwoodworth.knbt.test.file.nbtFiles
import net.benwoodworth.knbt.test.parameterizeTest
import java.io.InputStream
import kotlin.test.Test
import kotlin.test.assertEquals

class BinaryNbtFormatCompressionJvmTest {
    private interface StreamTestCase {
        val compression: NbtCompression
        fun inputStream(): InputStream
    }

    private val streamTestCases = sequence {
        class FromResource(val resource: String, override val compression: NbtCompression) : StreamTestCase {
            override fun inputStream() = this::class.java.getResourceAsStream(resource)
            override fun toString(): String = "From resource: $resource"
        }

        yield(FromResource("/uncompressed-nbt/bedrock/level-headerless-0.nbt", NbtCompression.None))
        yield(FromResource("/uncompressed-nbt/bedrock/level-headerless-1.nbt", NbtCompression.None))
        yield(FromResource("/uncompressed-nbt/java/level-0.nbt", NbtCompression.None))
        yield(FromResource("/uncompressed-nbt/java/level-1.nbt", NbtCompression.None))
        yield(FromResource("/uncompressed-nbt/java/map-0.nbt", NbtCompression.None))
        yield(FromResource("/uncompressed-nbt/java/map-1.nbt", NbtCompression.None))
        yield(FromResource("/uncompressed-nbt/java/map-2.nbt", NbtCompression.None))
        yield(FromResource("/uncompressed-nbt/java/player.nbt", NbtCompression.None))
        yield(FromResource("/uncompressed-nbt/java/raids.nbt", NbtCompression.None))


        class FromFile(val file: NbtTestFile<*>) : StreamTestCase {
            override val compression = file.nbt.configuration.compression
            override fun inputStream() = file.toByteArray().inputStream()
            override fun toString(): String = "From file: ${file.description}"
        }

        yieldAll(nbtFiles.asSequence().map(::FromFile))
    }

    @Test
    fun should_correctly_detect_compression_from_InputStream() = parameterizeTest {
        val stream by parameter(streamTestCases)

        stream.inputStream().use { inputStream ->
            val actual = NbtCompression.detect(inputStream)
            assertEquals(stream.compression, actual)
        }
    }

    @Test
    fun detecting_compression_should_not_advance_the_stream() = parameterizeTest {
        val stream by parameter(streamTestCases)

        val expectedRemainingBytes = stream.inputStream().use { it.readBytes().size }

        val remainingBytes = stream.inputStream().use { inputStream ->
            NbtCompression.detect(inputStream)
            inputStream.readBytes().size
        }

        assertEquals(expectedRemainingBytes, remainingBytes, "Remaining bytes")
    }
}
