package net.benwoodworth.knbt.integration

import kotlinx.serialization.*
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader.*
import net.benwoodworth.knbt.internal.NbtReaderDecoder
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.internal.NbtWriterEncoder
import net.benwoodworth.knbt.internal.TreeNbtReader
import net.benwoodworth.knbt.internal.TreeNbtWriter
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.binaryEquals
import net.benwoodworth.knbt.test.data.bigTestClass
import net.benwoodworth.knbt.test.data.bigTestTag
import net.benwoodworth.knbt.test.data.testClass
import net.benwoodworth.knbt.test.data.testTag
import net.benwoodworth.knbt.test.verify.VerifyingNbtReader
import net.benwoodworth.knbt.test.verify.VerifyingNbtWriter
import kotlin.test.*

class SerializationTest {
    private inline fun <reified T> assertSerializesCorrectly(
        value: T,
        nbtTag: NbtTag,
        noinline valuesEqual: (T, T) -> Boolean = { a, b -> a == b }
    ): Unit =
        assertSerializesCorrectly(serializer(), value, nbtTag, valuesEqual)

    private fun <T> assertSerializesCorrectly(
        serializer: KSerializer<T>,
        value: T,
        nbtTag: NbtTag,
        valuesEqual: (T, T) -> Boolean = { a, b -> a == b }
    ) {
        run { // Serialize Value
            lateinit var actualNbtTag: NbtTag
            val writer = VerifyingNbtWriter(TreeNbtWriter { actualNbtTag = it })
            NbtWriterEncoder(NbtFormat(), writer).encodeSerializableValue(serializer, value)

            writer.assertComplete()
            assertTrue("Serialized value incorrectly. Expected <$nbtTag>, actual <$actualNbtTag>.") {
                actualNbtTag.binaryEquals(nbtTag)
            }
        }

        run {// Deserialize Value
            val reader = VerifyingNbtReader(TreeNbtReader(nbtTag))
            val decoder = NbtReaderDecoder(NbtFormat(), reader)
            val actualValue = decoder.decodeSerializableValue(serializer)

            reader.assertComplete()
            assertTrue("Deserialized value incorrectly. Expected <$value>, actual <$actualValue>.") {
                valuesEqual(actualValue, value)
            }
        }

        run { // Serialize NbtTag
            lateinit var serializedNbtTag: NbtTag
            val writer = VerifyingNbtWriter(TreeNbtWriter { serializedNbtTag = it })
            NbtWriterEncoder(NbtFormat(), writer).encodeSerializableValue(NbtTag.serializer(), nbtTag)

            writer.assertComplete()
            assertTrue("Serialized nbtTag incorrectly. Expected <$nbtTag>, actual <$serializedNbtTag>.") {
                serializedNbtTag.binaryEquals(nbtTag)
            }
        }

        run {// Deserialize NbtTag
            val reader = VerifyingNbtReader(TreeNbtReader(nbtTag))
            val decoder = NbtReaderDecoder(NbtFormat(), reader)
            val deserializedNbtTag = decoder.decodeSerializableValue(NbtTag.serializer())

            reader.assertComplete()
            assertTrue("Deserialized nbtTag incorrectly. Expected <$value>, actual <$deserializedNbtTag>.") {
                deserializedNbtTag.binaryEquals(nbtTag)
            }
        }
    }

    @Test
    fun should_serialize_TestNbt_class_correctly() {
        assertSerializesCorrectly(testClass, testTag)
    }

    @Test
    fun should_serialize_BigTestNbt_class_correctly() {
        assertSerializesCorrectly(bigTestClass, bigTestTag)
    }

    @Test
    fun should_serialize_compound_with_no_entries_to_Map_correctly() {
        assertSerializesCorrectly(mapOf<String, Int>(), buildNbtCompound { })
    }

    @Test
    fun should_serialize_compound_with_one_entry_to_Map_correctly() {
        assertSerializesCorrectly(
            mapOf("property" to 7),
            buildNbtCompound {
                put("property", 7)
            },
        )
    }

    @Test
    fun should_serialize_class_with_one_property_correctly() {
        @Serializable
        @SerialName("OneProperty")
        data class OneProperty(val property: Int)

        assertSerializesCorrectly(
            OneProperty(7),
            buildNbtCompound("OneProperty") {
                put("property", 7)
            },
        )
    }

    @Test
    fun should_serialize_class_with_two_properties_correctly() {
        @Serializable
        @SerialName("TwoProperties")
        data class TwoProperties(val entry1: String, val entry2: Long)

        assertSerializesCorrectly(
            TwoProperties(entry1 = "value1", entry2 = 1234L),
            buildNbtCompound("TwoProperties") {
                put("entry1", "value1")
                put("entry2", 1234L)
            },
        )
    }

    @Test
    fun should_serialize_List_of_Lists_correctly() {
        assertSerializesCorrectly(
            listOf(listOf(1.toByte()), listOf()),
            buildNbtList<NbtList<*>> {
                addNbtList<NbtByte> { add(1.toByte()) }
                addNbtList<Nothing> { }
            },
        )
    }

    @Test
    fun should_serialize_ByteArray_correctly() {
        assertSerializesCorrectly(
            byteArrayOf(1, 2, 3),
            NbtByteArray(byteArrayOf(1, 2, 3)),
            ByteArray::contentEquals
        )
    }

    @Test
    fun should_serialize_IntArray_correctly() {
        assertSerializesCorrectly(
            intArrayOf(1, 2, 3),
            NbtIntArray(intArrayOf(1, 2, 3)),
            IntArray::contentEquals
        )
    }

    @Test
    fun should_serialize_LongArray_correctly() {
        assertSerializesCorrectly(
            longArrayOf(1, 2, 3),
            NbtLongArray(longArrayOf(1, 2, 3)),
            LongArray::contentEquals,
        )
    }

    @Test
    fun should_serialize_Byte_correctly() {
        assertSerializesCorrectly(4.toByte(), NbtByte(4))
    }

    @Test
    fun should_serialize_Short_correctly() {
        assertSerializesCorrectly(5.toShort(), NbtShort(5))
    }

    @Test
    fun should_serialize_Int_correctly() {
        assertSerializesCorrectly(6, NbtInt(6))
    }

    @Test
    fun should_serialize_Long_correctly() {
        assertSerializesCorrectly(7L, NbtLong(7L))
    }

    @Test
    fun should_serialize_Float_correctly() {
        assertSerializesCorrectly(3.14f, NbtFloat(3.14f))
    }

    @Test
    fun should_serialize_Double_correctly() {
        assertSerializesCorrectly(3.14, NbtDouble(3.14))
    }

    @Test
    fun serializing_a_class_should_nest_into_a_compound_with_the_class_serial_name_as_the_key() {
        @Serializable
        @SerialName("RootKey")
        data class MyClass(val property: String)

        assertSerializesCorrectly(
            MyClass("value"),
            buildNbtCompound {
                putNbtCompound("RootKey") {
                    put("property", "value")
                }
            }
        )
    }
}
