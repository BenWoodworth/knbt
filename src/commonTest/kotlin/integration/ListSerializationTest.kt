package net.benwoodworth.knbt.integration

import net.benwoodworth.knbt.*
import kotlin.test.Test

class ListSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_List_of_Lists_correctly() {
        defaultNbt.testSerialization(
            listOf(listOf(1.toByte()), listOf()),
            buildNbtList<NbtList<*>> {
                addNbtList<NbtByte> { add(1.toByte()) }
                addNbtList<Nothing> { }
            },
        )
    }
}
