package net.benwoodworth.knbt.tag

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class NbtByteArrayTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Byte): Unit =
            assertEquals(elements.asList(), nbtByteArrayOf(*elements))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(nbtListOf<NbtByte>(), nbtByteArrayOf())
        assertNotEquals<NbtTag>(nbtIntArrayOf(), nbtByteArrayOf())
        assertNotEquals<NbtTag>(nbtLongArrayOf(), nbtByteArrayOf())
    }
}
