package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.nbtName
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfNbtFormats
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Suppress("ClassName")
class NbtFormatTest_NamedNbtTag {
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
    fun encode_should_return_the_NBT_name_of_the_value() = parameterizeTest {
        val nbt by parameterOfNbtFormats()
        val value by parameter(valuesWithStaticNbtNames)

        val namedNbtTag = nbt.encodeToNamedNbtTag(value.serializer, value.value)
        assertEquals(value.serializer.descriptor.nbtName, namedNbtTag.name)
    }

    @Test
    fun encode_should_return_the_same_tag_as_encoding_to_an_nbt_tag() = parameterizeTest {
        val nbt by parameterOfNbtFormats()
        val value by parameter(valuesWithStaticNbtNames)

        val nbtTag = nbt.encodeToNbtTag(value.serializer, value.value)
        val namedNbtTag = nbt.encodeToNamedNbtTag(value.serializer, value.value)

        assertEquals(nbtTag, namedNbtTag.value)
    }

    @Test
    fun decode_value_with_static_NBT_name_should_succeed_with_same_name() = parameterizeTest {
        val nbt by parameterOfNbtFormats()
        val value by parameter(valuesWithStaticNbtNames)

        val name = value.serializer.descriptor.nbtName!!
        val nbtTag = nbt.encodeToNbtTag(value.serializer, value.value)

        val namedNbtTag = NbtNamed(name, nbtTag)
        nbt.decodeFromNamedNbtTag(value.serializer, namedNbtTag)
    }

    @Test
    fun decode_value_with_static_NBT_name_should_fail_with_different_name() =
        parameterizeTest { // TODO Move to NbtNameTest?
            val nbt by parameterOfNbtFormats()
            val value by parameter(valuesWithStaticNbtNames)

            val name = value.serializer.descriptor.nbtName!!
            val nbtTag = nbt.encodeToNbtTag(value.serializer, value.value)

            val differentlyNamedNbtTag = NbtNamed("different-than-$name", nbtTag)

            val failure = assertFailsWith<NbtDecodingException> {
                nbt.decodeFromNamedNbtTag(value.serializer, differentlyNamedNbtTag)
            }

            assertEquals(
                "Expected tag named '$name', but got '${differentlyNamedNbtTag.name}'",
                failure.message,
                "failure message"
            )
        }
}
