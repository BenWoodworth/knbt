package net.benwoodworth.knbt.internal

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// Regression tests when changing how NBT exception paths were captured
class NbtPathTest {
    private val nbt = NbtFormat()

    private inline fun <reified T : NbtException> assertFailsWithPathMessage(
        path: String,
        block: () -> Unit,
    ): NbtException {
        val exception = assertFailsWith<T> { block() }
        assertTrue(
            actual = exception.message?.contains("'$path'") ?: false,
            message = "Expected error message to contain NBT path '$path', but was \"${exception.message}\""
        )
        return exception
    }

    @Test
    @Ignore // Fix during encoder/decoder refactor: https://github.com/BenWoodworth/knbt/issues/30
    fun decoding_incorrect_root() {
        assertFailsWithPathMessage<NbtDecodingException>("{root}") {
            nbt.decodeFromNbtTag<String>(NbtInt(7))
        }
    }

    @Test
    @Ignore // Wasn't working before refactor
    fun decoding_missing_compound_entry() {
        @Serializable
        @NbtNamed("root")
        class MyClass(val entry: Int)

        assertFailsWithPathMessage<NbtDecodingException>("root.entry") {
            nbt.decodeFromNbtTag<MyClass>(
                buildNbtCompound("root") { }
            )
        }
    }

    @Test
    @Ignore // Fix during encoder/decoder refactor: https://github.com/BenWoodworth/knbt/issues/30
    fun decoding_incorrect_compound_entry() {
        @Serializable
        @NbtNamed("root")
        class MyClass(val entry: Int)

        assertFailsWithPathMessage<NbtDecodingException>("root.entry") {
            nbt.decodeFromNbtTag<MyClass>(
                buildNbtCompound("root") {
                    put("entry", "string!")
                }
            )
        }
    }

    @Test
    @Ignore // Fix during encoder/decoder refactor: https://github.com/BenWoodworth/knbt/issues/30
    fun decoding_incorrect_list_type() {
        @Serializable
        @NbtNamed("root")
        class MyClass(val entry: List<Int>)

        assertFailsWithPathMessage<NbtDecodingException>("root.entry[0]") {
            nbt.decodeFromNbtTag<MyClass>(
                buildNbtCompound("root") {
                    putNbtList<NbtString>("entry") { add("string!") }
                }
            )
        }
    }
}
