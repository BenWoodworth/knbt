package net.benwoodworth.knbt.external

import com.benwoodworth.parameterize.ParameterizeScope
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PolymorphicSerializerTest {
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

    private fun ParameterizeScope.parameterOfConfiguredNbtFormats() = parameterOfVerifyingNbt {
        serializersModule = SerializersModule {
            polymorphic(PolymorphicClass::class, PolymorphicClass.Actual::class, PolymorphicClass.Actual.serializer())
        }
    }

    private val encodedNbtTag = buildNbtCompound { put("a", "") }

    @Test
    fun serializing_with_the_builtin_sealed_class_serializer_should_fail_with_an_informative_unsupported_error() =
        parameterizeTest {
            val nbt by parameterOfConfiguredNbtFormats()

            val failure = assertFailsWith<UnsupportedOperationException> {
                nbt.verifyEncoderOrDecoder(SealedInterface.serializer(), SealedInterface.A(""), encodedNbtTag)
            }

            assertEquals(expectedInformativeMessage<SealedInterface>(), failure.message)
        }

    @Test
    fun serializing_with_the_builtin_polymorphic_serializer_should_fail_with_an_informative_unsupported_error() =
        parameterizeTest {
            val nbt by parameterOfConfiguredNbtFormats()

            val failure = assertFailsWith<UnsupportedOperationException> {
                nbt.verifyEncoderOrDecoder(PolymorphicClass.serializer(), PolymorphicClass.Actual(""), encodedNbtTag)
            }

            assertEquals(expectedInformativeMessage<PolymorphicClass>(), failure.message)
        }
}
