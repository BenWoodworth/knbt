package net.benwoodworth.knbt

import java.io.IOException
import kotlin.test.Test

class MinecraftFilesTest {
    private fun testFiles(nbt: Nbt, resources: List<String>) {
        fun testFile(resource: String): Boolean {
            val stream = this::class.java.getResourceAsStream(resource)
                ?: throw IOException("Resource not found: $resource")

            val fileBytes = stream.use { it.readBytes() }

            stream.use {
                val decoded = nbt.decodeFromByteArray(NbtTag.serializer(), fileBytes)
                val encodedBytes = nbt.encodeToByteArray(NbtTag.serializer(), decoded)
                return fileBytes.contentEquals(encodedBytes)
            }
        }

        val failed = resources.filter { !testFile(it) }

        assert(failed.isEmpty()) {
            val fileList = failed.joinToString("") { "\n- $it" }
            "Files didn't re-encode identically:$fileList"
        }
    }

    @Test
    fun Encoding_Java_NBT_file_to_NbtTag_and_back_should_be_identical() {
        testFiles(
            nbt = Nbt {
                variant = NbtVariant.Java
                compression = NbtCompression.None
            },
            resources = listOf(
                "/uncompressed-nbt/java/level-0.nbt",
                "/uncompressed-nbt/java/level-1.nbt",
                "/uncompressed-nbt/java/map-0.nbt",
                "/uncompressed-nbt/java/map-1.nbt",
                "/uncompressed-nbt/java/map-2.nbt",
                "/uncompressed-nbt/java/player.nbt",
                "/uncompressed-nbt/java/raids.nbt",
            )
        )
    }

    @Test
    fun Encoding_Bedrock_NBT_file_to_NbtTag_and_back_should_be_identical() {
        testFiles(
            nbt = Nbt {
                variant = NbtVariant.Bedrock
                compression = NbtCompression.None
            },
            resources = listOf(
                "/uncompressed-nbt/bedrock/level-headerless-0.nbt",
                "/uncompressed-nbt/bedrock/level-headerless-1.nbt",
            )
        )
    }
}
