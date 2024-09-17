package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.NbtArray
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfBooleans
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SerialDescriptorTest {
    private class NbtListKindTestCase(val expectedKind: NbtListKind, val descriptor: SerialDescriptor) {
        @OptIn(ExperimentalSerializationApi::class)
        override fun toString(): String =
            "${descriptor.serialName} (of ${descriptor.getElementDescriptor(0).serialName})"
    }

    private fun ParameterizeScope.parameterOfNbtListKindTestCases(
        vararg testCases: Pair<SerialDescriptor, NbtListKind>
    ) = parameter {
        testCases.asSequence().map { (serialDescriptor, nbtListKind) ->
            NbtListKindTestCase(nbtListKind, serialDescriptor)
        }
    }

    @Test
    fun nbt_list_kind_should_be_correct() = parameterizeTest {
        @OptIn(ExperimentalSerializationApi::class)
        fun SerialDescriptor.withNbtArrayAnnotation(): SerialDescriptor =
            object : SerialDescriptor by this {
                override val serialName: String = "WithNbtArrayAnnotation<${this@withNbtArrayAnnotation.serialName}>"
                override val annotations: List<Annotation> = this@withNbtArrayAnnotation.annotations + NbtArray()
            }

        val testCase by parameterOfNbtListKindTestCases(
            ByteArraySerializer().descriptor to NbtListKind.List,
            IntArraySerializer().descriptor to NbtListKind.List,
            LongArraySerializer().descriptor to NbtListKind.List,

            ByteArraySerializer().descriptor.withNbtArrayAnnotation() to NbtListKind.ByteArray,
            IntArraySerializer().descriptor.withNbtArrayAnnotation() to NbtListKind.IntArray,
            LongArraySerializer().descriptor.withNbtArrayAnnotation() to NbtListKind.LongArray,

            serializer<List<Byte>>().descriptor to NbtListKind.List,
            serializer<List<Int>>().descriptor to NbtListKind.List,
            serializer<List<Long>>().descriptor to NbtListKind.List,

            serializer<List<Byte>>().descriptor.withNbtArrayAnnotation() to NbtListKind.ByteArray,
            serializer<List<Int>>().descriptor.withNbtArrayAnnotation() to NbtListKind.IntArray,
            serializer<List<Long>>().descriptor.withNbtArrayAnnotation() to NbtListKind.LongArray,
        )

        val asElement by parameterOfBooleans()

        val nbtListKind = if (asElement) {
            buildClassSerialDescriptor("Parent") {
                element("element", testCase.descriptor)
            }.getElementNbtListKind(EmptyNbtContext, 0)
        } else {
            testCase.descriptor.getNbtListKind(EmptyNbtContext)
        }

        assertEquals(testCase.expectedKind, nbtListKind)
    }

    @Test
    fun nbt_list_kind_of_NbtArray_element_should_be_correct() = parameterizeTest {
        val testCase by parameterOfNbtListKindTestCases(
            serializer<List<Byte>>().descriptor to NbtListKind.ByteArray,
            serializer<List<Int>>().descriptor to NbtListKind.IntArray,
            serializer<List<Long>>().descriptor to NbtListKind.LongArray,
        )

        val descriptor = buildClassSerialDescriptor("Parent") {
            element("element", testCase.descriptor, listOf(NbtArray()))
        }

        assertEquals(testCase.expectedKind, descriptor.getElementNbtListKind(EmptyNbtContext, 0))
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    private val nbtArrayDescriptorWithZeroElements =
        buildSerialDescriptor("ListDescriptorWithZeroElements", StructureKind.LIST) {
            annotations = listOf(NbtArray())
        }

    @Test
    fun getting_kind_for_NbtArray_descriptor_should_throw_SerializationException_if_it_has_zero_elements() {
        assertFailsWith<NbtException> {
            nbtArrayDescriptorWithZeroElements.getNbtListKind(EmptyNbtContext)
        }
    }

    @Test
    fun getting_kind_for_NbtArray_element_descriptor_should_throw_SerializationException_if_it_has_zero_elements() {
        val descriptor = buildClassSerialDescriptor("MyClass") {
            element("0", nbtArrayDescriptorWithZeroElements)
        }

        assertFailsWith<NbtException> {
            descriptor.getElementNbtListKind(EmptyNbtContext, 0)
        }
    }
}
