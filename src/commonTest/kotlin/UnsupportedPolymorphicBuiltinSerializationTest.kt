package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UnsupportedPolymorphicBuiltinSerializationTest {
    private inline fun <reified T> expectedInformativeMessage(): String {
        @OptIn(ExperimentalSerializationApi::class)
        val serialName = serializer<T>().descriptor.serialName

        return "Unable to serialize type with serial name '$serialName'. " +
                "The builtin polymorphic serializers are not yet supported."
    }


    @Serializable
    private sealed interface SealedInterface {
        @Serializable
        data class A(val a: String) : SealedInterface
    }

    @Serializable
    private abstract class PolymorphicClass {
        @Serializable
        data class Actual(val a: String) : PolymorphicClass()
    }

    private val nbt = NbtFormat(
        serializersModule = SerializersModule {
            polymorphic(PolymorphicClass::class, PolymorphicClass.Actual::class, PolymorphicClass.Actual.serializer())
        }
    )

    @Test
    fun encoding_with_the_builtin_sealed_class_serializer_should_fail_with_an_informative_unsupported_error() {
        val failure = assertFailsWith<UnsupportedOperationException> {
            nbt.encodeToNbtTag<SealedInterface>(SealedInterface.A(""))
        }

        assertEquals(expectedInformativeMessage<SealedInterface>(), failure.message)
    }

    @Test
    fun decoding_with_the_builtin_sealed_class_serializer_should_fail_with_an_informative_unsupported_error() {
        val failure = assertFailsWith<UnsupportedOperationException> {
            nbt.decodeFromNbtTag<SealedInterface>(
                buildNbtCompound { put("a", "") }
            )
        }

        assertEquals(expectedInformativeMessage<SealedInterface>(), failure.message)
    }

    @Test
    fun encoding_with_the_builtin_polymorphic_serializer_should_fail_with_an_informative_unsupported_error() {
        val failure = assertFailsWith<UnsupportedOperationException> {
            nbt.encodeToNbtTag<PolymorphicClass>(
                PolymorphicClass.Actual("")
            )
        }

        assertEquals(expectedInformativeMessage<PolymorphicClass>(), failure.message)
    }

    @Test
    fun decoding_with_the_builtin_polymorphic_serializer_should_fail_with_an_informative_unsupported_error() {
        val failure = assertFailsWith<UnsupportedOperationException> {
            nbt.decodeFromNbtTag<PolymorphicClass>(
                buildNbtCompound { put("a", "") }
            )
        }

        assertEquals(expectedInformativeMessage<PolymorphicClass>(), failure.message)
    }
}
