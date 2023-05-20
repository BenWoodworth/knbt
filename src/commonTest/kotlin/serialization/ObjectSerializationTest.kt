package net.benwoodworth.knbt.serialization

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtNamed
import net.benwoodworth.knbt.buildNbtCompound
import kotlin.test.Test

class ObjectSerializationTest : SerializationTest() {
    @Serializable
    @NbtNamed("RootKey")
    private object MyObject {
        var property: String = ""
            private set

        init {
            // Change it from the default so `ignoreDefaults` doesn't apply
            property += "value"
        }
    }

    @Test
    fun serializing_an_object_should_nest_into_a_compound_with_the_class_serial_name_as_the_key() {
        defaultNbt.testSerialization(
            MyObject.serializer(),
            MyObject,
            buildNbtCompound("RootKey") {
                // empty
            }
        )
    }
}
