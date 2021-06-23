package net.benwoodworth.knbt.tag

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class NbtCompoundTest {
    @Test
    fun Should_equal_Map_of_same_contents() {
        fun assertWith(vararg elements: Pair<String, NbtTag>): Unit =
            assertEquals(elements.toMap(), nbtCompoundOf(*elements))

        assertWith()
        assertWith("one" to 1.toNbtInt())
        assertWith("a" to "a".toNbtString(), "b" to "b".toNbtString())

        assertEquals(nbtListOf(1.toNbtInt()), nbtListOf(1.toNbtInt()).toNbtList())
    }
}
