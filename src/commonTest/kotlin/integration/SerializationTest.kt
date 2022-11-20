package net.benwoodworth.knbt.integration

import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.NbtFormat
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
    protected val defaultNbt: NbtFormat = NbtFormat()

    protected inline fun <reified T> NbtFormat.testSerialization(
        value: T,
        nbtTag: NbtTag,
        noinline valuesEqual: (T, T) -> Boolean = { a, b -> a == b }
    ): Unit =
        testSerialization(this.serializersModule.serializer(), value, nbtTag, valuesEqual)

    protected fun <T> NbtFormat.testSerialization(
        serializer: KSerializer<T>,
        value: T,
        nbtTag: NbtTag,
        valuesEqual: (T, T) -> Boolean = { a, b -> a == b }
    ) {
        run { // Serialize Value
            lateinit var actualNbtTag: NbtTag
            val writer = VerifyingNbtWriter(TreeNbtWriter { actualNbtTag = it })
            NbtWriterEncoder(this, writer).encodeSerializableValue(serializer, value)

            writer.assertComplete()
            assertTrue("Serialized value incorrectly. Expected <$nbtTag>, actual <$actualNbtTag>.") {
                actualNbtTag.binaryEquals(nbtTag)
            }
        }

        run { // Deserialize Value
            val reader = VerifyingNbtReader(TreeNbtReader(nbtTag))
            val decoder = NbtReaderDecoder(this, reader)
            val actualValue = decoder.decodeSerializableValue(serializer)

            reader.assertComplete()
            assertTrue("Deserialized value incorrectly. Expected <$value>, actual <$actualValue>.") {
                valuesEqual(actualValue, value)
            }
        }

        run { // Deserialize Value (non-sequentially)
            val reader = VerifyingNbtReader(TreeNbtReader(nbtTag), knownSizes = false)
            val decoder = NbtReaderDecoder(this, reader)
            val actualValue = decoder.decodeSerializableValue(serializer)

            reader.assertComplete()
            assertTrue("Non-sequentially deserialized value incorrectly. Expected <$value>, actual <$actualValue>.") {
                valuesEqual(actualValue, value)
            }
        }

        run { // Serialize NbtTag
            lateinit var serializedNbtTag: NbtTag
            val writer = VerifyingNbtWriter(TreeNbtWriter { serializedNbtTag = it })
            NbtWriterEncoder(this, writer).encodeSerializableValue(NbtTag.serializer(), nbtTag)

            writer.assertComplete()
            assertTrue("Serialized nbtTag incorrectly. Expected <$nbtTag>, actual <$serializedNbtTag>.") {
                serializedNbtTag.binaryEquals(nbtTag)
            }
        }

        run { // Deserialize NbtTag
            val reader = VerifyingNbtReader(TreeNbtReader(nbtTag))
            val decoder = NbtReaderDecoder(this, reader)
            val deserializedNbtTag = decoder.decodeSerializableValue(NbtTag.serializer())

            reader.assertComplete()
            assertTrue("Deserialized nbtTag incorrectly. Expected <$value>, actual <$deserializedNbtTag>.") {
                deserializedNbtTag.binaryEquals(nbtTag)
            }
        }
    }
}
