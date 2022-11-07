package net.benwoodworth.knbt.internal

import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import kotlin.test.Test

class NbtTagTypeTest {
    @Test
    fun converting_from_NbtTag_class_should_return_the_correct_type() = table(
        headers("Class", "Expected Type"),

        row(Nothing::class, TAG_End),
        row(NbtByte::class, TAG_Byte),
        row(NbtShort::class, TAG_Short),
        row(NbtInt::class, TAG_Int),
        row(NbtLong::class, TAG_Long),
        row(NbtFloat::class, TAG_Float),
        row(NbtDouble::class, TAG_Double),
        row(NbtByteArray::class, TAG_Byte_Array),
        row(NbtString::class, TAG_String),
        row(NbtList::class, TAG_List),
        row(NbtCompound::class, TAG_Compound),
        row(NbtIntArray::class, TAG_Int_Array),
        row(NbtLongArray::class, TAG_Long_Array),
    ).forAll { nbtTagClass, expectedType ->
        nbtTagClass.toNbtTagType() shouldBe expectedType
    }
}
