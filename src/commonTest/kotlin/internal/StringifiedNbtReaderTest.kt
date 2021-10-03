package net.benwoodworth.knbt.internal

import kotlinx.serialization.builtins.*
import kotlinx.serialization.decodeFromString
import net.benwoodworth.knbt.*
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalNbtApi::class)
class StringifiedNbtReaderTest {
    private fun check(expected: NbtTag, snbt: String) {
        assertEquals(
            expected = expected,
            actual = StringifiedNbt.decodeFromString(NbtTag.serializer(), snbt),
            message = "Parsed \"$snbt\" incorrectly.",
        )

        when (expected) {
            is NbtByte -> assertEquals(expected.value, StringifiedNbt.decodeFromString(Byte.serializer(), snbt))
            is NbtShort -> assertEquals(expected.value, StringifiedNbt.decodeFromString(Short.serializer(), snbt))
            is NbtInt -> assertEquals(expected.value, StringifiedNbt.decodeFromString(Int.serializer(), snbt))
            is NbtLong -> assertEquals(expected.value, StringifiedNbt.decodeFromString(Long.serializer(), snbt))
            is NbtFloat -> assertEquals(expected.value, StringifiedNbt.decodeFromString(Float.serializer(), snbt))
            is NbtDouble -> assertEquals(expected.value, StringifiedNbt.decodeFromString(Double.serializer(), snbt))
            is NbtByteArray -> assertContentEquals(
                expected.toByteArray(),
                StringifiedNbt.decodeFromString(ByteArraySerializer(), snbt)
            )
            is NbtIntArray -> assertContentEquals(
                expected.toIntArray(),
                StringifiedNbt.decodeFromString(IntArraySerializer(), snbt)
            )
            is NbtLongArray -> assertContentEquals(
                expected.toLongArray(),
                StringifiedNbt.decodeFromString(LongArraySerializer(), snbt)
            )
            is NbtString -> assertEquals(expected.value, StringifiedNbt.decodeFromString(String.serializer(), snbt))
            is NbtCompound -> assertEquals(
                expected.toMap(),
                StringifiedNbt.decodeFromString(MapSerializer(String.serializer(), NbtTag.serializer()), snbt)
            )
            is NbtList<*> -> assertEquals(
                expected.toList(),
                StringifiedNbt.decodeFromString(ListSerializer(NbtTag.serializer()), snbt)
            )
            else -> error("Unexpected type: ${expected::class}")
        }
    }

    @Test
    fun Should_read_Byte_correctly() {
        check(NbtByte(0), "0b")
        check(NbtByte(Byte.MIN_VALUE), "${Byte.MIN_VALUE}b")
        check(NbtByte(Byte.MAX_VALUE), "${Byte.MAX_VALUE}b")
        check(NbtByte(0), "false")
        check(NbtByte(1), "true")

        check(NbtByte(0), " 0b ")
    }

    @Test
    fun Should_read_Short_correctly() {
        check(NbtShort(0), "0s")
        check(NbtShort(Short.MIN_VALUE), "${Short.MIN_VALUE}s")
        check(NbtShort(Short.MAX_VALUE), "${Short.MAX_VALUE}s")

        check(NbtShort(0), " 0s ")
    }

    @Test
    fun Should_read_Int_correctly() {
        check(NbtInt(0), "0")
        check(NbtInt(Int.MIN_VALUE), "${Int.MIN_VALUE}")
        check(NbtInt(Int.MAX_VALUE), "${Int.MAX_VALUE}")

        check(NbtInt(0), " 0 ")
    }

    @Test
    fun Should_read_Long_correctly() {
        check(NbtLong(0), "0l")
        check(NbtLong(Long.MIN_VALUE), "${Long.MIN_VALUE}l")
        check(NbtLong(Long.MAX_VALUE), "${Long.MAX_VALUE}l")

        check(NbtLong(0), " 0l ")
    }

    @Test
    fun Should_read_Float_correctly() {
        check(NbtFloat(0.0f), "0f")
        check(NbtFloat(0.1f), "0.1f")
        check(NbtFloat(0.1f), ".1f")
        check(NbtFloat(1.0f), "1.f")
        check(NbtFloat(Float.MIN_VALUE), "${Float.MIN_VALUE}f")
        check(NbtFloat(Float.MAX_VALUE), "${Float.MAX_VALUE}f")
        check(NbtFloat(-Float.MIN_VALUE), "-${Float.MIN_VALUE}f")
        check(NbtFloat(-Float.MAX_VALUE), "-${Float.MAX_VALUE}f")
        check(NbtFloat(1.23e4f), "1.23e4f")
        check(NbtFloat(-56.78e-9f), "-56.78e-9f")

        check(NbtFloat(0f), " 0f ")
    }

