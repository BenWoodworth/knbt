package net.benwoodworth.knbt.internal

import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.bytes
import io.kotest.property.exhaustive.enum
import io.kotest.property.exhaustive.filter
import kotlinx.coroutines.test.runTest
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NbtTagTypeTest {
    @Test
    fun converting_to_NbtTagType_or_null_from_a_valid_ID_byte_should_return_the_correct_tag() = runTest {
        checkAll(Exhaustive.enum<NbtTagType>()) { expectedType ->
            val id = expectedType.id

            assertEquals(expectedType, id.toNbtTagTypeOrNull())
        }
    }

    @Test
    fun converting_to_NbtTagType_or_null_from_an_invalid_ID_byte_should_return_null() = runTest {
        val validIds = NbtTagType.values().map { it.id }

        val invalidIds = Exhaustive.bytes()
            .filter { it !in validIds }

        invalidIds.checkAll { id ->
            assertNull(id.toNbtTagTypeOrNull())
        }
    }

    @Test
    fun converting_class_Nothing_should_return_TAG_End(): Unit =
        assertEquals(TAG_End, Nothing::class.toNbtTagType())

    @Test
    fun converting_class_NbtByte_should_return_TAG_Int(): Unit =
        assertEquals(TAG_Byte, NbtByte::class.toNbtTagType())

    @Test
    fun converting_class_NbtShort_should_return_TAG_Short(): Unit =
        assertEquals(TAG_Short, NbtShort::class.toNbtTagType())

    @Test
    fun converting_class_NbtInt_should_return_TAG_Int(): Unit =
        assertEquals(TAG_Int, NbtInt::class.toNbtTagType())

    @Test
    fun converting_class_NbtLong_should_return_TAG_Long(): Unit =
        assertEquals(TAG_Long, NbtLong::class.toNbtTagType())

    @Test
    fun converting_class_NbtFloat_should_return_TAG_Float(): Unit =
        assertEquals(TAG_Float, NbtFloat::class.toNbtTagType())

    @Test
    fun converting_class_NbtDouble_should_return_TAG_Double(): Unit =
        assertEquals(TAG_Double, NbtDouble::class.toNbtTagType())

    @Test
    fun converting_class_NbtByteArray_should_return_TAG_Byte_Array(): Unit =
        assertEquals(TAG_Byte_Array, NbtByteArray::class.toNbtTagType())

    @Test
    fun converting_class_NbtString_should_return_TAG_String(): Unit =
        assertEquals(TAG_String, NbtString::class.toNbtTagType())

    @Test
    fun converting_class_NbtList_should_return_TAG_List(): Unit =
        assertEquals(TAG_List, NbtList::class.toNbtTagType())

    @Test
    fun converting_class_NbtCompound_should_return_TAG_Compound(): Unit =
        assertEquals(TAG_Compound, NbtCompound::class.toNbtTagType())

    @Test
    fun converting_class_NbtIntArray_should_return_TAG_Int_Array(): Unit =
        assertEquals(TAG_Int_Array, NbtIntArray::class.toNbtTagType())

    @Test
    fun converting_class_NbtLongArray_should_return_TAG_Long_Array(): Unit =
        assertEquals(TAG_Long_Array, NbtLongArray::class.toNbtTagType())
}
