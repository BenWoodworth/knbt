package net.benwoodworth.knbt

import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class NbtTagTestConversions {
    @Test
    fun Should_convert_correctly_with_properties() {
        fun <R : NbtTag> assertConversionSucceeds(tag: NbtTag, convert: KProperty1<NbtTag, R>): Unit =
            assertEquals(tag, convert(tag), "Converting ${tag::class.simpleName} with ${convert.name}")

        fun <R : NbtTag> assertConversionFails(tag: NbtTag, convert: KProperty1<NbtTag, R>): RuntimeException =
            assertFailsWith<IllegalArgumentException>("Converting ${tag::class.simpleName} with ${convert.name}") {
                convert(tag)
            }

        assertConversionSucceeds(NbtByte(1), NbtTag::nbtByte)
        assertConversionFails(NbtShort(1), NbtTag::nbtByte)

        assertConversionSucceeds(NbtShort(1), NbtTag::nbtShort)
        assertConversionFails(NbtInt(1), NbtTag::nbtShort)

        assertConversionSucceeds(NbtInt(1), NbtTag::nbtInt)
        assertConversionFails(NbtLong(1), NbtTag::nbtInt)

        assertConversionSucceeds(NbtLong(1), NbtTag::nbtLong)
        assertConversionFails(NbtFloat(1.0f), NbtTag::nbtLong)

        assertConversionSucceeds(NbtFloat(1.0f), NbtTag::nbtFloat)
        assertConversionFails(NbtDouble(1.0), NbtTag::nbtFloat)

        assertConversionSucceeds(NbtDouble(1.0), NbtTag::nbtDouble)
        assertConversionFails(nbtByteArrayOf(1), NbtTag::nbtDouble)

        assertConversionSucceeds(nbtByteArrayOf(1), NbtTag::nbtByteArray)
        assertConversionFails(NbtString("1"), NbtTag::nbtByteArray)

        assertConversionSucceeds(NbtString("1"), NbtTag::nbtString)
        assertConversionFails(nbtListOf<NbtTag>(), NbtTag::nbtString)

        assertConversionSucceeds(nbtListOf<NbtTag>(), NbtTag::nbtList)
        assertConversionFails(nbtCompoundOf(), NbtTag::nbtList)

        assertConversionSucceeds(nbtCompoundOf(), NbtTag::nbtCompound)
        assertConversionFails(nbtIntArrayOf(), NbtTag::nbtCompound)

        assertConversionSucceeds(nbtIntArrayOf(), NbtTag::nbtIntArray)
        assertConversionFails(nbtLongArrayOf(), NbtTag::nbtIntArray)

        assertConversionSucceeds(nbtLongArrayOf(), NbtTag::nbtLongArray)
        assertConversionFails(NbtByte(1), NbtTag::nbtLongArray)
    }

    @Test
    @OptIn(ExperimentalNbtApi::class)
    fun Should_convert_to_typed_NbtList_correctly() {
        fun <R : NbtTag> assertConversionSucceeds(tag: NbtTag, convert: NbtTag.() -> R): Unit =
            assertEquals(tag, convert(tag), "Converting ${tag::class.simpleName}")

        fun <R : NbtTag> assertConversionFails(tag: NbtTag, convert: NbtTag.() -> R): RuntimeException =
            assertFailsWith<IllegalArgumentException>("Converting ${tag::class.simpleName}") {
                convert(tag)
            }

        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtByte>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtShort>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtInt>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtLong>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtFloat>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtDouble>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtByteArray>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtString>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtList<*>>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtCompound>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtIntArray>() }
        assertConversionSucceeds(nbtListOf<Nothing>()) { nbtList<NbtLongArray>() }

        assertConversionSucceeds(nbtListOf(NbtByte(1))) { nbtList<NbtByte>() }
        assertConversionFails(nbtListOf(NbtShort(1))) { nbtList<NbtByte>() }

        assertConversionSucceeds(nbtListOf(NbtShort(1))) { nbtList<NbtShort>() }
        assertConversionFails(nbtListOf(NbtInt(1))) { nbtList<NbtShort>() }

        assertConversionSucceeds(nbtListOf(NbtInt(1))) { nbtList<NbtInt>() }
        assertConversionFails(nbtListOf(NbtLong(1))) { nbtList<NbtInt>() }

        assertConversionSucceeds(nbtListOf(NbtLong(1))) { nbtList<NbtLong>() }
        assertConversionFails(nbtListOf(NbtFloat(1.0f))) { nbtList<NbtLong>() }

        assertConversionSucceeds(nbtListOf(NbtFloat(1.0f))) { nbtList<NbtFloat>() }
        assertConversionFails(nbtListOf(NbtDouble(1.0))) { nbtList<NbtFloat>() }

        assertConversionSucceeds(nbtListOf(NbtDouble(1.0))) { nbtList<NbtDouble>() }
        assertConversionFails(nbtListOf(nbtByteArrayOf(1))) { nbtList<NbtDouble>() }

        assertConversionSucceeds(nbtListOf(nbtByteArrayOf(1))) { nbtList<NbtByteArray>() }
        assertConversionFails(nbtListOf(NbtString("1"))) { nbtList<NbtByteArray>() }

        assertConversionSucceeds(nbtListOf(NbtString("1"))) { nbtList<NbtString>() }
        assertConversionFails(nbtListOf(nbtListOf<NbtTag>())) { nbtList<NbtString>() }

        assertConversionSucceeds(nbtListOf(nbtListOf<NbtTag>())) { nbtList<NbtList<*>>() }
        assertConversionFails(nbtListOf(nbtCompoundOf())) { nbtList<NbtList<*>>() }

        assertConversionSucceeds(nbtListOf(nbtCompoundOf())) { nbtList<NbtCompound>() }
        assertConversionFails(nbtListOf(nbtIntArrayOf())) { nbtList<NbtCompound>() }

        assertConversionSucceeds(nbtListOf(nbtIntArrayOf())) { nbtList<NbtIntArray>() }
        assertConversionFails(nbtListOf(nbtLongArrayOf())) { nbtList<NbtIntArray>() }

        assertConversionSucceeds(nbtListOf(nbtLongArrayOf())) { nbtList<NbtLongArray>() }
        assertConversionFails(nbtListOf(NbtByte(1))) { nbtList<NbtLongArray>() }
    }
}

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
