package net.benwoodworth.knbt

import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class NbtTagToStringTest {
    @Test
    fun NbtByte_toString() {
        assertEquals("7b", NbtByte(7).toString())
    }

    @Test
    fun NbtShort_toString() {
        assertEquals("8s", NbtShort(8).toString())
    }

    @Test
    fun NbtInt_toString() {
        assertEquals("9", NbtInt(9).toString())
    }

    @Test
    fun NbtLong_toString() {
        assertEquals("10L", NbtLong(10).toString())
    }

    @Test
    fun NbtFloat_toString() {
        assertEquals("3.1415f", NbtFloat(3.1415f).toString())
    }

    @Test
    fun NbtDouble_toString() {
        assertEquals("2.71828d", NbtDouble(2.71828).toString())
    }

    @Test
    fun NbtByteArray_toString() {
        assertEquals("[B;4B,3B,2B,1B]", NbtByteArray(listOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun NbtString_toString() {
        assertEquals("hello", NbtString("hello").toString())
        assertEquals("'\"double-quoted\"'", NbtString("\"double-quoted\"").toString())
        assertEquals("\"'single-quoted'\"", NbtString("'single-quoted'").toString())
        assertEquals("\"\\\"'multi-quoted'\\\"\"", NbtString("\"'multi-quoted'\"").toString())
    }

    @Test
    fun NbtList_toString() {
        assertEquals("[]", NbtList(emptyList<NbtByte>()).toString())
        assertEquals("[1b]", NbtList(listOf(NbtByte(1))).toString())
        assertEquals("[[]]", NbtList(listOf(NbtList(emptyList<NbtByte>()))).toString())
    }

    @Test
    fun NbtCompound_toString() {
        assertEquals("{}", buildNbtCompound { }.toString())
        assertEquals("{a:1b}", buildNbtCompound { put("a", 1.toByte()) }.toString())
        assertEquals("{a:1b,b:7}", buildNbtCompound { put("a", 1.toByte()); put("b", 7) }.toString())
    }

    @Test
    fun NbtIntArray_toString() {
        assertEquals("[I;4,3,2,1]", NbtIntArray(listOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun NbtLongArray_toString() {
        assertEquals("[L;4L,3L,2L,1L]", NbtLongArray(listOf(4, 3, 2, 1)).toString())
    }
}

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
        assertConversionFails(NbtByteArray(listOf(1)), NbtTag::nbtDouble)

        assertConversionSucceeds(NbtByteArray(listOf(1)), NbtTag::nbtByteArray)
        assertConversionFails(NbtString("1"), NbtTag::nbtByteArray)

        assertConversionSucceeds(NbtString("1"), NbtTag::nbtString)
        assertConversionFails(NbtList(emptyList<NbtByte>()), NbtTag::nbtString)

        assertConversionSucceeds(NbtList(emptyList<NbtByte>()), NbtTag::nbtList)
        assertConversionFails(NbtCompound(emptyMap()), NbtTag::nbtList)

        assertConversionSucceeds(NbtCompound(emptyMap()), NbtTag::nbtCompound)
        assertConversionFails(NbtIntArray(listOf()), NbtTag::nbtCompound)

        assertConversionSucceeds(NbtIntArray(listOf()), NbtTag::nbtIntArray)
        assertConversionFails(NbtLongArray(listOf()), NbtTag::nbtIntArray)

        assertConversionSucceeds(NbtLongArray(listOf()), NbtTag::nbtLongArray)
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

        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtByte>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtShort>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtInt>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtLong>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtFloat>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtDouble>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtByteArray>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtString>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtList<*>>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtCompound>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtIntArray>() }
        assertConversionSucceeds(NbtList(emptyList<NbtByte>())) { nbtList<NbtLongArray>() }

        assertConversionSucceeds(NbtList(listOf(NbtByte(1)))) { nbtList<NbtByte>() }
        assertConversionFails(NbtList(listOf(NbtShort(1)))) { nbtList<NbtByte>() }

        assertConversionSucceeds(NbtList(listOf(NbtShort(1)))) { nbtList<NbtShort>() }
        assertConversionFails(NbtList(listOf(NbtInt(1)))) { nbtList<NbtShort>() }

        assertConversionSucceeds(NbtList(listOf(NbtInt(1)))) { nbtList<NbtInt>() }
        assertConversionFails(NbtList(listOf(NbtLong(1)))) { nbtList<NbtInt>() }

        assertConversionSucceeds(NbtList(listOf(NbtLong(1)))) { nbtList<NbtLong>() }
        assertConversionFails(NbtList(listOf(NbtFloat(1.0f)))) { nbtList<NbtLong>() }

        assertConversionSucceeds(NbtList(listOf(NbtFloat(1.0f)))) { nbtList<NbtFloat>() }
        assertConversionFails(NbtList(listOf(NbtDouble(1.0)))) { nbtList<NbtFloat>() }

        assertConversionSucceeds(NbtList(listOf(NbtDouble(1.0)))) { nbtList<NbtDouble>() }
        assertConversionFails(NbtList(listOf(NbtByteArray(listOf(1))))) { nbtList<NbtDouble>() }

        assertConversionSucceeds(NbtList(listOf(NbtByteArray(listOf(1))))) { nbtList<NbtByteArray>() }
        assertConversionFails(NbtList(listOf(NbtString("1")))) { nbtList<NbtByteArray>() }

        assertConversionSucceeds(NbtList(listOf(NbtString("1")))) { nbtList<NbtString>() }
        assertConversionFails(NbtList(listOf(NbtList(emptyList<NbtByte>())))) { nbtList<NbtString>() }

        assertConversionSucceeds(NbtList(listOf(NbtList(emptyList<NbtByte>())))) { nbtList<NbtList<*>>() }
        assertConversionFails(NbtList(listOf(NbtCompound(emptyMap())))) { nbtList<NbtList<*>>() }

        assertConversionSucceeds(NbtList(listOf(NbtCompound(emptyMap())))) { nbtList<NbtCompound>() }
        assertConversionFails(NbtList(listOf(NbtIntArray(listOf())))) { nbtList<NbtCompound>() }

        assertConversionSucceeds(NbtList(listOf(NbtIntArray(listOf())))) { nbtList<NbtIntArray>() }
        assertConversionFails(NbtList(listOf(NbtLongArray(listOf())))) { nbtList<NbtIntArray>() }

        assertConversionSucceeds(NbtList(listOf(NbtLongArray(listOf())))) { nbtList<NbtLongArray>() }
        assertConversionFails(NbtList(listOf(NbtByte(1)))) { nbtList<NbtLongArray>() }
    }
}

class NbtByteArrayTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Byte): Unit =
            assertEquals(elements.asList(), NbtByteArray(listOf(*elements.toTypedArray())))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(emptyList<NbtByte>()), NbtByteArray(listOf()))
        assertNotEquals<NbtTag>(NbtIntArray(listOf()), NbtByteArray(listOf()))
        assertNotEquals<NbtTag>(NbtLongArray(listOf()), NbtByteArray(listOf()))
    }
}

class NbtListTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(nbtList: NbtList<*>): Unit =
            assertEquals(nbtList.toList(), nbtList)

        assertWith(NbtList(emptyList<NbtByte>()))
        assertWith(NbtList(listOf(NbtInt(1))))
        assertWith(NbtList(listOf(NbtString("a"), NbtString("b"))))

        assertEquals(NbtList(listOf(NbtInt(1))), NbtList(NbtList(listOf(NbtInt(1)))))
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtByteArray(listOf()), NbtList(emptyList<NbtByte>()))
        assertNotEquals<NbtTag>(NbtIntArray(listOf()), NbtList(emptyList<NbtInt>()))
        assertNotEquals<NbtTag>(NbtLongArray(listOf()), NbtList(emptyList<NbtLong>()))
    }
}

class NbtCompoundTest {
    @Test
    fun Should_equal_Map_of_same_contents() {
        fun assertWith(vararg elements: Pair<String, NbtTag>): Unit =
            assertEquals(elements.toMap(), NbtCompound(mapOf(*elements)))

        assertWith()
        assertWith("one" to NbtInt(1))
        assertWith("a" to NbtString("a"), "b" to NbtString("b"))

        assertEquals(NbtList(listOf(NbtInt(1))), NbtList(NbtList(listOf(NbtInt(1)))))
    }
}

class NbtIntArrayTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Int): Unit =
            assertEquals(elements.asList(), NbtIntArray(elements))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(emptyList<NbtInt>()), NbtIntArray(listOf()))
        assertNotEquals<NbtTag>(NbtByteArray(listOf()), NbtIntArray(listOf()))
        assertNotEquals<NbtTag>(NbtLongArray(listOf()), NbtIntArray(listOf()))
    }
}

class NbtLongArrayTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Long): Unit =
            assertEquals(elements.asList(), NbtLongArray(elements))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(emptyList<NbtLong>()), NbtLongArray(listOf()))
        assertNotEquals<NbtTag>(NbtIntArray(listOf()), NbtLongArray(listOf()))
        assertNotEquals<NbtTag>(NbtByteArray(listOf()), NbtLongArray(listOf()))
    }
}
