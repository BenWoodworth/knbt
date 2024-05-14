package net.benwoodworth.knbt.serialization

import kotlinx.serialization.KSerializer
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.internal.NbtReaderDecoder
import net.benwoodworth.knbt.internal.NbtWriterEncoder
import net.benwoodworth.knbt.test.CompareBy
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.assertEquals
import net.benwoodworth.knbt.test.compareByBinary
import net.benwoodworth.knbt.test.verify.VerifyingNbtReader
import net.benwoodworth.knbt.test.verify.VerifyingNbtWriter

abstract class SerializationTest {
    protected val defaultNbt: NbtFormat = NbtFormat()

    protected fun <T> NbtFormat.testSerialization(
        serializer: KSerializer<T>,
        value: T,
        nbtTag: NbtTag,
        compareBy: CompareBy<T> = CompareBy.Self
    ) {
        testEncoding(serializer, value, nbtTag)
        testDecoding(serializer, value, nbtTag, compareBy)
    }

    protected fun <T> NbtFormat.testEncoding(
        serializer: KSerializer<T>,
        value: T,
        nbtTag: NbtTag
    ) {
        run { // Serialize Value
            val writer = VerifyingNbtWriter(nbtTag)
            NbtWriterEncoder(this, writer).encodeSerializableValue(serializer, value)

            writer.assertComplete()
        }

        run { // Serialize NbtTag
            val writer = VerifyingNbtWriter(nbtTag)
            NbtWriterEncoder(this, writer).encodeSerializableValue(NbtTag.serializer(), nbtTag)

            writer.assertComplete()
        }
    }

    protected fun <T> NbtFormat.testDecoding(
        serializer: KSerializer<T>,
        value: T,
        nbtTag: NbtTag,
        compareBy: CompareBy<T> = CompareBy.Self
    ) {
        run { // Deserialize Value
            val reader = VerifyingNbtReader(nbtTag)
            val decoder = NbtReaderDecoder(this, reader)
            val actualValue = decoder.decodeSerializableValue(serializer)

            reader.assertComplete()
            compareBy.assertEquals(value, actualValue, "Deserialized value incorrectly")
        }

        run { // Deserialize Value (non-sequentially)
            val reader = VerifyingNbtReader(nbtTag, knownSizes = false)
            val decoder = NbtReaderDecoder(this, reader)
            val actualValue = decoder.decodeSerializableValue(serializer)

            reader.assertComplete()
            compareBy.assertEquals(value, actualValue, "Non-sequentially deserialized value incorrectly")
        }

        run { // Deserialize NbtTag
            val reader = VerifyingNbtReader(nbtTag)
            val decoder = NbtReaderDecoder(this, reader)
            val deserializedNbtTag = decoder.decodeSerializableValue(NbtTag.serializer())

            reader.assertComplete()
            NbtTag.compareByBinary().assertEquals(nbtTag, deserializedNbtTag, "Deserialized nbtTag incorrectly")
        }
    }
}