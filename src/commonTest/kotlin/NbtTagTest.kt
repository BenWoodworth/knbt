package net.benwoodworth.knbt

import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.bytes
import io.kotest.property.exhaustive.filter
import io.kotest.property.exhaustive.map
import kotlinx.coroutines.test.runTest
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class NbtByteTest {
    @Test
    fun creating_from_false_boolean_should_return_0b() {
        assertEquals(NbtByte(0), NbtByte.fromBoolean(false))
    }

    @Test
    fun creating_from_true_boolean_should_return_1b() {
        assertEquals(NbtByte(1), NbtByte.fromBoolean(true))
    }

    @Test
    fun converting_0b_to_boolean_should_be_false() {
        assertEquals(false, NbtByte(0).toBoolean())
    }

    @Test
    fun converting_non_zero_to_boolean_should_be_true() = runTest {
        val nonZeroNbtBytes = Exhaustive.bytes()
            .filter { it != 0.toByte() }
            .map { NbtByte(it) }

        checkAll(nonZeroNbtBytes) { nbtByte ->
            assertEquals(true, nbtByte.toBoolean())
        }
    }
}

class NbtTagToStringTest {
    @Test
    fun converting_NbtByte_to_string() {
        assertEquals("7b", NbtByte(7).toString())
    }

    @Test
    fun converting_NbtShort_to_string() {
        assertEquals("8s", NbtShort(8).toString())
    }

    @Test
    fun converting_NbtInt_to_string() {
        assertEquals("9", NbtInt(9).toString())
    }

    @Test
    fun converting_NbtLong_to_string() {
        assertEquals("10L", NbtLong(10).toString())
    }

    @Test
    fun converting_NbtFloat_to_string() {
        assertEquals("3.1415f", NbtFloat(3.1415f).toString())
    }

    @Test
    fun converting_NbtDouble_to_string() {
        assertEquals("2.71828d", NbtDouble(2.71828).toString())
    }

    @Test
    fun converting_NbtByteArray_to_string() {
        assertEquals("[B;4B,3B,2B,1B]", NbtByteArray(byteArrayOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun converting_NbtString_to_string() {
        assertEquals("\"\"", NbtString("").toString())
        assertEquals("\"hello\"", NbtString("hello").toString())
        assertEquals("\"\\\"double-quoted\\\"\"", NbtString("\"double-quoted\"").toString())
        assertEquals("\"'single-quoted'\"", NbtString("'single-quoted'").toString())
        assertEquals("\"\\\"'multi-quoted'\\\"\"", NbtString("\"'multi-quoted'\"").toString())
    }

    @Test
    fun converting_NbtList_to_string() {
        assertEquals("[]", NbtList(emptyList<NbtByte>()).toString())
        assertEquals("[1b]", NbtList(listOf(NbtByte(1))).toString())
        assertEquals("[[]]", NbtList(listOf(NbtList(emptyList<NbtByte>()))).toString())
    }

    @Test
    fun converting_NbtCompound_to_string() {
        assertEquals("{}", buildNbtCompound { }.toString())
        assertEquals("{a:1b}", buildNbtCompound { put("a", 1.toByte()) }.toString())
        assertEquals("{a:1b,b:7}", buildNbtCompound { put("a", 1.toByte()); put("b", 7) }.toString())
    }

    @Test
    fun converting_NbtIntArray_to_string() {
        assertEquals("[I;4,3,2,1]", NbtIntArray(intArrayOf(4, 3, 2, 1)).toString())
    }

    @Test
    fun converting_NbtLongArray_to_string() {
        assertEquals("[L;4L,3L,2L,1L]", NbtLongArray(longArrayOf(4, 3, 2, 1)).toString())
    }
}

class NbtTagTestConversions {
    @Test
    fun should_convert_correctly_with_properties() {
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
    fun should_convert_to_typed_NbtList_correctly() {
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
    fun should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Byte): Unit =
            assertEquals(elements.asList(), NbtByteArray(byteArrayOf(*elements)))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(emptyList<NbtByte>()), NbtByteArray(byteArrayOf()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtByteArray(byteArrayOf()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtByteArray(byteArrayOf()))
    }
}

class NbtListTest {
    @Test
    fun should_equal_List_of_same_contents() {
        fun assertWith(nbtList: NbtList<*>): Unit =
            assertEquals(nbtList.toList(), nbtList)

        assertWith(NbtList(emptyList<NbtByte>()))
        assertWith(NbtList(listOf(NbtInt(1))))
        assertWith(NbtList(listOf(NbtString("a"), NbtString("b"))))

        assertEquals(NbtList(listOf(NbtInt(1))), NbtList(NbtList(listOf(NbtInt(1)))))
    }

    @Test
    fun should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtList(emptyList<NbtByte>()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtList(emptyList<NbtInt>()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtList(emptyList<NbtLong>()))
    }
}

class NbtCompoundTest {
    @Test
    fun should_equal_Map_of_same_contents() {
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
    fun should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Int): Unit =
            assertEquals(elements.asList(), NbtIntArray(elements))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(emptyList<NbtInt>()), NbtIntArray(intArrayOf()))
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtIntArray(intArrayOf()))
        assertNotEquals<NbtTag>(NbtLongArray(longArrayOf()), NbtIntArray(intArrayOf()))
    }
}

class NbtLongArrayTest {
    @Test
    fun should_equal_List_of_same_contents() {
        fun assertWith(vararg elements: Long): Unit =
            assertEquals(elements.asList(), NbtLongArray(elements))

        assertWith()
        assertWith(1)
        assertWith(1, 2, 4, 8)
    }

    @Test
    fun should_not_equal_NbtTag_of_different_type_but_same_contents() {
        assertNotEquals<NbtTag>(NbtList(emptyList<NbtLong>()), NbtLongArray(longArrayOf()))
        assertNotEquals<NbtTag>(NbtIntArray(intArrayOf()), NbtLongArray(longArrayOf()))
        assertNotEquals<NbtTag>(NbtByteArray(byteArrayOf()), NbtLongArray(longArrayOf()))
    }
}
