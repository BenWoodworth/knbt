package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfBytes
import net.benwoodworth.knbt.test.reportedAs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NbtTagTypeTest {
    @Test
    fun converting_to_NbtType_or_null_from_a_valid_ID_byte_should_return_the_correct_tag() = parameterizeTest {
        val expectedType by parameter(NbtType.entries)
        val id = expectedType.id

        assertEquals(expectedType, id.toNbtTypeOrNull())
    }

    @Test
    fun converting_to_NbtType_or_null_from_an_invalid_ID_byte_should_return_null() = parameterizeTest {
        val invalidId by parameterOfBytes()
        assume(NbtType.entries.none { it.id == invalidId })

        assertNull(invalidId.toNbtTypeOrNull())
    }

    @Test
    fun converting_class_should_return_the_correct_tag_type() = parameterizeTest {
        fun NbtType.nbtTagClass() = when (this) {
            NbtType.TAG_End -> Nothing::class
            NbtType.TAG_Byte -> NbtByte::class
            NbtType.TAG_Short -> NbtShort::class
            NbtType.TAG_Int -> NbtInt::class
            NbtType.TAG_Long -> NbtLong::class
            NbtType.TAG_Float -> NbtFloat::class
            NbtType.TAG_Double -> NbtDouble::class
            NbtType.TAG_Byte_Array -> NbtByteArray::class
            NbtType.TAG_String -> NbtString::class
            NbtType.TAG_List -> NbtList::class
            NbtType.TAG_Compound -> NbtCompound::class
            NbtType.TAG_Int_Array -> NbtIntArray::class
            NbtType.TAG_Long_Array -> NbtLongArray::class
        }

        val expectedConversions = NbtType.entries.asSequence()
            .map { it.nbtTagClass() to it }

        val expectedConversion by parameter(expectedConversions)
            .reportedAs(this, "class") { it.first }

        val (nbtTagClass, nbtType) = expectedConversion

        assertEquals(nbtType, nbtTagClass.toNbtType())
    }
}
