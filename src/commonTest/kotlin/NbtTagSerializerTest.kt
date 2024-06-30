package net.benwoodworth.knbt

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtTagSerializerTest {
    @Test
    fun should_serialize_NbtTag_class_property() = parameterizeTest {
        @Serializable
        @NbtName("")
        data class NbtTagContainer(
            val nbtTag: NbtTag
        )

        val nbt by parameterOfVerifyingNbt()

        val compound = buildNbtCompound {
            put("entry", "Hello, world!")
        }

        nbt.verifyEncoderOrDecoder(
            NbtTagContainer.serializer(),
            NbtTagContainer(compound),
            buildNbtCompound("") {
                put(NbtTagContainer::nbtTag.name, compound)
            },
            testDecodedValue = { value, decodedValue ->
                assertEquals(value, decodedValue, "decodedValue")
            }
        )
    }
}
