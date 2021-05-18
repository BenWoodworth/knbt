package net.benwoodworth.knbt

import net.benwoodworth.knbt.tag.NbtTag
import org.junit.Test
import java.io.IOException

class MinecraftFilesTest {
    @Test
    fun Encoding_Java_NBT_file_to_NbtTag_and_back_should_be_identical() {
        val nbt = Nbt { variant = NbtVariant.Java }

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

        val files = listOf(
            "/uncompressed-nbt/java/level-0.nbt",
            "/uncompressed-nbt/java/level-1.nbt",
            "/uncompressed-nbt/java/map-0.nbt",
            "/uncompressed-nbt/java/map-1.nbt",
            "/uncompressed-nbt/java/map-2.nbt",
            "/uncompressed-nbt/java/player.nbt",
            "/uncompressed-nbt/java/raids.nbt",
        )

        val failed = files.filter { !testFile(it) }

        assert(failed.isEmpty()) {
            val fileList = failed.joinToString("") { "\n- $it" }
            "Files didn't re-encode identically:$fileList"
        }
    }
}
