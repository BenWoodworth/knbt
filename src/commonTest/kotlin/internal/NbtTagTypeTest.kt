package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterOfBytes
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NbtTagTypeTest {
    @Test
    fun converting_to_NbtTagType_or_null_from_a_valid_ID_byte_should_return_the_correct_tag() = parameterizeTest {
        val expectedType by parameter(NbtTagType.entries)
        val id = expectedType.id

        assertEquals(expectedType, id.toNbtTagTypeOrNull())
    }

    @Test
    fun converting_to_NbtTagType_or_null_from_an_invalid_ID_byte_should_return_null() = parameterizeTest {
        val invalidId by parameterOfBytes()
        assume(NbtTagType.entries.none { it.id == invalidId })

        assertNull(invalidId.toNbtTagTypeOrNull())
    }

    @Test
    fun converting_class_should_return_the_correct_tag_type() = parameterizeTest {
        fun NbtTagType.nbtTagClass() = when (this) {
            NbtTagType.TAG_End -> Nothing::class
            NbtTagType.TAG_Byte -> NbtByte::class
            NbtTagType.TAG_Short -> NbtShort::class
            NbtTagType.TAG_Int -> NbtInt::class
            NbtTagType.TAG_Long -> NbtLong::class
            NbtTagType.TAG_Float -> NbtFloat::class
            NbtTagType.TAG_Double -> NbtDouble::class
            NbtTagType.TAG_Byte_Array -> NbtByteArray::class
            NbtTagType.TAG_String -> NbtString::class
            NbtTagType.TAG_List -> NbtList::class
            NbtTagType.TAG_Compound -> NbtCompound::class
            NbtTagType.TAG_Int_Array -> NbtIntArray::class
            NbtTagType.TAG_Long_Array -> NbtLongArray::class
        }

        val expectedConversions = NbtTagType.entries.asSequence()
            .map { it.nbtTagClass() to it }

        val expectedConversion by parameter(expectedConversions)
        val (nbtTagClass, nbtTagType) = expectedConversion

        assertEquals(nbtTagType, nbtTagClass.toNbtTagType())
    }
}
