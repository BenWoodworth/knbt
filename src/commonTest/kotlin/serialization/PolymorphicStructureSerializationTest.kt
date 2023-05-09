package net.benwoodworth.knbt.serialization

import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.*
import kotlinx.serialization.builtins.NothingSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.generators.cartesian
import net.benwoodworth.knbt.test.generators.emptySerialDescriptor
import net.benwoodworth.knbt.test.generators.serialKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

@OptIn(ExperimentalSerializationApi::class)
class PolymorphicStructureSerializationTest : SerializationTest() {
    private sealed interface CustomPolymorphic {
        @Serializable
        data class A(val a: String) : CustomPolymorphic

        @Serializable
        data class B(val b: Int) : CustomPolymorphic
    }

    private data class CustomPolymorphicSerializer(
        val kind: PolymorphicKind,
        val deserializeSequentially: Boolean,
        val serializeTypeDirectly: Boolean
    ) : KSerializer<CustomPolymorphic> {
        @OptIn(InternalSerializationApi::class)
        override val descriptor: SerialDescriptor =
            buildSerialDescriptor("CustomPolymorphic", kind) {
                element("type", String.serializer().descriptor)
                element("value", buildSerialDescriptor("CustomPolymorphicValue", SerialKind.CONTEXTUAL))
            }

        private val typeIndex = descriptor.getElementIndex("type")
        private val valueIndex = descriptor.getElementIndex("value")

        private fun CompositeEncoder.encodeType(type: String): Unit =
            if (serializeTypeDirectly) {
                encodeStringElement(descriptor, typeIndex, type)
            } else {
                encodeSerializableElement(descriptor, typeIndex, String.serializer(), type)
            }

        private fun CompositeDecoder.decodeType(): String =
            if (serializeTypeDirectly) {
                decodeStringElement(descriptor, typeIndex)
            } else {
                decodeSerializableElement(descriptor, typeIndex, String.serializer())
            }

        override fun serialize(encoder: Encoder, value: CustomPolymorphic): Unit =
            when (value) {
                is CustomPolymorphic.A -> encoder.encodeStructure(descriptor) {
                    encodeType("A")
                    encodeSerializableElement(descriptor, valueIndex, CustomPolymorphic.A.serializer(), value)
                }

                is CustomPolymorphic.B -> encoder.encodeStructure(descriptor) {
                    encodeType("B")
                    encodeSerializableElement(descriptor, valueIndex, CustomPolymorphic.B.serializer(), value)
                }
            }

        override fun deserialize(decoder: Decoder): CustomPolymorphic =
            if (deserializeSequentially) {
                deserializeSequentially(decoder)
            } else {
                deserializeNonsequentially(decoder)
            }

        private fun deserializeSequentially(decoder: Decoder): CustomPolymorphic =
            decoder.decodeStructure(descriptor) {
                assertTrue(decodeSequentially(), "Polymorphic structure decoder must support decoding sequentially")

                val type = decodeType()
                val deserializer = getDeserializer(type)
                decodeSerializableElement(descriptor, valueIndex, deserializer)
            }

        private fun deserializeNonsequentially(decoder: Decoder): CustomPolymorphic =
            decoder.decodeStructure(descriptor) {
                var type: String? = null
                var value: CustomPolymorphic? = null

                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        CompositeDecoder.DECODE_DONE -> break

                        typeIndex -> {
                            type = decodeType()
                        }

                        valueIndex -> {
                            requireNotNull(type) { "Cannot read polymorphic value before its type token" }
                            val deserializer = getDeserializer(type)
                            value = decodeSerializableElement(descriptor, index, deserializer)
                        }

                        else -> throw SerializationException(
                            "Invalid index in polymorphic deserialization of " +
                                    (type ?: "unknown type") +
                                    "\n Expected type($typeIndex), value($valueIndex) or DECODE_DONE(-1), but found $index"
                        )
                    }
                }

