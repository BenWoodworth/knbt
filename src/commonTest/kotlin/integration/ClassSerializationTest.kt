package net.benwoodworth.knbt.integration

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.putNbtCompound
import net.benwoodworth.knbt.test.data.bigTestClass
import net.benwoodworth.knbt.test.data.bigTestTag
import net.benwoodworth.knbt.test.data.testClass
import net.benwoodworth.knbt.test.data.testTag
import kotlin.test.Test

class ClassSerializationTest : SerializationTest() {
    @Test
    fun serializing_a_class_should_nest_into_a_compound_with_the_class_serial_name_as_the_key() {
        @Serializable
        @SerialName("RootKey")
        data class MyClass(val property: String)

        defaultNbt.testSerialization(
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
        defaultNbt.testSerialization(testClass, testTag)
    }

    @Test
    fun should_serialize_BigTestNbt_class_correctly() {
        defaultNbt.testSerialization(bigTestClass, bigTestTag)
    }

    @Test
    fun should_serialize_class_with_one_property_correctly() {
        @Serializable
        @SerialName("OneProperty")
        data class OneProperty(val property: Int)

        defaultNbt.testSerialization(
            OneProperty(7),
            buildNbtCompound("OneProperty") {
                put("property", 7)
            },
        )
    }

    @Test
    fun should_serialize_class_with_two_properties_correctly() {
        @Serializable
        @SerialName("TwoProperties")
        data class TwoProperties(val entry1: String, val entry2: Long)

        defaultNbt.testSerialization(
            TwoProperties(entry1 = "value1", entry2 = 1234L),
            buildNbtCompound("TwoProperties") {
                put("entry1", "value1")
                put("entry2", 1234L)
            },
        )
    }
}
