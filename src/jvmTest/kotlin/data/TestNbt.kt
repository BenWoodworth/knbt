package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbt
import net.benwoodworth.knbt.getResourceAsStream
import net.benwoodworth.knbt.tag.put

val testTag = buildNbt("hello world") {
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

val testClass = TestNbt(
    helloWorld = TestNbt.HelloWorld(
        name = "Bananrama",
    ),
)

val testBytes = getResourceAsStream("/test.nbt")
    .use { it.readBytes().asList() }
