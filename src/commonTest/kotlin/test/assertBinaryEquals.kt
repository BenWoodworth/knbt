package net.benwoodworth.knbt.test

import net.benwoodworth.knbt.*
import kotlin.test.assertEquals

fun assertBinaryEquals(expected: NbtTag, actual: NbtTag): Unit =
    assertBinaryEquals(expected, actual, "$")

private fun assertBinaryEquals(expected: NbtTag, actual: NbtTag, path: String) {
    assertEquals(expected.type, actual.type, "Mismatched tag type at $path")

    when (expected) {
        is NbtByte -> assertEquals(expected, actual.nbtByte, "At $path")
        is NbtShort -> assertEquals(expected, actual.nbtShort, "At $path")
        is NbtInt -> assertEquals(expected, actual.nbtInt, "At $path")
        is NbtLong -> assertEquals(expected, actual.nbtLong, "At $path")
        is NbtFloat -> {
            val expectedString = expected.toString()
            val actualString = (actual as NbtFloat).toString()

            if (expectedString == actualString) {
                val expectedBinary = expected.value.toRawBits().toString(2).padStart(64, '0')
                val actualBinary = actual.value.toRawBits().toString(2).padStart(64, '0')

                assertEquals("$expected ($expectedBinary)", "$actual ($actualBinary)", "At $path")
            } else {
                assertEquals(expectedString, actualString, "At $path")
            }
        }

        is NbtDouble -> {
            val expectedString = expected.toString()
            val actualString = (actual as NbtDouble).toString()

            if (expectedString == actualString) {
                val expectedBinary = expected.value.toRawBits().toString(2).padStart(128, '0')
                val actualBinary = actual.value.toRawBits().toString(2).padStart(128, '0')

                assertEquals("$expected ($expectedBinary)", "$actual ($actualBinary)", "At $path")
            } else {
                assertEquals(expectedString, actualString, "At $path")
            }
        }

        is NbtString -> assertEquals(expected, actual.nbtString, "At $path")
        is NbtCompound -> {
            assertEquals(expected.size, (actual as NbtCompound).size, "TAG_Compound size at $path")
            expected.content.entries.zip(actual.content.entries) { expectedEntry, actualEntry ->
                assertEquals(expectedEntry.key, actualEntry.key, "TAG_Compound entry name at $path")
                assertBinaryEquals(expectedEntry.value, actualEntry.value, "$path[${expectedEntry.key}]")
            }
        }

        is NbtList<*> -> {
            assertEquals(expected.elementType, (actual as NbtList<*>).elementType, "TAG_List element type at $path")
            assertEquals(expected.size, actual.size, "TAG_List size at $path")
            for (i in expected.content.indices) {
                assertBinaryEquals(expected[i], actual[i], "$path[$i]")
            }
        }

        is NbtByteArray -> {
            assertEquals(expected.size, (actual as NbtByteArray).size, "TAG_Byte_Array size at $path")
            for (i in expected.content.indices) {
                assertEquals(expected[i], actual[i], "TAG_Byte_Array element at $path[$i]")
            }
        }

        is NbtIntArray -> {
            assertEquals(expected.size, (actual as NbtIntArray).size, "TAG_Int_Array size at $path")
            for (i in expected.content.indices) {
                assertEquals(expected[i], actual[i], "TAG_Int_Array element at $path[$i]")
            }
        }

        is NbtLongArray -> {
            assertEquals(expected.size, (actual as NbtLongArray).size, "TAG_Long_Array size at $path")
            for (i in expected.content.indices) {
                assertEquals(expected[i], actual[i], "TAG_Long_Array element at $path[$i]")
            }
        }
    }
}
