package net.benwoodworth.knbt.tag

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class NbtLongArrayTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Long): Unit =
            assertEquals(elements.asList(), nbtLongArrayOf(*elements))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(nbtListOf<NbtLong>(), nbtLongArrayOf())
        assertNotEquals<NbtTag>(nbtIntArrayOf(), nbtLongArrayOf())
        assertNotEquals<NbtTag>(nbtByteArrayOf(), nbtLongArrayOf())
    }
}
