package net.benwoodworth.knbt

//import io.kotest.property.Arb
//import io.kotest.property.arbitrary.string
//import io.kotest.property.assume
//import io.kotest.property.checkAll
//import kotlinx.coroutines.test.runTest
//import net.benwoodworth.knbt.test.parameterize.arguments.byteEdgeCases
//import net.benwoodworth.knbt.test.parameterize.arguments.bytes
//import net.benwoodworth.knbt.test.generators.*
//import net.benwoodworth.knbt.test.parameterize.flatZip
//import net.benwoodworth.knbt.test.parameterize.parameterize
//import kotlin.reflect.KProperty1
//import kotlin.test.*
//
//class NbtByteTest {
//    @Test
//    fun should_equal_another_NbtByte_with_the_same_value() = parameterize {
//        val byte by parameter { byteEdgeCases() }
//
//        assertEquals(NbtByte(byte), NbtByte(byte))
//    }
//
//    @Test
//    fun should_not_equal_another_NbtByte_with_different_value() = parameterize {
//        val byteToDifferentByte by parameter {
//            byteEdgeCases().flatZip { byteEdgeCases(it) }
//        }
//        val (byte, differentByte) = byteToDifferentByte
//
//        assertNotEquals(byte, differentByte)
//    }
//
//    @Test
//    fun hash_code_should_be_the_value_hash_code() = runTest {
//        checkAll(Arb.nbtByte()) { nbtByte ->
//            assertEquals(nbtByte.value.hashCode(), nbtByte.hashCode())
//        }
//    }
//
//    @Test
//    fun creating_from_false_boolean_should_return_0b() {
//        assertEquals(NbtByte(0), NbtByte.fromBoolean(false))
//    }
//
//    @Test
//    fun creating_from_true_boolean_should_return_1b() {
//        assertEquals(NbtByte(1), NbtByte.fromBoolean(true))
//    }
//
//    @Test
//    fun converting_0b_to_boolean_should_be_false() {
//        assertEquals(false, NbtByte(0).toBoolean())
//    }
//
//    @Test
//    fun converting_non_zero_to_boolean_should_be_true() = parameterize {
//        val nonZeroNbtByte by parameter {
//            bytes().filter { it != 0.toByte() }.map(::NbtByte)
//        }
//
//        assertEquals(true, nonZeroNbtByte.toBoolean())
//    }
//}
//
//class NbtShortTest {
//    @Test
//    fun should_equal_another_NbtShort_with_the_same_value() = runTest {
//        checkAll(Arb.nbtShort()) { nbtShort ->
//            val nbtShortWithSameValue = NbtShort(nbtShort.value)
//
//            assertEquals(nbtShortWithSameValue, nbtShort)
//        }
//    }
//
//    @Test
//    fun should_not_equal_another_NbtShort_with_different_value() = runTest {
//        checkAll(Arb.nbtShort(), Arb.nbtShort()) { nbtShortA, nbtShortB ->
//            assume(nbtShortA.value != nbtShortB.value)
//
//            assertNotEquals(nbtShortA, nbtShortB)
//        }
//    }
//
//    @Test
//    fun hash_code_should_be_the_value_hash_code() = runTest {
//        checkAll(Arb.nbtShort()) { nbtShort ->
//            assertEquals(nbtShort.value.hashCode(), nbtShort.hashCode())
//        }
//    }
//}
//
//class NbtIntTest {
//    @Test
//    fun should_equal_another_NbtInt_with_the_same_value() = runTest {
//        checkAll(Arb.nbtInt()) { nbtInt ->
//            val nbtIntWithSameValue = NbtInt(nbtInt.value)
//
//            assertEquals(nbtIntWithSameValue, nbtInt)
//        }
//    }
//
//    @Test
//    fun should_not_equal_another_NbtInt_with_different_value() = runTest {
//        checkAll(Arb.nbtInt(), Arb.nbtInt()) { nbtIntA, nbtIntB ->
//            assume(nbtIntA.value != nbtIntB.value)
//
//            assertNotEquals(nbtIntA, nbtIntB)
//        }
//    }
//
//    @Test
//    fun hash_code_should_be_the_value_hash_code() = runTest {
//        checkAll(Arb.nbtInt()) { nbtInt ->
//            assertEquals(nbtInt.value.hashCode(), nbtInt.hashCode())
//        }
//    }
//}
//
//class NbtLongTest {
//    @Test
//    fun should_equal_another_NbtLong_with_the_same_value() = runTest {
//        checkAll(Arb.nbtLong()) { nbtLong ->
//            val nbtLongWithSameValue = NbtLong(nbtLong.value)
//
//            assertEquals(nbtLongWithSameValue, nbtLong)
//        }
//    }
//
//    @Test
//    fun should_not_equal_another_NbtLong_with_different_value() = runTest {
//        checkAll(Arb.nbtLong(), Arb.nbtLong()) { nbtLongA, nbtLongB ->
//            assume(nbtLongA.value != nbtLongB.value)
//
//            assertNotEquals(nbtLongA, nbtLongB)
//        }
//    }
//
//    @Test
//    fun hash_code_should_be_the_value_hash_code() = runTest {
//        checkAll(Arb.nbtLong()) { nbtLong ->
//            assertEquals(nbtLong.value.hashCode(), nbtLong.hashCode())
//        }
//    }
//}
//
//class NbtFloatTest {
//    @Test
//    fun should_equal_another_NbtFloat_with_the_same_value_bits() = runTest {
//        checkAll(Arb.nbtFloat()) { nbtFloat ->
//            val nbtFloatWithSameValue = NbtFloat(nbtFloat.value)
//
//            assertEquals(nbtFloatWithSameValue, nbtFloat)
//        }
//    }
//
//    @Test
//    fun should_not_equal_another_NbtFloat_with_different_value_bits() = runTest {
//        checkAll(Arb.nbtFloat(), Arb.nbtFloat()) { nbtFloatA, nbtFloatB ->
//            assume(nbtFloatA.value.toRawBits() != nbtFloatB.value.toRawBits())
//
//            assertNotEquals(nbtFloatA, nbtFloatB)
//        }
//    }
//
//    @Test
//    fun hash_code_should_be_the_value_bits_hash_code() = runTest {
//        checkAll(Arb.nbtFloat()) { nbtFloat ->
//            assertEquals(nbtFloat.value.toRawBits().hashCode(), nbtFloat.hashCode())
//        }
//    }
//}
//
//class NbtDoubleTest {
//    @Test
//    fun should_equal_another_NbtDouble_with_the_same_value_bits() = runTest {
//        checkAll(Arb.nbtDouble()) { nbtDouble ->
//            val nbtDoubleWithSameValue = NbtDouble(nbtDouble.value)
//
//            assertEquals(nbtDoubleWithSameValue, nbtDouble)
//        }
//    }
//
//    @Test
//    fun should_not_equal_another_NbtDouble_with_different_value_bits() = runTest {
//        checkAll(Arb.nbtDouble(), Arb.nbtDouble()) { nbtDoubleA, nbtDoubleB ->
//            assume(nbtDoubleA.value.toRawBits() != nbtDoubleB.value.toRawBits())
//
//            assertNotEquals(nbtDoubleA, nbtDoubleB)
//        }
//    }
//
//    @Test
//    fun hash_code_should_be_the_value_bits_hash_code() = runTest {
//        checkAll(Arb.nbtDouble()) { nbtDouble ->
//            assertEquals(nbtDouble.value.toRawBits().hashCode(), nbtDouble.hashCode())
//        }
//    }
//}
//
//class NbtStringTest {
//    @Test
//    fun should_equal_another_NbtString_with_the_same_value() = runTest {
//        checkAll(Arb.nbtString()) { nbtString ->
//            val nbtStringWithSameValue = NbtString(nbtString.value)
//
//            assertEquals(nbtStringWithSameValue, nbtString)
//        }
//    }
//
//    @Test
//    fun should_not_equal_another_NbtString_with_different_value() = runTest {
//        checkAll(Arb.nbtString(), Arb.nbtString()) { nbtStringA, nbtStringB ->
//            assume(nbtStringA.value != nbtStringB.value)
//
//            assertNotEquals(nbtStringA, nbtStringB)
//        }
//    }
//
//    @Test
//    fun hash_code_should_be_the_value_hash_code() = runTest {
//        checkAll(Arb.nbtString()) { nbtString ->
//            assertEquals(nbtString.value.hashCode(), nbtString.hashCode())
//        }
//    }
//}
//
//class NbtTagToStringTest {
//    @Test
//    fun converting_NbtByte_to_string() {
//        assertEquals("7b", NbtByte(7).toString())
//    }
//
//    @Test
//    fun converting_NbtShort_to_string() {
//        assertEquals("8s", NbtShort(8).toString())
//    }
//
//    @Test
//    fun converting_NbtInt_to_string() {
//        assertEquals("9", NbtInt(9).toString())
//    }
//
//    @Test
//    fun converting_NbtLong_to_string() {
//        assertEquals("10L", NbtLong(10).toString())
//    }
//
//    @Test
//    fun converting_NbtFloat_to_string() {
//        assertEquals("3.1415f", NbtFloat(3.1415f).toString())
//    }
//
//    @Test
//    fun converting_NbtDouble_to_string() {
//        assertEquals("2.71828d", NbtDouble(2.71828).toString())
//    }
//
//    @Test
//    fun converting_NbtByteArray_to_string() {
//        assertEquals("[B;4B,3B,2B,1B]", NbtByteArray(listOf(4, 3, 2, 1)).toString())
//    }
//
//    @Test
//    fun converting_NbtString_to_string() {
//        assertEquals("\"\"", NbtString("").toString())
//        assertEquals("\"hello\"", NbtString("hello").toString())
//        assertEquals("\"\\\"double-quoted\\\"\"", NbtString("\"double-quoted\"").toString())
//        assertEquals("\"'single-quoted'\"", NbtString("'single-quoted'").toString())
//        assertEquals("\"\\\"'multi-quoted'\\\"\"", NbtString("\"'multi-quoted'\"").toString())
//    }
//
//    @Test
//    fun converting_NbtList_to_string() {
//        assertEquals("[]", NbtList(emptyList()).toString())
//        assertEquals("[1b]", NbtList(listOf(NbtByte(1))).toString())
//        assertEquals("[[]]", NbtList(listOf(NbtList(emptyList()))).toString())
//    }
//
//    @Test
//    fun converting_NbtCompound_to_string() {
//        assertEquals("{}", buildNbtCompound { }.toString())
//        assertEquals("{a:1b}", buildNbtCompound { put("a", 1.toByte()) }.toString())
//        assertEquals("{a:1b,b:7}", buildNbtCompound { put("a", 1.toByte()); put("b", 7) }.toString())
//    }
//
//    @Test
//    fun converting_NbtIntArray_to_string() {
//        assertEquals("[I;4,3,2,1]", NbtIntArray(listOf(4, 3, 2, 1)).toString())
//    }
//
//    @Test
//    fun converting_NbtLongArray_to_string() {
//        assertEquals("[L;4L,3L,2L,1L]", NbtLongArray(listOf(4, 3, 2, 1)).toString())
//    }
//}
//
//class NbtTagTestConversions {
//    @Test
//    fun should_convert_correctly_with_properties() {
//        fun <R : NbtTag> assertConversionSucceeds(tag: NbtTag, convert: KProperty1<NbtTag, R>): Unit =
//            assertEquals(tag, convert(tag), "Converting ${tag::class.simpleName} with ${convert.name}")
//
//        fun <R : NbtTag> assertConversionFails(tag: NbtTag, convert: KProperty1<NbtTag, R>): RuntimeException =
//            assertFailsWith<IllegalArgumentException>("Converting ${tag::class.simpleName} with ${convert.name}") {
//                convert(tag)
//            }
//
//        assertConversionSucceeds(NbtByte(1), NbtTag::nbtByte)
//        assertConversionFails(NbtShort(1), NbtTag::nbtByte)
//
//        assertConversionSucceeds(NbtShort(1), NbtTag::nbtShort)
//        assertConversionFails(NbtInt(1), NbtTag::nbtShort)
//
//        assertConversionSucceeds(NbtInt(1), NbtTag::nbtInt)
//        assertConversionFails(NbtLong(1), NbtTag::nbtInt)
//
//        assertConversionSucceeds(NbtLong(1), NbtTag::nbtLong)
//        assertConversionFails(NbtFloat(1.0f), NbtTag::nbtLong)
//
//        assertConversionSucceeds(NbtFloat(1.0f), NbtTag::nbtFloat)
//        assertConversionFails(NbtDouble(1.0), NbtTag::nbtFloat)
//
//        assertConversionSucceeds(NbtDouble(1.0), NbtTag::nbtDouble)
//        assertConversionFails(NbtByteArray(listOf(1)), NbtTag::nbtDouble)
//
//        assertConversionSucceeds(NbtByteArray(listOf(1)), NbtTag::nbtByteArray)
//        assertConversionFails(NbtString("1"), NbtTag::nbtByteArray)
//
//        assertConversionSucceeds(NbtString("1"), NbtTag::nbtString)
//        assertConversionFails(NbtList(emptyList()), NbtTag::nbtString)
//
//        assertConversionSucceeds(NbtList(emptyList()), NbtTag::nbtList)
//        assertConversionFails(NbtCompound(emptyMap()), NbtTag::nbtList)
//
//        assertConversionSucceeds(NbtCompound(emptyMap()), NbtTag::nbtCompound)
//        assertConversionFails(NbtIntArray(listOf()), NbtTag::nbtCompound)
//
//        assertConversionSucceeds(NbtIntArray(listOf()), NbtTag::nbtIntArray)
//        assertConversionFails(NbtLongArray(listOf()), NbtTag::nbtIntArray)
//
//        assertConversionSucceeds(NbtLongArray(listOf()), NbtTag::nbtLongArray)
//        assertConversionFails(NbtByte(1), NbtTag::nbtLongArray)
//    }
//
//    @Test
//    @OptIn(ExperimentalNbtApi::class)
//    fun should_convert_to_typed_NbtList_correctly() {
//        fun <R : NbtTag> assertConversionSucceeds(tag: NbtTag, convert: NbtTag.() -> R): Unit =
//            assertEquals(tag, convert(tag), "Converting ${tag::class.simpleName}")
//
//        fun <R : NbtTag> assertConversionFails(tag: NbtTag, convert: NbtTag.() -> R): RuntimeException =
//            assertFailsWith<IllegalArgumentException>("Converting ${tag::class.simpleName}") {
//                convert(tag)
//            }
//
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtByte>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtShort>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtInt>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtLong>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtFloat>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtDouble>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtByteArray>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtString>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtList<*>>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtCompound>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtIntArray>() }
//        assertConversionSucceeds(NbtList(emptyList())) { nbtList<NbtLongArray>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtByte(1)))) { nbtList<NbtByte>() }
//        assertConversionFails(NbtList(listOf(NbtShort(1)))) { nbtList<NbtByte>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtShort(1)))) { nbtList<NbtShort>() }
//        assertConversionFails(NbtList(listOf(NbtInt(1)))) { nbtList<NbtShort>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtInt(1)))) { nbtList<NbtInt>() }
//        assertConversionFails(NbtList(listOf(NbtLong(1)))) { nbtList<NbtInt>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtLong(1)))) { nbtList<NbtLong>() }
//        assertConversionFails(NbtList(listOf(NbtFloat(1.0f)))) { nbtList<NbtLong>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtFloat(1.0f)))) { nbtList<NbtFloat>() }
//        assertConversionFails(NbtList(listOf(NbtDouble(1.0)))) { nbtList<NbtFloat>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtDouble(1.0)))) { nbtList<NbtDouble>() }
//        assertConversionFails(NbtList(listOf(NbtByteArray(listOf(1))))) { nbtList<NbtDouble>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtByteArray(listOf(1))))) { nbtList<NbtByteArray>() }
//        assertConversionFails(NbtList(listOf(NbtString("1")))) { nbtList<NbtByteArray>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtString("1")))) { nbtList<NbtString>() }
//        assertConversionFails(NbtList(listOf(NbtList(emptyList())))) { nbtList<NbtString>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtList(emptyList())))) { nbtList<NbtList<*>>() }
//        assertConversionFails(NbtList(listOf(NbtCompound(emptyMap())))) { nbtList<NbtList<*>>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtCompound(emptyMap())))) { nbtList<NbtCompound>() }
//        assertConversionFails(NbtList(listOf(NbtIntArray(listOf())))) { nbtList<NbtCompound>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtIntArray(listOf())))) { nbtList<NbtIntArray>() }
//        assertConversionFails(NbtList(listOf(NbtLongArray(listOf())))) { nbtList<NbtIntArray>() }
//
//        assertConversionSucceeds(NbtList(listOf(NbtLongArray(listOf())))) { nbtList<NbtLongArray>() }
//        assertConversionFails(NbtList(listOf(NbtByte(1)))) { nbtList<NbtLongArray>() }
//    }
//}
//
//class NbtByteArrayTest { // TODO no longer valid
//    @Test
//    fun should_equal_List_of_same_contents() {
//        fun assertWith(vararg elements: Byte): Unit =
//            assertEquals(
//                NbtByteArray(elements.asList()),
//                NbtByteArray(elements.copyOf().asList())
//            )
//
//        assertWith()
//        assertWith(1)
//        assertWith(1, 2, 4, 8)
//    }
//
//    @Test
//    fun should_not_equal_NbtTag_of_different_type_but_same_contents() {
//        assertNotEquals<NbtTag>(NbtList(emptyList()), NbtByteArray(listOf()))
//        assertNotEquals<NbtTag>(NbtIntArray(listOf()), NbtByteArray(listOf()))
//        assertNotEquals<NbtTag>(NbtLongArray(listOf()), NbtByteArray(listOf()))
//    }
//}
//
//class NbtListTest {
//    @Test
//    fun should_equal_NbtList_of_same_contents() {
//        @OptIn(UnsafeNbtApi::class)
//        fun assertWith(nbtList: NbtList<*>): Unit =
//            assertEquals(
//                nbtList,
//                NbtList(nbtList.content.toList())
//            )
//
//        assertWith(NbtList(emptyList()))
//        assertWith(NbtList(listOf(NbtInt(1))))
//        assertWith(NbtList(listOf(NbtString("a"), NbtString("b"))))
//
//        assertEquals(NbtList(listOf(NbtInt(1))), NbtList(NbtList(listOf(NbtInt(1))).content))
//    }
//
//    @Test
//    fun should_not_equal_NbtTag_of_different_type_but_same_contents() {
//        assertNotEquals<NbtTag>(NbtByteArray(listOf()), NbtList(emptyList()))
//        assertNotEquals<NbtTag>(NbtIntArray(listOf()), NbtList(emptyList()))
//        assertNotEquals<NbtTag>(NbtLongArray(listOf()), NbtList(emptyList()))
//    }
//}
//
//class NbtCompoundTest {
//    private val arbNbtCompound = Arb.nbtCompound(1)
//
//    @Test
//    fun getting_a_tag_with_a_contained_name_should_return_the_tag_from_content() = runTest {
//        checkAll(arbNbtCompound) { compound ->
//            val names = compound.content.keys
//
//            names.forEach { containedName ->
//                assertSame(compound.content[containedName], compound[containedName])
//            }
//        }
//    }
//
//    @Test
//    fun getting_a_tag_with_a_non_contained_name_should_throw_a_NoSuchElementException() = runTest {
//        checkAll(arbNbtCompound, Arb.string()) { compound, nonContainedName ->
//            assume(nonContainedName !in compound.content)
//
//            assertFailsWith<NoSuchElementException> {
//                compound[nonContainedName]
//            }
//        }
//    }
//
//    @Test
//    fun getting_a_tag_or_null_with_a_contained_name_should_return_the_tag_from_content() = runTest {
//        checkAll(arbNbtCompound) { compound ->
//            val names = compound.content.keys
//
//            names.forEach { containedName ->
//                assertSame(compound.content[containedName], compound[containedName])
//            }
//        }
//    }
//
//    @Test
//    fun getting_a_tag_or_null_with_a_non_contained_name_should_return_null() = runTest {
//        checkAll(arbNbtCompound, Arb.string()) { compound, nonContainedName ->
//            assume(nonContainedName !in compound.content)
//
//            assertNull(compound.getOrNull(nonContainedName))
//        }
//    }
//
//    @Test
//    fun checking_contains_with_a_name_in_the_content_should_return_true() = runTest {
//        checkAll(arbNbtCompound) { compound ->
//            val names = compound.content.keys
//
//            names.forEach { containedName ->
//                assertTrue(containedName in compound)
//            }
//        }
//    }
//
//    @Test
//    fun checking_contains_with_a_name_not_in_the_content_should_return_false() = runTest {
//        checkAll(arbNbtCompound, Arb.string()) { compound, nonContainedName ->
//            assume(nonContainedName !in compound.content)
//
//            assertFalse(nonContainedName in compound)
//        }
//    }
//
//
//    @Test
//    fun should_equal_NbtCompound_of_same_contents() {
//        fun assertWith(vararg elements: Pair<String, NbtTag>): Unit =
//            assertEquals(
//                NbtCompound(elements.toMap()),
//                NbtCompound(elements.copyOf().toMap())
//            )
//
//        assertWith()
//        assertWith("one" to NbtInt(1))
//        assertWith("a" to NbtString("a"), "b" to NbtString("b"))
//
//        assertEquals(NbtList(listOf(NbtInt(1))), NbtList(NbtList(listOf(NbtInt(1))).content))
//    }
//}
//
//class NbtIntArrayTest {
//    @Test
//    fun should_equal_NbtIntArray_of_same_contents() {
//        fun assertWith(vararg elements: Int): Unit =
//            assertEquals(
//                NbtIntArray(elements.asList()),
//                NbtIntArray(elements.copyOf().asList())
//            )
//
//        assertWith()
//        assertWith(1)
//        assertWith(1, 2, 4, 8)
//    }
//
//    @Test
//    fun should_not_equal_NbtTag_of_different_type_but_same_contents() {
//        assertNotEquals<NbtTag>(NbtList(emptyList()), NbtIntArray(listOf()))
//        assertNotEquals<NbtTag>(NbtByteArray(listOf()), NbtIntArray(listOf()))
//        assertNotEquals<NbtTag>(NbtLongArray(listOf()), NbtIntArray(listOf()))
//    }
//}
//
//class NbtLongArrayTest {
//    @Test
//    fun should_equal_List_of_same_contents() {
//        fun assertWith(vararg elements: Long): Unit =
//            assertEquals(
//                NbtLongArray(elements.asList()),
//                NbtLongArray(elements.copyOf().asList())
//            )
//
//        assertWith()
//        assertWith(1)
//        assertWith(1, 2, 4, 8)
//    }
//
//    @Test
//    fun should_not_equal_NbtTag_of_different_type_but_same_contents() {
//        assertNotEquals<NbtTag>(NbtList(emptyList()), NbtLongArray(listOf()))
//        assertNotEquals<NbtTag>(NbtIntArray(listOf()), NbtLongArray(listOf()))
//        assertNotEquals<NbtTag>(NbtByteArray(listOf()), NbtLongArray(listOf()))
//    }
//}
