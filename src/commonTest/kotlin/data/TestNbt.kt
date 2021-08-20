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
data class TestNbt(
    @SerialName("hello world")
    val helloWorld: HelloWorld,
) {
    @Serializable
    data class HelloWorld(
        val name: String,
    )
}

fun assertStructureEquals(expected: TestNbt, actual: TestNbt, message: String? = null): Unit =
    assertStructureEquals(expected, actual, message) {
        property("helloWorld.name") { helloWorld.name }
    }

val testClass: TestNbt
    get() = TestNbt(
        helloWorld = TestNbt.HelloWorld(
            name = "Bananrama",
        ),
    )
