package net.benwoodworth.knbt

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtTagPolymorphismTest {
    private val nbt = Nbt {
        variant = NbtVariant.Java
        compression = NbtCompression.None
    }

    @Serializable
    @SerialName("")
    private data class NbtTagContainer(
        val nbtTag: NbtTag,
    )

    @Test
    fun Should_encode_NbtCompound_to_NbtTag_property_correctly() {
        val compound = buildNbtCompound {
            put("entry", "Hello, world!")
        }

        val toEncode = NbtTagContainer(compound)

        assertEquals(
            expected = buildNbtCompound("") {
                put(NbtTagContainer::nbtTag.name, compound)
            },
            actual = nbt.encodeToNbtTag(NbtTagContainer.serializer(), toEncode),
        )
    }

    @Test
    fun Should_decode_NbtCompound_from_NbtTag_property_correctly() {
        val compound = buildNbtCompound {
            put("entry", "Hello, world!")
        }

        val toDecode = buildNbtCompound("") {
            put(NbtTagContainer::nbtTag.name, compound)
        }

        assertEquals(
            expected = NbtTagContainer(compound),
            actual = nbt.decodeFromNbtTag(NbtTagContainer.serializer(), toDecode),
        )
    }
}
