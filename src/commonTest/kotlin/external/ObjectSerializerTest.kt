package net.benwoodworth.knbt.external

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtNamed
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import kotlin.test.Test
import kotlin.test.assertEquals

class ObjectSerializerTest {
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
    fun serializing_an_object_should_nest_into_a_compound_with_the_class_serial_name_as_the_key() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()

        nbt.verifyEncoderOrDecoder(
            MyObject.serializer(),
            MyObject,
            buildNbtCompound("RootKey") {
                // empty
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }
}
