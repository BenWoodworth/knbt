package net.benwoodworth.knbt

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.NbtException
import net.benwoodworth.knbt.internal.nbtName
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.SerializableTypeEdgeCase
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
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

    @OptIn(ExperimentalSerializationApi::class)
    private fun dynamicDelegationRequirementMessage(serializer: SerialDescriptor, delegate: SerialDescriptor): String {
        return "$dynamicAnnotation is required when delegating to a dynamically named serializer, but " +
                "'${serializer.serialName}' delegates to '${delegate.serialName}' without it."
    }

    @OptIn(ExperimentalSerializationApi::class, ExperimentalNbtApi::class)
    private class DelegatingSerializer(
        val serializableType: SerializableTypeEdgeCase,
        val isDynamic: Boolean,
        val delegate: DelegatingSerializer? = null
    ) : KSerializer<Unit> {
        override val descriptor: SerialDescriptor = object : SerialDescriptor by serializableType.baseDescriptor {
            override val serialName = run {
                val type = if (isDynamic) "Dynamic" else "Static"
                val delegation = if (delegate == null) "" else " -> ${delegate.descriptor.serialName}"

                type + delegation
            }

            override val annotations = listOfNotNull(NbtName.Dynamic().takeIf { isDynamic })
        }

        override fun toString(): String = "${this::class.simpleName}<${descriptor.serialName}>"

        override fun serialize(encoder: Encoder, value: Unit) = when (delegate) {
            null -> serializableType.encodeValue(encoder, descriptor)
            else -> encoder.encodeSerializableValue(delegate, value)
        }

        override fun deserialize(decoder: Decoder) = when (delegate) {
            null -> serializableType.decodeValue(decoder, descriptor)
            else -> decoder.decodeSerializableValue(delegate)
        }
    }

    private fun ParameterizeScope.parameterOfDelegationCombinations(
        serializableType: SerializableTypeEdgeCase,
        maxSerializerDelegations: Int
    ) = parameter {
        fun serializersThatDelegateTo(delegate: DelegatingSerializer?) = sequence {
            yield(DelegatingSerializer(serializableType, isDynamic = true, delegate))
            yield(DelegatingSerializer(serializableType, isDynamic = false, delegate))
        }

        tailrec fun nestSerializer(
            delegations: Int,
            serializer: Sequence<DelegatingSerializer>
        ): Sequence<DelegatingSerializer> = when {
            delegations == 0 -> serializer
            else -> nestSerializer(delegations - 1, serializer.flatMap { serializersThatDelegateTo(it) })
        }

        sequence {
            for (delegationCount in 0..maxSerializerDelegations) {
                yieldAll(nestSerializer(delegationCount, serializersThatDelegateTo(null)))
            }
        }
    }

    /**
     * Delegating to a [NbtName.Dynamic] serializer requires the delegating serializer to also be [NbtName.Dynamic], and
     * if not, the delegation is considered invalid.
     */
    private val DelegatingSerializer.isDelegateInvalidWithDynamic: Boolean
        get() = delegate != null && !isDynamic && delegate.isDynamic

    private fun DelegatingSerializer.hasInvalidDelegationWithDynamic(): Boolean =
        isDelegateInvalidWithDynamic || (delegate != null && delegate.hasInvalidDelegationWithDynamic())

    @Test
    fun serializer_without_invalid_delegation_should_not_fail() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        assume(nbt.capabilities.namedRoot)

        val serializableType by parameterOfSerializableTypeEdgeCases()

        val serializer by parameterOfDelegationCombinations(serializableType, 3)
        assume(!serializer.hasInvalidDelegationWithDynamic())

        if (nbt.toString() != "Decode NbtTag" ||
            serializableType.name != "Collection (non-sequentially)" ||
            serializer.toString() != "DelegatingSerializer<Static>"
        ) {
            val skip by parameterOf<Unit>() // TODO Remove
        }

        // Should not fail
        nbt.verifyEncoderOrDecoder(serializer, Unit, serializableType.valueTag)
    }

    @Test
    fun serializer_with_invalid_delegation_should_fail() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        assume(nbt.capabilities.namedRoot)

        val serializableType by parameterOfSerializableTypeEdgeCases()

        val serializer by parameterOfDelegationCombinations(serializableType, 3)
        assume(serializer.hasInvalidDelegationWithDynamic())

        val failure = assertFailsWith<NbtException> {
            nbt.verifyEncoderOrDecoder(serializer, Unit, serializableType.valueTag)
        }

        val expectedMessage = run {
            tailrec fun DelegatingSerializer.getFirstInvalidDelegation(): DelegatingSerializer =
                if (isDelegateInvalidWithDynamic) this else delegate!!.getFirstInvalidDelegation()

            serializer.getFirstInvalidDelegation()
                .let { dynamicDelegationRequirementMessage(it.descriptor, it.delegate!!.descriptor) }
        }

        assertEquals(expectedMessage, failure.message, "failure.message")
    }
}
