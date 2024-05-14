package net.benwoodworth.knbt.serialization

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtNamed
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import kotlin.test.Test

class NbtTagElementSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_NbtTag_class_property() {
        @Serializable
        @NbtNamed("")
        data class NbtTagContainer(
            val nbtTag: NbtTag
        )

        val compound = buildNbtCompound {
            put("entry", "Hello, world!")
        }

        defaultNbt.testSerialization(
            NbtTagContainer.serializer(),
            NbtTagContainer(compound),
            buildNbtCompound("") {
                put(NbtTagContainer::nbtTag.name, compound)
            }
        )
    }
}
