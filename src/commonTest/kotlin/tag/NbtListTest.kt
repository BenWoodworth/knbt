package net.benwoodworth.knbt.tag

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


class NbtListTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: NbtTag): Unit =
            assertEquals(elements.asList(), nbtListOf(*elements))

        assertWith()
        assertWith(1.toNbtInt())
        assertWith("a".toNbtString(), "b".toNbtString())

        assertEquals(nbtListOf(1.toNbtInt()), nbtListOf(1.toNbtInt()).toNbtList())
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(nbtByteArrayOf(), nbtListOf<NbtByte>())
        assertNotEquals<NbtTag>(nbtIntArrayOf(), nbtListOf<NbtInt>())
        assertNotEquals<NbtTag>(nbtLongArrayOf(), nbtListOf<NbtLong>())
    }
}