                requireNotNull(value) { "Polymorphic value has not been read for type $type" }
            }

        private fun getDeserializer(type: String): DeserializationStrategy<CustomPolymorphic> =
            when (type) {
                "A" -> CustomPolymorphic.A.serializer()
                "B" -> CustomPolymorphic.B.serializer()
                else -> error("Bad type: $type")
            }
    }


    private fun Exhaustive.Companion.customPolymorphicTestCase(serializerExhaustive: Exhaustive<KSerializer<CustomPolymorphic>>) =
        serializerExhaustive.flatMap { serializer ->
            Exhaustive.of(
                TestCase(
                    serializer = serializer,
                    value = CustomPolymorphic.A("value"),
                    nbtTag = buildNbtCompound {
                        put("type", "A")
                        put("a", "value")
                    }
                ),
                TestCase(
                    serializer = serializer,
                    value = CustomPolymorphic.B(1234),
                    nbtTag = buildNbtCompound {
                        put("type", "B")
                        put("b", 1234)
                    }
                )
            )
        }

    @Test
    fun custom_polymorphic_structure_serializer_should_serialize_as_compound_with_additional_type_key() = runTest {
        val serializerExhaustive = Exhaustive.cartesian(
            Exhaustive.serialKind<PolymorphicKind>(),
            Exhaustive.boolean(),
            Exhaustive.boolean(),
        ) { kind, deserializeSequentially, serializeTypeDirectly ->
            CustomPolymorphicSerializer(kind, deserializeSequentially, serializeTypeDirectly)
        }

        checkAll(
            Exhaustive.customPolymorphicTestCase(serializerExhaustive)
        ) { testCase ->
            defaultNbt.testSerialization(testCase)
        }
    }


    private class BadDescriptorFailingSerializer(
        override val descriptor: SerialDescriptor,
    ) : KSerializer<CustomPolymorphic> {
        override fun serialize(encoder: Encoder, value: CustomPolymorphic): Unit =
            encoder.encodeStructure(descriptor) {
                fail("Should have thrown when beginning structure")
            }

        override fun deserialize(decoder: Decoder): CustomPolymorphic =
            decoder.decodeStructure(descriptor) {
                fail("Should have thrown when beginning structure")
            }
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun should_throw_with_helpful_message_if_element_count_is_not_2() = runTest {
        val serializerWithBadElementCount = Exhaustive.cartesian(
            Exhaustive.serialKind<PolymorphicKind>(),
            Exhaustive.ints(0..10).filter { it != 2 }
        ) { kind, badElementCount ->
            BadDescriptorFailingSerializer(
                buildSerialDescriptor("customPolymorphic", kind) {
                    repeat(badElementCount) { index ->
                        element("element-$index", NothingSerializer().descriptor)
                    }
                }
            )
        }

        checkAll(
            Exhaustive.customPolymorphicTestCase(serializerWithBadElementCount)
        ) { testCase ->
            defaultNbt.testSerializationForNbtException(testCase) { failure ->
                val elements = testCase.serializer.descriptor.elementNames.toList()
                assertEquals(
                    "Expected polymorphic structure 2 elements named 'type' and 'value', but got ${elements.size}: $elements",
                    failure.message
                )
            }
        }
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun should_throw_with_helpful_message_if_element_0_is_not_named_type() = runTest {
        val serializersWithBadTypeName = Exhaustive.cartesian(
            Exhaustive.serialKind<PolymorphicKind>()
        ) { kind ->
            BadDescriptorFailingSerializer(
                buildSerialDescriptor("customPolymorphic", kind) {
                    element("badTypeName", String.serializer().descriptor)
                    element("value", buildSerialDescriptor("value", SerialKind.CONTEXTUAL))
                }
            )
        }

        checkAll(
            Exhaustive.customPolymorphicTestCase(serializersWithBadTypeName)
        ) { testCase ->
            defaultNbt.testSerializationForNbtException(testCase) { failure ->
                val elementZeroName = testCase.serializer.descriptor.getElementName(0)
                assertEquals(
                    "Expected polymorphic structure element 0 to be named 'type', but got '$elementZeroName'",
                    failure.message
                )
            }
        }
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun should_throw_with_helpful_message_if_type_element_is_not_a_string() = runTest {
        val serializersWithBadTypeKind = Exhaustive.cartesian(
            Exhaustive.serialKind<PolymorphicKind>(),
            Exhaustive.emptySerialDescriptor().filter { it.kind !is PrimitiveKind.STRING },
        ) { kind, nonStringTypeDescriptor ->
            BadDescriptorFailingSerializer(
                buildSerialDescriptor("customPolymorphic", kind) {
                    element("type", nonStringTypeDescriptor)
                    element("value", buildSerialDescriptor("value", SerialKind.CONTEXTUAL))
                }
            )
        }

        checkAll(
            Exhaustive.customPolymorphicTestCase(serializersWithBadTypeKind)
        ) { testCase ->
            defaultNbt.testSerializationForNbtException(testCase) { failure ->
                val typeKind = testCase.serializer.descriptor.getElementDescriptor(0).kind
                assertEquals(
                    "Expected polymorphic structure 'type' to have kind ${PrimitiveKind.STRING}, but got $typeKind",
                    failure.message
                )
            }
        }
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun should_throw_with_helpful_message_if_element_1_is_not_named_value() = runTest {
        val serializersWithBadValueName = Exhaustive.cartesian(
            Exhaustive.serialKind<PolymorphicKind>()
        ) { kind ->
            BadDescriptorFailingSerializer(
                buildSerialDescriptor("customPolymorphic", kind) {
                    element("type", String.serializer().descriptor)
                    element("badValueName", buildSerialDescriptor("value", SerialKind.CONTEXTUAL))
                }
            )
        }

        checkAll(
            Exhaustive.customPolymorphicTestCase(serializersWithBadValueName)
        ) { testCase ->
            defaultNbt.testSerializationForNbtException(testCase) { failure ->
                val elementZeroName = testCase.serializer.descriptor.getElementName(1)
                assertEquals(
                    "Expected polymorphic structure element 1 to be named 'value', but got '$elementZeroName'",
                    failure.message
                )
            }
        }
    }

    @Test
    @OptIn(InternalSerializationApi::class)
    fun should_throw_with_helpful_message_if_value_element_is_not_contextual() = runTest {
        val serializersWithBadValueKind = Exhaustive.cartesian(
            Exhaustive.serialKind<PolymorphicKind>(),
            Exhaustive.emptySerialDescriptor().filter { it.kind !is SerialKind.CONTEXTUAL },
        ) { kind, valueDescriptor ->
            BadDescriptorFailingSerializer(
                buildSerialDescriptor("customPolymorphic", kind) {
                    element("type", String.serializer().descriptor)
                    element("value", valueDescriptor)
                }
            )
        }

        checkAll(
            Exhaustive.customPolymorphicTestCase(serializersWithBadValueKind)
        ) { testCase ->
            defaultNbt.testSerializationForNbtException(testCase) { failure ->
                val typeKind = testCase.serializer.descriptor.getElementDescriptor(1).kind
                assertEquals(
                    "Expected polymorphic structure 'value' to have kind ${SerialKind.CONTEXTUAL}, but got $typeKind",
                    failure.message
                )
            }
        }
    }

    // value first

    // encoding NbtString 'type'

    // encoding non-NbtString 'type'

    // encoding NbtCompound 'value'

    // encoding non-NbtCompound 'value'

    // compound only
}