    @Test
    fun Should_read_Double_correctly() {
        check(NbtDouble(0.0), "0d")
        check(NbtDouble(0.1), "0.1d")
        check(NbtDouble(0.1), ".1d")
        check(NbtDouble(1.0), "1.d")
        check(NbtDouble(Double.MIN_VALUE), "${Double.MIN_VALUE}d")
        check(NbtDouble(Double.MAX_VALUE), "${Double.MAX_VALUE}d")
        check(NbtDouble(-Double.MIN_VALUE), "-${Double.MIN_VALUE}d")
        check(NbtDouble(-Double.MAX_VALUE), "-${Double.MAX_VALUE}d")
        check(NbtDouble(1.23e4), "1.23e4d")
        check(NbtDouble(-56.78e-9), "-56.78e-9d")

        check(NbtDouble(0.1), "0.1")
        check(NbtDouble(0.1), ".1")
        check(NbtDouble(1.0), "1.")
        check(NbtDouble(Double.MIN_VALUE), "${Double.MIN_VALUE}")
        check(NbtDouble(Double.MAX_VALUE), "${Double.MAX_VALUE}")
        check(NbtDouble(-Double.MIN_VALUE), "-${Double.MIN_VALUE}")
        check(NbtDouble(-Double.MAX_VALUE), "-${Double.MAX_VALUE}")
        check(NbtDouble(1.23e4), "1.23e4")
        check(NbtDouble(-56.78e-9), "-56.78e-9")

        check(NbtDouble(0.0), " .0 ")
    }

    @Test
    fun Should_parse_ByteArray_correctly() {
        check(NbtByteArray(byteArrayOf()), "[B;]")
        check(NbtByteArray(byteArrayOf(1, 2, 3)), "[B; 1b, 2b, 3b]")

        check(NbtByteArray(byteArrayOf(1, 2, 3)), " [ B ; 1b , 2b , 3b ] ")
    }

    @Test
    fun Should_parse_IntArray_correctly() {
        check(NbtIntArray(intArrayOf()), "[I;]")
        check(NbtIntArray(intArrayOf(1, 2, 3)), "[I; 1, 2, 3]")

        check(NbtIntArray(intArrayOf(1, 2, 3)), " [ I ; 1 , 2 , 3 ] ")
    }

    @Test
    fun Should_parse_LongArray_correctly() {
        check(NbtLongArray(longArrayOf()), "[L;]")
        check(NbtLongArray(longArrayOf(1, 2, 3)), "[L; 1l, 2l, 3l]")

        check(NbtLongArray(longArrayOf(1, 2, 3)), " [ L ; 1l , 2l , 3l ] ")
    }

    @Test
    fun Should_parse_String_correctly() {
        check(NbtString(""), "''")
        check(NbtString(""), "\"\"")
        check(NbtString("one"), "one")
        check(NbtString("a1"), "a1")
        check(NbtString("2x"), "2x")
        check(NbtString("2_2"), "2_2")
        check(NbtString("'"), "\"'\"")
        check(NbtString("\""), "'\"'")
        check(NbtString("'"), "'\\''")
        check(NbtString("\""), "\"\\\"\"")
    }

    @Test
    fun Should_parse_List_correctly() {
        check(buildNbtList<Nothing> { }, "[]")
        check(buildNbtList<NbtByte> { add(0) }, "[0b]")
        check(buildNbtList<NbtShort> { add(0) }, "[0s]")
        check(buildNbtList<NbtInt> { add(0) }, "[0]")
        check(buildNbtList<NbtLong> { add(0) }, "[0l]")
        check(buildNbtList<NbtFloat> { add(0f) }, "[0f]")
        check(buildNbtList<NbtDouble> { add(0.0) }, "[0d]")
        check(buildNbtList<NbtByteArray> { add(byteArrayOf()) }, "[[B;]]")
        check(buildNbtList<NbtIntArray> { add(intArrayOf()) }, "[[I;]]")
        check(buildNbtList<NbtLongArray> { add(longArrayOf()) }, "[[L;]]")
        check(buildNbtList<NbtList<*>> { add(buildNbtList<Nothing> { }) }, "[[]]")
        check(buildNbtList { add(buildNbtCompound { }) }, "[{}]")

        check(buildNbtList<NbtList<*>> {
            add(buildNbtList<NbtInt> { add(1) })
            add(buildNbtList<NbtByte> { add(2); add(3) })
        }, " [ [ 1 ] , [ 2b , 3b ] ] ")
    }

    @Test
    fun Should_parse_Compound_correctly() {
        check(buildNbtCompound { }, "{}")
        check(buildNbtCompound { put("one", 1) }, "{one: 1}")
        check(buildNbtCompound { put("", 0) }, "{'': 0}")
        check(buildNbtCompound { put("", 0.toByte()) }, "{\"\": 0b}")

        check(
            buildNbtCompound {
                putNbtCompound("") {
                    put("1234", 1234)
                }
            },
            " { '' : { 1234 : 1234 } } ",
        )
    }

    @Test
    fun Should_fail_on_missing_key() {
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString(NbtTag.serializer(), "{ : value}") }
    }

    @Test
    fun Should_fail_on_missing_value() {
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString(NbtTag.serializer(), "{ key: }") }
    }

    @Test
    fun Should_fail_if_only_whitespace() {
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString(NbtTag.serializer(), "") }
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString(NbtTag.serializer(), "    ") }

        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString<String>("") }
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString<String>("    ") }
    }

    @Test
    fun Should_fail_if_there_is_trailing_data() {
        assertFailsWith<NbtDecodingException> { StringifiedNbt.decodeFromString(NbtTag.serializer(), "{} hi") }
    }
}
