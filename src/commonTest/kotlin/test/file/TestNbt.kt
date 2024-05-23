package net.benwoodworth.knbt.test.file

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtNamed
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.assertStructureEquals

val testTag: NbtCompound
    get() = buildNbtCompound("hello world") {
        put("name", "Bananrama")
    }

@Serializable
@NbtNamed("hello world")
data class TestNbt(
    val name: String,
)

fun assertStructureEquals(expected: TestNbt, actual: TestNbt, message: String? = null): Unit =
    assertStructureEquals(expected, actual, message) {
        property("name") { name }
    }

val testClass: TestNbt
    get() = TestNbt(
        name = "Bananrama",
    )
