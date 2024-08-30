package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.nbtName
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import net.benwoodworth.knbt.test.qualifiedNameOrDefault
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NbtNameTest {
    /**
     * Returns "@[NbtName.Dynamic]", which is how the annotation should be shown in error messages.
     */
    @OptIn(ExperimentalNbtApi::class)
    private val dynamicAnnotation = NbtName.Dynamic::class
        .qualifiedNameOrDefault("net.benwoodworth.knbt.NbtName.Dynamic")!!
        .let { Regex(""".*?\.(?<nestedClassName>[A-Z].*)""").matchEntire(it)!! } // From first capitalized part
        .groups["nestedClassName"]!!.value
        .let { nestedClassName -> "@$nestedClassName" }

    @Serializable
    @NbtName("root-name")
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


    private data class SerializableValueWithNbtName<T>(
        val value: T,
        val serializer: KSerializer<T>,
    )

    private val valuesWithStaticNbtNames = buildList {
        @Serializable
        @NbtName("Name")
        class Value {
            override fun toString(): String = "Value"
            override fun equals(other: Any?): Boolean = other is Value
            override fun hashCode(): Int = this::class.hashCode()
        }

        add(SerializableValueWithNbtName(Value(), Value.serializer()))


        @Serializable
        @NbtName("DifferentName")
        class DifferentValue {
            override fun toString(): String = "DifferentValue"
            override fun equals(other: Any?): Boolean = other is DifferentValue
            override fun hashCode(): Int = this::class.hashCode()
        }

        add(SerializableValueWithNbtName(DifferentValue(), DifferentValue.serializer()))
    }.let {
        @Suppress("UNCHECKED_CAST")
        it as List<SerializableValueWithNbtName<Any?>> // KT-68606: Remove cast
    }

    @Test
    fun decoding_value_with_static_NBT_name_should_fail_with_different_name() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt(includeNamedRootNbt = true)
        assume(nbt.capabilities.namedRoot)

        val value by parameter(valuesWithStaticNbtNames)
        val name = value.serializer.descriptor.nbtName!!

        val differentlyNamedNbtTag = buildNbtCompound("different_than_$name") {
            // No elements, since the decoder should fail before reaching this point anyway
        }

        val failure = assertFailsWith<NbtDecodingException> {
            nbt.verifyDecoder(value.serializer, differentlyNamedNbtTag)
        }

        assertEquals(
            "Expected tag named '$name', but got '${differentlyNamedNbtTag.content.keys.single()}'",
            failure.message,
            "failure message"
        )
    }


    @Test
    fun should_not_fail_decoding_a_different_NBT_name_when_dynamic() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt(includeNamedRootNbt = true)
        assume(nbt.capabilities.namedRoot)

        @Serializable
        @NbtName.Dynamic
        @NbtName("static_name")
        @OptIn(ExperimentalNbtApi::class)
        class MyClass

        nbt.verifyDecoder(
            MyClass.serializer(),
            buildNbtCompound("different_encoded_name") {}
        )
    }
}
