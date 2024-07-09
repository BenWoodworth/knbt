@file:OptIn(ExperimentalNbtApi::class)

package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.nbtName
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
import net.benwoodworth.knbt.test.parameters.parameterOfEncoderVerifyingNbt
import net.benwoodworth.knbt.test.parameters.parameterOfSerializableTypeEdgeCases
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

//    private fun ParameterizeScope.parameterOfNbtNameTypeEdgeCaseSerializers() = parameterOfSequence {
//        data class NbtNameAndTypeEdgeCaseSerializer(
//            val nbtName: String,
//            val serializableType: SerializableTypeEdgeCase
//        ) : KSerializer<Unit> {
//            override val descriptor = object : SerialDescriptor by serializableType.baseDescriptor {
//                @ExperimentalSerializationApi
//                override val serialName = "NbtNameAndTypeEdgeCase(nbtName=$nbtName, serializableType=$serializableType)"
//                override val annotations =
//                    listOf(NbtName(this@NbtNameAndTypeEdgeCaseSerializer.nbtName), NbtName.Dynamic())
//
//            }
//
//            override fun serialize(encoder: Encoder, value: Unit) {
//                TODO("Not yet implemented")
//            }
//
//            override fun deserialize(decoder: Decoder) {
//                TODO("Not yet implemented")
//            }
//        }
//
//        parameterize {
//            val serializableType by parameterOfSerializableTypeEdgeCases()
//            val nbtName by parameterOf("name", "different_name")
//
//            yield(NbtNameAndTypeEdgeCaseSerializer(nbtName, serializableType))
//        }
//    }

    @Test
    fun type_with_NBT_name_should_serialize_nested_under_its_name() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val serializableType by parameterOfSerializableTypeEdgeCases()
        val nbtName by parameterOf("name", "different_name")

        val valueSerializer = object : KSerializer<Unit> {
            @OptIn(SealedSerializationApi::class)
            override val descriptor = object : SerialDescriptor by serializableType.baseDescriptor {
                override val annotations = listOf(NbtName(nbtName))
            }

            override fun serialize(encoder: Encoder, value: Unit) =
                serializableType.encodeValue(encoder, descriptor)

            override fun deserialize(decoder: Decoder) =
                serializableType.decodeValue(decoder, descriptor)
        }

        nbt.verifyEncoderOrDecoder(
            valueSerializer,
            Unit,
            buildNbtCompound {
                put(nbtName, serializableType.valueTag)
            }
        )
    }

    @Serializable
    private data class OuterClass<T>(val value: T) // KT-69388: Ideally should be declared inside the test that uses it

    @Test
    fun should_serialize_nested_under_name_when_within_a_class() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val serializableType by parameterOfSerializableTypeEdgeCases()
        val nbtName by parameterOf("name", "different_name")

        val valueSerializer = object : KSerializer<Unit> {
            @OptIn(SealedSerializationApi::class)
            override val descriptor = object : SerialDescriptor by serializableType.baseDescriptor {
                override val annotations = listOf(NbtName(nbtName))
            }

            override fun serialize(encoder: Encoder, value: Unit) =
                serializableType.encodeValue(encoder, descriptor)

            override fun deserialize(decoder: Decoder) =
                serializableType.decodeValue(decoder, descriptor)
        }

        nbt.verifyEncoderOrDecoder(
            OuterClass.serializer(valueSerializer),
            OuterClass(Unit),
            buildNbtCompound {
                putNbtCompound("value") {
                    put(nbtName, serializableType.valueTag)
                }
            }
        )
    }

    @Test
    fun should_serialize_nested_under_name_when_within_a_list() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val serializableType by parameterOfSerializableTypeEdgeCases()
        val nbtName by parameterOf("name", "different_name")

        val valueSerializer = object : KSerializer<Unit> {
            @OptIn(SealedSerializationApi::class)
            override val descriptor = object : SerialDescriptor by serializableType.baseDescriptor {
                override val annotations = listOf(NbtName(nbtName))
            }

            override fun serialize(encoder: Encoder, value: Unit) =
                serializableType.encodeValue(encoder, descriptor)

            override fun deserialize(decoder: Decoder) =
                serializableType.decodeValue(decoder, descriptor)
        }

        nbt.verifyEncoderOrDecoder(
            ListSerializer(valueSerializer),
            listOf(Unit),
            buildNbtList {
                addNbtCompound {
                    put(nbtName, serializableType.valueTag)
                }
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
        class MyClass

        nbt.verifyDecoder(
            MyClass.serializer(),
            buildNbtCompound("different_encoded_name") {}
        )
    }

    @Test
    fun should_encode_dynamic_name_as_the_static_name_when_no_names_are_actually_encoded() = parameterizeTest {
        val nbt by parameterOfEncoderVerifyingNbt(includeNamedRootNbt = true)
        assume(nbt.capabilities.namedRoot)

        val serializableType by parameterOfSerializableTypeEdgeCases()
        val staticName by parameterOf("name", "different_name")

        class DynamicDefaultingToStaticSerializer : SerializationStrategy<Unit> {
            override val descriptor = object : SerialDescriptor by serializableType.baseDescriptor {
                @ExperimentalSerializationApi
                override val annotations = listOf(NbtName(staticName), NbtName.Dynamic())
            }

            override fun serialize(encoder: Encoder, value: Unit) {
                //encoder.asNbtEncoder().encodeNbtName(...) // Name is not encoded
                serializableType.encodeValue(encoder, descriptor)
            }
        }

        val expectedTag = buildNbtCompound {
            put(staticName, serializableType.valueTag)
        }

        nbt.verifyEncoder(DynamicDefaultingToStaticSerializer(), Unit, expectedTag)
    }

    @Test
    fun serializing_names_dynamically_should_require_the_serializer_to_be_marked_as_dynamic() {

    }

    @Test
    fun serializing_dynamic_name_should_fail_after_starting_to_serialize_the_value() = parameterizeTest {

    }

    @Test
    fun dynamic_name_should_be_encoded() = parameterizeTest {

    }

    @Test
    fun dynamic_name_should_be_the_first_name_encoded_if_another_is_encoded_later() = parameterizeTest {

    }

    @Test
    fun dynamic_name_should_be_the_first_name_encoded_if_another_is_encoded_later_from_a_delegate() = parameterizeTest {

    }

    @Test
    fun dynamic_name_encoded_from_delegate_should_be_encoded_if_it_is_the_first() = parameterizeTest {

    }

    @Test
    fun dynamic_name_decoded_from_named_root_should_be_correct() = parameterizeTest {

    }

    @Test
    fun dynamic_name_decoded_from_named_root_should_be_correct_when_decoded_from_delegate() = parameterizeTest {

    }

    @Test
    fun dynamic_name_decoded_from_unnamed_root_should_be_null() = parameterizeTest {

    }

    @Test
    fun dynamic_name_decoded_from_compound_should_be_correct() = parameterizeTest {

    }

    @Test
    fun dynamic_name_decoded_from_compound_should_be_correct_when_decoded_from_delegate() = parameterizeTest {

    }

    @Test
    fun dynamic_name_decoded_from_collection_should_be_null() = parameterizeTest {

    }
}
