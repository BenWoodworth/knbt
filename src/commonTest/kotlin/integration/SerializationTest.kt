package net.benwoodworth.knbt.integration

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.internal.NbtReaderDecoder
import net.benwoodworth.knbt.internal.NbtWriterEncoder
import net.benwoodworth.knbt.internal.TreeNbtReader
import net.benwoodworth.knbt.internal.TreeNbtWriter
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.binaryEquals
import net.benwoodworth.knbt.test.verify.VerifyingNbtReader
import net.benwoodworth.knbt.test.verify.VerifyingNbtWriter
import kotlin.test.assertTrue

abstract class SerializationTest {
    protected inline fun <reified T> assertSerializesCorrectly(
        value: T,
        nbtTag: NbtTag,
        noinline valuesEqual: (T, T) -> Boolean = { a, b -> a == b }
    ): Unit =
        assertSerializesCorrectly(serializer(), value, nbtTag, valuesEqual)

    protected fun <T> assertSerializesCorrectly(
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
}
