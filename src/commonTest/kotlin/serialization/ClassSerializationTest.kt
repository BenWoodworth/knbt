package net.benwoodworth.knbt.serialization

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtNamed
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.putNbtCompound
import net.benwoodworth.knbt.test.file.*
import kotlin.test.Test

class ClassSerializationTest : SerializationTest() {
    @Test
    fun serializing_a_class_should_nest_into_a_compound_with_the_class_serial_name_as_the_key() {
        @Serializable
        @NbtNamed("RootKey")
        data class MyClass(val property: String)

        defaultNbt.testSerialization(
            MyClass.serializer(),
            MyClass("value"),
            buildNbtCompound {
                putNbtCompound("RootKey") {
                    put("property", "value")
                }
            }
        )
    }

    @Test
    fun should_serialize_TestNbt_class_correctly() {
        defaultNbt.testSerialization(TestNbt.serializer(), testClass, testTag)
    }

    @Test
    fun should_serialize_BigTestNbt_class_correctly() {
        defaultNbt.testSerialization(BigTestNbt.serializer(), bigTestClass, bigTestTag)
    }

    @Test
    fun should_serialize_class_with_one_property_correctly() {
        @Serializable
        @NbtNamed("OneProperty")
        data class OneProperty(val property: Int)

        defaultNbt.testSerialization(
            OneProperty.serializer(),
            OneProperty(7),
            buildNbtCompound("OneProperty") {
                put("property", 7)
            },
        )
    }

    @Test
    fun should_serialize_class_with_two_properties_correctly() {
        @Serializable
        @NbtNamed("TwoProperties")
        data class TwoProperties(val entry1: String, val entry2: Long)

        defaultNbt.testSerialization(
            TwoProperties.serializer(),
            TwoProperties(entry1 = "value1", entry2 = 1234L),
            buildNbtCompound("TwoProperties") {
                put("entry1", "value1")
                put("entry2", 1234L)
            },
        )
    }
}
