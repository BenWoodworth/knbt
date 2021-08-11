package net.benwoodworth.knbt

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

class NbtIntArrayTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Int): Unit =
            assertEquals(elements.asList(), nbtIntArrayOf(*elements))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(nbtListOf<NbtInt>(), nbtIntArrayOf())
        assertNotEquals<NbtTag>(nbtByteArrayOf(), nbtIntArrayOf())
        assertNotEquals<NbtTag>(nbtLongArrayOf(), nbtIntArrayOf())
    }
}

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
