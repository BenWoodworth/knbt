package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.decodeFromString
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class StringifiedNbtReaderTest {
    private data class DecodeTestCase(val expected: NbtTag, val snbt: String) {
        override fun toString(): String = snbt
    }

    private fun ParameterizeScope.parameterOfDecodeTestCases(
        vararg testCases: Pair<String, NbtTag>
    ) = parameter {
        testCases.asSequence().map { (snbt, expected) ->
            DecodeTestCase(expected, snbt)
        }
    }

    @Test
    fun should_read_Byte_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "0b" to NbtByte(0),
            "${Byte.MIN_VALUE}b" to NbtByte(Byte.MIN_VALUE),
            "${Byte.MAX_VALUE}b" to NbtByte(Byte.MAX_VALUE),
            "false" to NbtByte(0),
            "true" to NbtByte(1),

            " 0b " to NbtByte(0),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_read_Short_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "0s" to NbtShort(0),
            "${Short.MIN_VALUE}s" to NbtShort(Short.MIN_VALUE),
            "${Short.MAX_VALUE}s" to NbtShort(Short.MAX_VALUE),

            " 0s " to NbtShort(0),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_read_Int_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "0" to NbtInt(0),
            "${Int.MIN_VALUE}" to NbtInt(Int.MIN_VALUE),
            "${Int.MAX_VALUE}" to NbtInt(Int.MAX_VALUE),

            " 0 " to NbtInt(0),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_read_Long_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "0l" to NbtLong(0),
            "${Long.MIN_VALUE}l" to NbtLong(Long.MIN_VALUE),
            "${Long.MAX_VALUE}l" to NbtLong(Long.MAX_VALUE),

            " 0l " to NbtLong(0),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_read_Float_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "0f" to NbtFloat(0.0f),
            "0.1f" to NbtFloat(0.1f),
            ".1f" to NbtFloat(0.1f),
            "1.f" to NbtFloat(1.0f),
            "${Float.MIN_VALUE}f" to NbtFloat(Float.MIN_VALUE),
            "${Float.MAX_VALUE}f" to NbtFloat(Float.MAX_VALUE),
            "-${Float.MIN_VALUE}f" to NbtFloat(-Float.MIN_VALUE),
            "-${Float.MAX_VALUE}f" to NbtFloat(-Float.MAX_VALUE),
            "1.23e4f" to NbtFloat(1.23e4f),
            "-56.78e-9f" to NbtFloat(-56.78e-9f),

            " 0f " to NbtFloat(0f),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_read_Double_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "0d" to NbtDouble(0.0),
            "0.1d" to NbtDouble(0.1),
            ".1d" to NbtDouble(0.1),
            "1.d" to NbtDouble(1.0),
            "${Double.MIN_VALUE}d" to NbtDouble(Double.MIN_VALUE),
            "${Double.MAX_VALUE}d" to NbtDouble(Double.MAX_VALUE),
            "-${Double.MIN_VALUE}d" to NbtDouble(-Double.MIN_VALUE),
            "-${Double.MAX_VALUE}d" to NbtDouble(-Double.MAX_VALUE),
            "1.23e4d" to NbtDouble(1.23e4),
            "-56.78e-9d" to NbtDouble(-56.78e-9),

            "0.1" to NbtDouble(0.1),
            ".1" to NbtDouble(0.1),
            "1." to NbtDouble(1.0),
            "${Double.MIN_VALUE}" to NbtDouble(Double.MIN_VALUE),
            "${Double.MAX_VALUE}" to NbtDouble(Double.MAX_VALUE),
            "-${Double.MIN_VALUE}" to NbtDouble(-Double.MIN_VALUE),
            "-${Double.MAX_VALUE}" to NbtDouble(-Double.MAX_VALUE),
            "1.23e4" to NbtDouble(1.23e4),
            "-56.78e-9" to NbtDouble(-56.78e-9),

            " .0 " to NbtDouble(0.0),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_parse_ByteArray_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "[B;]" to NbtByteArray(listOf()),
            "[B; 1b , 2b, 3b]" to NbtByteArray(listOf(1, 2, 3)),

            " [ B ; 1b , 2b , 3b ] " to NbtByteArray(listOf(1, 2, 3)),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_parse_IntArray_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "[I;]" to NbtIntArray(listOf()),
            "[I; 1, 2, 3]" to NbtIntArray(listOf(1, 2, 3)),

            " [ I ; 1 , 2 , 3 ] " to NbtIntArray(listOf(1, 2, 3)),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_parse_LongArray_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "[L;]" to NbtLongArray(listOf()),
            "[L; 1l, 2l, 3l]" to NbtLongArray(listOf(1, 2, 3)),

            " [ L ; 1l , 2l , 3l ] " to NbtLongArray(listOf(1, 2, 3)),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_parse_String_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "''" to NbtString(""),
            "\"\"" to NbtString(""),
            "one" to NbtString("one"),
            "a1" to NbtString("a1"),
            "2x" to NbtString("2x"),
            "2_2" to NbtString("2_2"),
            "\"'\"" to NbtString("'"),
            "'\"'" to NbtString("\""),
            "'\\''" to NbtString("'"),
            "\"\\\"\"" to NbtString("\""),
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_parse_List_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "[]" to buildNbtList<Nothing> { },
            "[0b]" to buildNbtList<NbtByte> { add(0) },
            "[0s]" to buildNbtList<NbtShort> { add(0) },
            "[0]" to buildNbtList<NbtInt> { add(0) },
            "[0l]" to buildNbtList<NbtLong> { add(0) },
            "[0f]" to buildNbtList<NbtFloat> { add(0f) },
            "[0d]" to buildNbtList<NbtDouble> { add(0.0) },
            "[[B;]]" to buildNbtList<NbtByteArray> { add(byteArrayOf()) },
            "[[I;]]" to buildNbtList<NbtIntArray> { add(intArrayOf()) },
            "[[L;]]" to buildNbtList<NbtLongArray> { add(longArrayOf()) },
            "[[]]" to buildNbtList<NbtList<*>> { add(buildNbtList<Nothing> { }) },
            "[{}]" to buildNbtList { add(buildNbtCompound { }) },

            " [ [ 1 ] , [ 2b , 3b ] ] " to buildNbtList<NbtList<*>> {
                add(buildNbtList<NbtInt> { add(1) })
                add(buildNbtList<NbtByte> { add(2); add(3) })
            }
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_parse_Compound_correctly() = parameterizeTest {
        val testCase by parameterOfDecodeTestCases(
            "{}" to buildNbtCompound { },
            "{one: 1}" to buildNbtCompound { put("one", 1) },
            "{'': 0}" to buildNbtCompound { put("", 0) },
            "{\"\": 0b}" to buildNbtCompound { put("", 0.toByte()) },

            " { '' : { 1234 : 1234 } } " to buildNbtCompound {
                putNbtCompound("") {
                    put("1234", 1234)
                }
            },
        )

        assertEquals(testCase.expected, StringifiedNbt.decodeFromString(testCase.snbt))
    }

    @Test
    fun should_fail_on_missing_key() {
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString<NbtTag>("{ : value}") }
    }

    @Test
    fun should_fail_on_missing_value() {
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString<NbtTag>("{ key: }") }
    }

    @Test
    fun should_fail_if_only_whitespace() {
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString<NbtTag>("") }
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString<NbtTag>("    ") }

        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString<String>("") }
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString<String>("    ") }
    }

    @Test
    fun should_fail_if_there_is_trailing_data() {
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString<NbtTag>("{} hi") }
    }
}
