package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NbtFormatConfigurationTest {
    @Serializable
    @SerialName("")
    private data class TestData(
        val a: Int,
        val b: Int,
    )

    @Test
    fun Should_ignore_unknown_key_at_beginning() = parameterizeTest {
        val nbt = parameterizedNbtFormat { ignoreUnknownKeys = true }

        val tag = buildNbtCompound("") {
            put("unknown", "value")
            put("a", 1)
            put("b", 2)
        }

        val expected = TestData(1, 2)

        assertEquals(expected, nbt.decodeFromNbtTag(TestData.serializer(), tag))
    }

    @Test
    fun Should_ignore_unknown_key_at_middle() = parameterizeTest {
        val nbt = parameterizedNbtFormat { ignoreUnknownKeys = true }

        val tag = buildNbtCompound("") {
            put("a", 1)
            put("unknown", "value")
            put("b", 2)
        }

        val expected = TestData(1, 2)

        assertEquals(expected, nbt.decodeFromNbtTag(TestData.serializer(), tag))
    }

    @Test
    fun Should_ignore_unknown_key_at_end() = parameterizeTest {
        val nbt = parameterizedNbtFormat { ignoreUnknownKeys = true }

        val tag = buildNbtCompound("") {
            put("a", 1)
            put("b", 2)
            put("unknown", "value")
        }

        val expected = TestData(1, 2)

        assertEquals(expected, nbt.decodeFromNbtTag(TestData.serializer(), tag))
    }

    @Test
    fun Should_throw_for_unknown_keys_if_not_permitted() = parameterizeTest {
        val nbt = parameterizedNbtFormat { ignoreUnknownKeys = false }

        val tag = buildNbtCompound("") {
            put("a", 1)
            put("b", 2)
            put("unknown", "value")
        }

        assertFailsWith<SerializationException> {
            nbt.decodeFromNbtTag(TestData.serializer(), tag)
        }
    }

    @Test
    fun class_discriminator_default_should_be_the_same_as_JSON() = parameterizeTest {
        var actualDefault: String? = null

        parameterizedNbtFormat {
            actualDefault = classDiscriminator
        }

        assertEquals(Json.configuration.classDiscriminator, actualDefault)
    }

    @Test
    fun class_discriminator_should_apply_when_built() = parameterizeTest {
        val classDiscriminatorValue by parameterOf("type1", "type2")

        val nbt = parameterizedNbtFormat {
            classDiscriminator = classDiscriminatorValue
        }

        assertEquals(classDiscriminatorValue, nbt.configuration.classDiscriminator)
    }

    @Test
    fun name_root_classes_default_should_be_true() = parameterizeTest {
        var actualDefault: Boolean? = null

        parameterizedNbtFormat {
            actualDefault = nameRootClasses
        }

        assertEquals(true, actualDefault)
    }

    @Test
    fun name_root_classes_should_apply_when_built() = parameterizeTest {
        val nameRootClassesValue by parameterOf(true, false)

        val nbt = parameterizedNbtFormat {
            nameRootClasses = nameRootClassesValue
        }

        assertEquals(nameRootClassesValue, nbt.configuration.nameRootClasses)
    }
}
