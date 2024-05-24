package net.benwoodworth.knbt.internal

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

// Regression tests when changing how NBT exception paths were captured
class NbtPathTest {
    private inline fun <reified T : NbtException> assertFailsWithPathMessage(
        path: String,
        block: () -> Unit,
    ): NbtException {
        val exception = assertFailsWith<T> { block() }
        assertTrue(
            actual = exception.message?.contains("'$path'") == true,
            message = "Expected error message to contain NBT path '$path', but was \"${exception.message}\""
        )
        return exception
    }

    @Test
    @Ignore // Fix during encoder/decoder refactor: https://github.com/BenWoodworth/knbt/issues/30
    fun decoding_incorrect_root() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt()

        assertFailsWithPathMessage<NbtDecodingException>("{root}") {
            nbt.verifyDecoder(String.serializer(), NbtInt(7))
        }
    }

    @Test
    @Ignore // Wasn't working before refactor
    fun decoding_missing_compound_entry() = parameterizeTest {
        @Serializable
        @NbtNamed("root")
        class MyClass(val entry: Int)

        val nbt by parameterOfDecoderVerifyingNbt()

        assertFailsWithPathMessage<NbtDecodingException>("root.entry") {
            nbt.verifyDecoder(
                MyClass.serializer(),
                buildNbtCompound("root") { }
            )
        }
    }

    @Test
    @Ignore // Fix during encoder/decoder refactor: https://github.com/BenWoodworth/knbt/issues/30
    fun decoding_incorrect_compound_entry() = parameterizeTest {
        @Serializable
        @NbtNamed("root")
        class MyClass(val entry: Int)

        val nbt by parameterOfDecoderVerifyingNbt()

        assertFailsWithPathMessage<NbtDecodingException>("root.entry") {
            nbt.verifyDecoder(
                MyClass.serializer(),
                buildNbtCompound("root") {
                    put("entry", "string!")
                }
            )
        }
    }

    @Test
    @Ignore // Fix during encoder/decoder refactor: https://github.com/BenWoodworth/knbt/issues/30
    fun decoding_incorrect_list_type() = parameterizeTest {
        @Serializable
        @NbtNamed("root")
        class MyClass(val entry: List<Int>)

        val nbt by parameterOfDecoderVerifyingNbt()

        assertFailsWithPathMessage<NbtDecodingException>("root.entry[0]") {
            nbt.verifyDecoder(
                MyClass.serializer(),
                buildNbtCompound("root") {
                    putNbtList<NbtString>("entry") { add("string!") }
                }
            )
        }
    }
}
