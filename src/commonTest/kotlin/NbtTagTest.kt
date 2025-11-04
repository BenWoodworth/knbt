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
        assertEquals("[B;4B,3B,2B,1B]", NbtByteArray(byteArrayOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun NbtString_toString() {
        assertEquals("\"\"", NbtString("").toString())
        assertEquals("\"hello\"", NbtString("hello").toString())
        assertEquals("\"\\\"double-quoted\\\"\"", NbtString("\"double-quoted\"").toString())
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
        assertEquals("[I;4,3,2,1]", NbtIntArray(intArrayOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun NbtLongArray_toString() {
        assertEquals("[L;4L,3L,2L,1L]", NbtLongArray(longArrayOf(4, 3, 2, 1)).toString())
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
        assertConversionFails(NbtByteArray(byteArrayOf(1)), NbtTag::nbtDouble)

        assertConversionSucceeds(NbtByteArray(byteArrayOf(1)), NbtTag::nbtByteArray)
        assertConversionFails(NbtString("1"), NbtTag::nbtByteArray)

        assertConversionSucceeds(NbtString("1"), NbtTag::nbtString)
        assertConversionFails(NbtList(emptyList<NbtByte>()), NbtTag::nbtString)

        assertConversionSucceeds(NbtList(emptyList<NbtByte>()), NbtTag::nbtList)
        assertConversionFails(NbtCompound(emptyMap()), NbtTag::nbtList)

        assertConversionSucceeds(NbtCompound(emptyMap()), NbtTag::nbtCompound)
        assertConversionFails(NbtIntArray(intArrayOf()), NbtTag::nbtCompound)

        assertConversionSucceeds(NbtIntArray(intArrayOf()), NbtTag::nbtIntArray)
        assertConversionFails(NbtLongArray(longArrayOf()), NbtTag::nbtIntArray)

        assertConversionSucceeds(NbtLongArray(longArrayOf()), NbtTag::nbtLongArray)
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
        assertConversionFails(NbtList(listOf(NbtByteArray(byteArrayOf(1))))) { nbtList<NbtDouble>() }

        assertConversionSucceeds(NbtList(listOf(NbtByteArray(byteArrayOf(1))))) { nbtList<NbtByteArray>() }
        assertConversionFails(NbtList(listOf(NbtString("1")))) { nbtList<NbtByteArray>() }

        assertConversionSucceeds(NbtList(listOf(NbtString("1")))) { nbtList<NbtString>() }
        assertConversionFails(NbtList(listOf(NbtList(emptyList<NbtByte>())))) { nbtList<NbtString>() }

        assertConversionSucceeds(NbtList(listOf(NbtList(emptyList<NbtByte>())))) { nbtList<NbtList<*>>() }
        assertConversionFails(NbtList(listOf(NbtCompound(emptyMap())))) { nbtList<NbtList<*>>() }

        assertConversionSucceeds(NbtList(listOf(NbtCompound(emptyMap())))) { nbtList<NbtCompound>() }
        assertConversionFails(NbtList(listOf(NbtIntArray(intArrayOf())))) { nbtList<NbtCompound>() }

        assertConversionSucceeds(NbtList(listOf(NbtIntArray(intArrayOf())))) { nbtList<NbtIntArray>() }
        assertConversionFails(NbtList(listOf(NbtLongArray(longArrayOf())))) { nbtList<NbtIntArray>() }

        assertConversionSucceeds(NbtList(listOf(NbtLongArray(longArrayOf())))) { nbtList<NbtLongArray>() }
        assertConversionFails(NbtList(listOf(NbtByte(1)))) { nbtList<NbtLongArray>() }
    }
}

class NbtByteArrayTest {
    @Test
    fun Should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Byte): Unit =
            assertEquals(elements.asList(), NbtByteArray(byteArrayOf(*elements)))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_hash_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Byte): Unit =
            assertEquals(elements.asList().hashCode(), NbtByteArray(byteArrayOf(*elements)).hashCode())

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_hash_equal_NbtByteArray_of_same_contents() {
        fun assertWith(vararg elements: Byte): Unit =
            assertEquals(NbtByteArray(byteArrayOf(*elements)).hashCode(), NbtByteArray(byteArrayOf(*elements)).hashCode())

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(emptyList<NbtByte>()), NbtByteArray(byteArrayOf()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtByteArray(byteArrayOf()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtByteArray(byteArrayOf()))
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
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtList(emptyList<NbtByte>()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtList(emptyList<NbtInt>()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtList(emptyList<NbtLong>()))
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
    fun Should_hash_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Int): Unit =
            assertEquals(elements.asList().hashCode(), NbtIntArray(intArrayOf(*elements)).hashCode())

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_hash_equal_NbtIntArray_of_same_contents() {
        fun assertWith(vararg elements: Int): Unit =
            assertEquals(NbtIntArray(intArrayOf(*elements)).hashCode(), NbtIntArray(intArrayOf(*elements)).hashCode())

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(emptyList<NbtInt>()), NbtIntArray(intArrayOf()))
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtIntArray(intArrayOf()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtIntArray(intArrayOf()))
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
    fun Should_hash_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Long): Unit =
            assertEquals(elements.asList().hashCode(), NbtLongArray(longArrayOf(*elements)).hashCode())

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_hash_equal_NbtLongArray_of_same_contents() {
        fun assertWith(vararg elements: Long): Unit =
            assertEquals(NbtLongArray(longArrayOf(*elements)).hashCode(), NbtLongArray(longArrayOf(*elements)).hashCode())

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun Should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(emptyList<NbtLong>()), NbtLongArray(longArrayOf()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtLongArray(longArrayOf()))
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtLongArray(longArrayOf()))
    }
}
