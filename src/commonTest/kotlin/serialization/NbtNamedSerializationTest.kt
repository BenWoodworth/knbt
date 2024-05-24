package net.benwoodworth.knbt.serialization

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import net.benwoodworth.knbt.NbtNamed
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.buildNbtList
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtNamedSerializationTest {
    @Serializable
    @NbtNamed("root-name")
    private data class TestNbtClass(
        val string: String,
        val int: Int,
    )

    private val testNbt = TestNbtClass(
        string = "string",
        int = 42,
    )

    private val testNbtTag = buildNbtCompound("root-name") {
        put("string", "string")
        put("int", 42)
    }

    @Test
    fun should_serialize_nested_under_name() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            TestNbtClass.serializer(),
            testNbt,
            testNbtTag,
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_nested_under_name_when_within_a_class() = parameterizeTest {
        @Serializable
        data class OuterClass(val testNbtClass: TestNbtClass)

        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            OuterClass.serializer(),
            OuterClass(testNbt),
            buildNbtCompound {
                put("testNbtClass", testNbtTag)
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }

    @Test
    fun should_serialize_nested_under_name_when_within_a_list() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            ListSerializer(TestNbtClass.serializer()),
            listOf(testNbt),
            buildNbtList {
                add(testNbtTag)
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }
}
