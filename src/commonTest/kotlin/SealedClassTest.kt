package net.benwoodworth.knbt

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlin.test.Test
import kotlin.test.assertEquals

// Regression test for https://github.com/BenWoodworth/knbt/issues/12

@Serializable
private sealed class Person {
    abstract val name: String
    abstract val age: Int

    @Serializable
    @NbtName("Adult")
    data class Adult(override val name: String, override val age: Int, val occupancy: String) : Person()

//    @Serializable
//    @NbtName("Teen")
//    data class Teen(override val name: String, override val age: Int, val school: String) : Person()

    @Serializable
    @NbtName("Child")
    data class Child(override val name: String, override val age: Int, val father: Adult, val mother: Adult) : Person()
}

private val nbt = Nbt {
    variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
    compression = NbtCompression.None // None, Gzip, Zlib
}

private val father = Person.Adult("jon", 32, "accountant")
private val mother = Person.Adult("kathy", 31, "baker")
private val child = Person.Child("nate", 6, father, mother)

class SealedClassTest {
    @Test
    fun should_successfully_encode_and_decode_sealed_classes() {
        val childNbt = nbt.encodeToByteArray(child)

        val childFromNbt: Person.Child = nbt.decodeFromByteArray(childNbt)

        assertEquals(child, childFromNbt)
    }
}
