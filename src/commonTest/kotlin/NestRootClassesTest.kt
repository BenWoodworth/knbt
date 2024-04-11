package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class)
class NestRootClassesTest {
    private class TestCase<T>(
        val serializer: KSerializer<T>,
        val value: T,
        val serializersModule: SerializersModule = EmptySerializersModule()
    ) {
        override fun toString(): String =
            serializer.descriptor.serialName

        fun encodeToNbtTag(nbt: NbtFormat): NbtTag =
            nbt.encodeToNbtTag(serializer, value)

        fun decodeFromNbtTag(nbt: NbtFormat, tag: NbtTag): T =
            nbt.decodeFromNbtTag(serializer, tag)
    }

    @Serializable
    private data class Class(val element: String)

    private abstract class AbstractClass {
        @Serializable
        data class Impl(val element: String) : AbstractClass()
    }

    private interface Interface {
        @Serializable
        data class Impl(val element: String) : Interface
    }

    @Serializable
    private sealed class SealedClass {
        @Serializable
        data class Impl(val element: String) : SealedClass()
    }

    @Serializable
    private sealed interface SealedInterface {
        @Serializable
        data class Impl(val element: String) : SealedInterface
    }

    private val testCases = listOf(
        TestCase(
            Class.serializer(),
            Class("value")
        ),
        TestCase(
            PolymorphicSerializer(AbstractClass::class),
            AbstractClass.Impl("value"),
            SerializersModule {
                polymorphic(AbstractClass::class, AbstractClass.Impl::class, AbstractClass.Impl.serializer())
            }
        ),
        TestCase(
            PolymorphicSerializer(Interface::class),
            Interface.Impl("value"),
            SerializersModule {
                polymorphic(Interface::class, Interface.Impl::class, Interface.Impl.serializer())
            }
        ),
        TestCase(
            SealedClass.serializer(),
            SealedClass.Impl("value")
        ),
        TestCase(
            SealedInterface.serializer(),
            SealedInterface.Impl("value")
        )
    )

    @Test
    fun class_encoded_with_nesting_be_class_without_nesting_wrapped_in_serial_name() = parameterizeTest {
        val testCase by parameter(testCases)

        val nbtWithoutNesting = parameterizedNbtFormat {
            nameRootClasses = false
            serializersModule = testCase.serializersModule
        }

        val nbtWithNesting = NbtFormat(nbtWithoutNesting) {
            nameRootClasses = true
        }

        val tagWithoutNesting = testCase.encodeToNbtTag(nbtWithoutNesting)
        val tagWithNesting = testCase.encodeToNbtTag(nbtWithNesting)

        val expectedTagWithNesting = buildNbtCompound {
            put(testCase.serializer.descriptor.serialName, tagWithoutNesting)
        }

        assertEquals(expectedTagWithNesting, tagWithNesting)
    }

    @Test
    fun should_correctly_serialize_class_with_name_root_classes_configured() = parameterizeTest {
        val testCase by parameter(testCases)

        val nameRootClasses by parameterOf(true, false)

        val nbt = parameterizedNbtFormat {
            this.nameRootClasses = nameRootClasses
            serializersModule = testCase.serializersModule
        }

        val encoded = testCase.encodeToNbtTag(nbt)
        val decoded = testCase.decodeFromNbtTag(nbt, encoded)

        assertEquals(testCase.value, decoded, "Decoded tag")
    }
}
