package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.assertStructureEquals
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put

val testTag: NbtCompound
    get() = buildNbtCompound("hello world") {
        put("name", "Bananrama")
    }

@Serializable
@SerialName("hello world")
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
