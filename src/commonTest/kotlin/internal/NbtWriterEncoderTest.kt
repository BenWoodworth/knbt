package net.benwoodworth.knbt.internal

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.withClue
import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.binaryEquals
import net.benwoodworth.knbt.test.data.bigTestClass
import net.benwoodworth.knbt.test.data.bigTestTag
import net.benwoodworth.knbt.test.data.testClass
import net.benwoodworth.knbt.test.data.testTag
import net.benwoodworth.knbt.test.verify.VerifyingNbtWriter
import kotlin.test.Test

class NbtWriterEncoderTest {
    private inline fun <reified T> assertEncodesCorrectly(expectedNbtTag: NbtTag, value: T) {
        lateinit var actualNbtTag: NbtTag

        val writer = VerifyingNbtWriter(TreeNbtWriter { actualNbtTag = it })
        NbtWriterEncoder(NbtFormat(), writer).encodeSerializableValue(serializer(), value)
        writer.assertComplete()

        withClue("$actualNbtTag should binary equal $expectedNbtTag") {
            actualNbtTag.binaryEquals(expectedNbtTag)
        }
    }

    @Test
    fun encoding_TestNbt_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = testTag,
            value = testClass,
        )
    }

    @Test
    fun encoding_BigTestNbt_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = bigTestTag,
            value = bigTestClass,
        )
    }

    @Test
    fun encoding_compound_with_no_entries_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = buildNbtCompound {},
            value = mapOf<String, Int>(),
        )
    }

    @Test
    fun encoding_compound_with_one_entry_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = buildNbtCompound {
                put("property", 7)
            },
            value = mapOf("property" to 7),
        )
    }

    @Test
    fun encoding_compound_with_two_entries_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = buildNbtCompound {
                put("entry1", "value1")
                put("entry2", 1234L)
            },
            value = buildMap {
                put("entry1", NbtString("value1"))
                put("entry2", NbtLong(1234L))
            }
        )
    }

    @Test
    fun encoding_List_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = buildNbtList<NbtList<*>> {
                add(NbtList(listOf(NbtByte(1))))
                add(NbtList(emptyList<NbtInt>()))
            },
            value = listOf(listOf(1.toByte()), listOf()),
        )
    }

    @Test
    fun encoding_ByteArray_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = NbtByteArray(byteArrayOf(1, 2, 3)),
            value = byteArrayOf(1, 2, 3),
        )
    }

    @Test
    fun encoding_IntArray_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = NbtIntArray(intArrayOf(1, 2, 3)),
            value = intArrayOf(1, 2, 3),
        )
    }

    @Test
    fun encoding_LongArray_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = NbtLongArray(longArrayOf(1, 2, 3)),
            value = longArrayOf(1, 2, 3),
        )
    }

    @Test
    fun encoding_Byte_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = NbtByte(4),
            value = 4.toByte(),
        )
    }

    @Test
    fun encoding_Short_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = NbtShort(5),
            value = 5.toShort(),
        )
    }

    @Test
    fun encoding_Int_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = NbtInt(6),
            value = 6,
        )
    }

    @Test
    fun encoding_Long_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = NbtLong(7L),
            value = 7L,
        )
    }

    @Test
    fun encoding_Float_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = NbtFloat(3.14f),
            value = 3.14f,
        )
    }

    @Test
    fun encoding_Double_should_write_correctly() {
        assertEncodesCorrectly(
            expectedNbtTag = NbtDouble(3.14),
            value = 3.14,
        )
    }

    @Test
    fun encoding_a_class_should_nest_into_a_compound_with_the_class_serial_name_as_the_key() {
        @Serializable
        @SerialName("RootKey")
        data class MyClass(val property: String)

        val myClass = MyClass("value")

        lateinit var encodedTag: NbtTag
        val encoder = NbtWriterEncoder(
            NbtFormat(),
            TreeNbtWriter { encodedTag = it }
        )

        encoder.encodeSerializableValue(MyClass.serializer(), myClass)

        val actualTag = encodedTag
        actualTag.shouldBeInstanceOf<NbtCompound>()
        actualTag shouldHaveSize 1
        actualTag shouldHaveKey "RootKey"
    }
}
