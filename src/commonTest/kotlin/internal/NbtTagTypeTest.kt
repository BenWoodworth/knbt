package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.test.parameterize.parameterize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NbtTagTypeTest {
    @Test
    fun converting_to_NbtTagType_or_null_from_a_valid_ID_byte_should_return_the_correct_tag() = parameterize {
        val expectedType by parameter { values().asList() }
        val id = expectedType.id

        assertEquals(expectedType, id.toNbtTagTypeOrNull())
    }

    @Test
    fun converting_to_NbtTagType_or_null_from_an_invalid_ID_byte_should_return_null() = parameterize {
        val invalidId by parameter {
            val validIds = values().map { it.id }

            (Byte.MIN_VALUE..Byte.MAX_VALUE)
                .map { it.toByte() }
                .filter { it !in validIds }
        }

        assertNull(invalidId.toNbtTagTypeOrNull())
    }

    @Test
    fun converting_class_to_NbtTag() = parameterize {
        val expectedConversion by parameter {
            listOf(
                Nothing::class to TAG_End,
                NbtByte::class to TAG_Byte,
                NbtShort::class to TAG_Short,
                NbtInt::class to TAG_Int,
                NbtLong::class to TAG_Long,
                NbtFloat::class to TAG_Float,
                NbtDouble::class to TAG_Double,
                NbtByteArray::class to TAG_Byte_Array,
                NbtString::class to TAG_String,
                NbtList::class to TAG_List,
                NbtCompound::class to TAG_Compound,
                NbtIntArray::class to TAG_Int_Array,
                NbtLongArray::class to TAG_Long_Array,
            )
        }

        println(expectedConversion)

        val (kClass, type) = expectedConversion
        assertEquals(type, kClass.toNbtTagType())
    }
}
