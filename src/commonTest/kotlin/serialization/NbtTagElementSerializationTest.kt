package net.benwoodworth.knbt.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.*
import kotlin.test.Test

class NbtTagElementSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_NbtTag_class_property() {
        @Serializable
        @SerialName("")
        data class NbtTagContainer(
            val nbtTag: NbtTag
        )

        val compound = buildNbtCompound {
            put("entry", "Hello, world!")
        }

        defaultNbt.testSerialization(
            NbtTagContainer(compound),
            buildNbtCompound("") {
                put(NbtTagContainer::nbtTag.name, compound)
            }
        )
    }
}
