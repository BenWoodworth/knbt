package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.*
import net.benwoodworth.knbt.test.qualifiedNameOrDefault
import kotlin.test.*

class NbtNamedSerializerTest {
    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun descriptor_serial_name_should_be_the_NbtNamed_class_name_with_the_value_descriptor_name() = parameterizeTest {
        val valueType by parameterOfSerializableTypeEdgeCases()

        val serializer = NbtNamed.serializer(valueType.serializer())

        val qualifiedNbtNamed = NbtNamed::class.qualifiedNameOrDefault("net.benwoodworth.knbt.NbtNamed")!!

        assertEquals(
            "$qualifiedNbtNamed<${valueType.baseDescriptor.serialName}>",
            serializer.descriptor.serialName
        )
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun descriptor_to_string_should_be_its_serial_name() = parameterizeTest {
        val valueType by parameterOfSerializableTypeEdgeCases()
        val serializer = NbtNamed.serializer(valueType.serializer())

        assertEquals(
            serializer.descriptor.serialName,
            serializer.descriptor.toString()
        )
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun descriptor_equals_should_be_true_if_constructed_the_same() = parameterizeTest {
        val valueType by parameterOfSerializableTypeEdgeCases()

        val serializer = NbtNamed.serializer(valueType.serializer())
        val other = NbtNamed.serializer(valueType.serializer())

        assertTrue(serializer.descriptor == other.descriptor, "equals")
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun descriptor_equals_should_be_false_if_value_serializer_is_unequal() = parameterizeTest {
        val valueType by parameterOfSerializableTypeEdgeCases()
        val otherValueType by parameterOfSerializableTypeEdgeCases()
        assume(valueType.baseDescriptor != otherValueType.baseDescriptor)

        val serializer = NbtNamed.serializer(valueType.serializer())
        val other = NbtNamed.serializer(otherValueType.serializer())

        assertFalse(serializer.descriptor == other.descriptor, "equals")
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun descriptor_hash_code_should_equal_if_constructed_the_same() = parameterizeTest {
        val valueType by parameterOfSerializableTypeEdgeCases()

        val serializer = NbtNamed.serializer(valueType.serializer())
        val other = NbtNamed.serializer(valueType.serializer())

        assertEquals(serializer.descriptor.hashCode(), other.descriptor.hashCode(), "hashCode")
    }

    @Test
    @OptIn(ExperimentalSerializationApi::class)
    fun descriptor_hash_code_should_be_different_from_common_descriptors() = parameterizeTest {
        val descriptor by parameter {
            this@parameterizeTest.parameterOfSerializableTypeEdgeCases().arguments
                .map { NbtNamed.serializer(it.serializer()).descriptor }
        }

        val differentDescriptor by parameter {
            val types = this@parameterizeTest.parameterOfSerializableTypeEdgeCases().arguments.map { it.serializer() }
            val namedTypes = types.map { NbtNamed.serializer(it) }

            (types + namedTypes)
                .map { it.descriptor }
                .filter { it != descriptor }
        }

        assertNotEquals(descriptor.hashCode(), differentDescriptor.hashCode(), "hashCode")
    }

    @Test
    fun descriptor_annotations_should_be_the_value_annotations() = parameterizeTest {
        val valueType by parameterOfSerializableTypeEdgeCases()

        data class CustomAnnotation(val foo: String) : Annotation

        @OptIn(SealedSerializationApi::class)
        val valueTypeWithExtraAnnotations = object : SerialDescriptor by valueType.baseDescriptor {
            override val annotations: List<Annotation> =
                valueType.baseDescriptor.annotations + CustomAnnotation("bar")
        }

        val serializer = NbtNamed.serializer(valueType.serializer(valueTypeWithExtraAnnotations))

        val containsAnnotation by parameter(valueTypeWithExtraAnnotations.annotations)
        assertContains(serializer.descriptor.annotations, containsAnnotation)
    }

    @Test
    @OptIn(ExperimentalNbtApi::class)
    fun descriptor_annotations_should_not_contain_multiple_Dynamic_annotations() = parameterizeTest {
        val valueType by parameterOfSerializableTypeEdgeCases()

        @OptIn(SealedSerializationApi::class)
        val dynamicValueType = object : SerialDescriptor by valueType.baseDescriptor {
            override val annotations: List<Annotation> =
                valueType.baseDescriptor.annotations + NbtName.Dynamic()
        }

        val serializer = NbtNamed.serializer(valueType.serializer(dynamicValueType))

        val dynamicAnnotationCount = serializer.descriptor.annotations
            .count { it is NbtName.Dynamic }

        assertEquals(1, dynamicAnnotationCount, "@NbtName.Dynamic count in descriptor")
    }

    @Test
    fun should_serialize_NbtNamed_properly() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt(includeNamedRootNbt = true)
        val serializableType by parameterOfSerializableTypeEdgeCases()
        val name by parameterOf("name", "different_name")

        nbt.verifyEncoderOrDecoder(
            NbtNamed.serializer(serializableType.serializer()),
            NbtNamed(name, Unit),
            NbtNamed(name, serializableType.valueTag)
        )
    }

    @Test
    fun encoding_nested_NbtNamed_should_use_outermost_name() = parameterizeTest {
        val nbt by parameterOfEncoderVerifyingNbt(includeNamedRootNbt = true)

        nbt.verifyEncoder(
            NbtNamed.serializer(NbtNamed.serializer(Int.serializer())),
            NbtNamed("outer", NbtNamed("inner", 0)),
            NbtNamed("outer", NbtInt(0))
        )
    }

    @Test
    fun decoding_nested_NbtNamed_should_decode_same_name() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt(includeNamedRootNbt = true)

        nbt.verifyDecoder(
            NbtNamed.serializer(NbtNamed.serializer(Int.serializer())),
            NbtNamed("dynamic_name", NbtInt(0)),
            testDecodedValue = { decoded ->
                val expected = NbtNamed("dynamic_name", NbtNamed("dynamic_name", 0))
                assertEquals(expected, decoded)
            }
        )
    }
}
