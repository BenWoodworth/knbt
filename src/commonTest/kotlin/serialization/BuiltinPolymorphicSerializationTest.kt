package net.benwoodworth.knbt.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.test.Test

@OptIn(ExperimentalSerializationApi::class)
class BuiltinPolymorphicSerializationTest : SerializationTest() {
    private inline fun <reified T> serialName(): String =
        serializer<T>().descriptor.serialName

    @Serializable
    private sealed class SealedClass {
        @Serializable
        data class A(val a: String) : SealedClass()

        @Serializable
        data class B(val b: Int) : SealedClass()

        @Serializable
        data class C(val c: Boolean) : SealedClass()
    }

    @Test
    fun should_serialize_sealed_class_as_compound_with_a_discriminating_type_field() {
        defaultNbt.testSerialization<SealedClass>(
            SealedClass.A("asdf"),
            buildNbtCompound {
                put("type", serialName<SealedClass.A>())
                put("a", "asdf")
            },
        )

        defaultNbt.testSerialization<SealedClass>(
            SealedClass.B(42),
            buildNbtCompound {
                put("type", serialName<SealedClass.B>())
                put("b", 42)
            },
        )

        defaultNbt.testSerialization<SealedClass>(
            SealedClass.C(false),
            buildNbtCompound {
                put("type", serialName<SealedClass.C>())
                put("c", false)
            },
        )
    }

    @Serializable
    private abstract class PolymorphicClass {
        @Serializable
        data class A(val a: String) : PolymorphicClass()

        @Serializable
        data class B(val b: Int) : PolymorphicClass()

        @Serializable
        data class C(val c: Boolean) : PolymorphicClass()
    }

    @Test
    fun should_serialize_polymorphic_interface_as_compound_with_a_discriminating_type_field() {
        val nbt = NbtFormat(
            serializersModule = SerializersModule {
                polymorphic(PolymorphicClass::class) {
                    subclass(PolymorphicClass.A.serializer())
                    subclass(PolymorphicClass.B.serializer())
                    subclass(PolymorphicClass.C.serializer())
                }
            }
        )

        nbt.testSerialization<PolymorphicClass>(
            PolymorphicClass.A("asdf"),
            buildNbtCompound {
                put("type", serialName<PolymorphicClass.A>())
                put("a", "asdf")
            },
        )

        nbt.testSerialization<PolymorphicClass>(
            PolymorphicClass.B(42),
            buildNbtCompound {
                put("type", serialName<PolymorphicClass.B>())
                put("b", 42)
            },
        )

        nbt.testSerialization<PolymorphicClass>(
            PolymorphicClass.C(false),
            buildNbtCompound {
                put("type", serialName<PolymorphicClass.C>())
                put("c", false)
            },
        )
    }
}
