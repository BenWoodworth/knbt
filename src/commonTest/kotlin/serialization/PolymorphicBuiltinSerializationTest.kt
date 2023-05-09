package net.benwoodworth.knbt.serialization

import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.of
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.test.Test

@OptIn(ExperimentalSerializationApi::class)
class PolymorphicBuiltinSerializationTest : SerializationTest() {
    @Serializable
    private sealed class SealedClass {
        @Serializable
        data class A(val a: String) : SealedClass()

        @Serializable
        data class B(val b: Int) : SealedClass()
    }

    @Serializable
    private abstract class PolymorphicClass {
        @Serializable
        data class A(val a: String) : PolymorphicClass()

        @Serializable
        data class B(val b: Int) : PolymorphicClass()
    }

    private val nbt = NbtFormat(
        serializersModule = SerializersModule {
            polymorphic(PolymorphicClass::class) {
                subclass(PolymorphicClass.A::class)
                subclass(PolymorphicClass.B::class)
            }
        }
    )

    private fun Exhaustive.Companion.builtinPolymorphicTestCase() =
        Exhaustive.of(
            TestCase(
                SealedClass.serializer(),
                SealedClass.A("value"),
                buildNbtCompound {
                    put("type", SealedClass.A.serializer().descriptor.serialName)
                    put("a", "value")
                }
            ),
            TestCase(
                SealedClass.serializer(),
                SealedClass.B(1234),
                buildNbtCompound {
                    put("type", SealedClass.B.serializer().descriptor.serialName)
                    put("b", 1234)
                }
            ),
            TestCase(
                PolymorphicClass.serializer(),
                PolymorphicClass.A("value"),
                buildNbtCompound {
                    put("type", PolymorphicClass.A.serializer().descriptor.serialName)
                    put("a", "value")
                }
            ),
            TestCase(
                PolymorphicClass.serializer(),
                PolymorphicClass.B(1234),
                buildNbtCompound {
                    put("type", PolymorphicClass.B.serializer().descriptor.serialName)
                    put("b", 1234)
                }
            ),
        )

    @Test
    fun builtin_polymorphic_serializer_should_serialize_as_compound_with_additional_type_key() = runTest {
        checkAll(
            Exhaustive.builtinPolymorphicTestCase()
        ) { testCase ->
            nbt.testSerialization(testCase)
        }
    }
}
