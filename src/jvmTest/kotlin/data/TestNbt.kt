package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.getResourceAsStream
import net.benwoodworth.knbt.tag.NbtString
import net.benwoodworth.knbt.tag.buildNbtCompound
import net.benwoodworth.knbt.tag.put
import net.benwoodworth.knbt.tag.putNbtCompound

val testTag = buildNbtCompound {
    putNbtCompound<NbtString>("hello world") {
        put("name", "Bananrama")
    }
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

val testClass = TestNbt(
    helloWorld = TestNbt.HelloWorld(
        name = "Bananrama",
    ),
)

val testBytes = getResourceAsStream("/test.nbt")
    .use { it.readBytes().asList() }
