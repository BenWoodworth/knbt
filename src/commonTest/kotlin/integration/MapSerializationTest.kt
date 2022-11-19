package net.benwoodworth.knbt.integration

import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import kotlin.test.Test

class MapSerializationTest : SerializationTest() {
    @Test
    fun should_serialize_compound_with_no_entries_to_Map_correctly() {
        defaultNbt.testSerialization(mapOf<String, Int>(), buildNbtCompound { })
    }

    @Test
    fun should_serialize_compound_with_one_entry_to_Map_correctly() {
        defaultNbt.testSerialization(
            mapOf("property" to 7),
            buildNbtCompound {
                put("property", 7)
            },
        )
    }
}
