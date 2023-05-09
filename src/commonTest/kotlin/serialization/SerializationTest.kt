package net.benwoodworth.knbt.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.NbtEncodingException
import net.benwoodworth.knbt.internal.NbtReaderDecoder
import net.benwoodworth.knbt.internal.NbtWriterEncoder
import net.benwoodworth.knbt.test.CompareBy
import net.benwoodworth.knbt.test.NbtFormat
import net.benwoodworth.knbt.test.assertEquals
import net.benwoodworth.knbt.test.compareByBinary
import net.benwoodworth.knbt.test.verify.VerifyingNbtReader
import net.benwoodworth.knbt.test.verify.VerifyingNbtWriter
import kotlin.test.assertFailsWith

abstract class SerializationTest {
    protected data class TestCase<T>(
        val serializer: KSerializer<T>,
        val value: T,
        val nbtTag: NbtTag,
        val compareBy: CompareBy<T> = CompareBy.Self
    )

    protected val defaultNbt: NbtFormat = NbtFormat()

    protected inline fun <reified T> NbtFormat.testSerialization(
        value: T,
        nbtTag: NbtTag,
        compareBy: CompareBy<T> = CompareBy.Self
    ): Unit =
        testSerialization(this.serializersModule.serializer(), value, nbtTag, compareBy)

    protected fun <T> NbtFormat.testSerialization(
        serializer: KSerializer<T>,
        value: T,
        nbtTag: NbtTag,
        compareBy: CompareBy<T> = CompareBy.Self
    ) {
        testEncoding(serializer, value, nbtTag)
        testDecoding(serializer, nbtTag, value, compareBy)
    }

    protected fun <T> NbtFormat.testSerialization(testCase: TestCase<T>) {
        testEncoding(testCase.serializer, testCase.value, testCase.nbtTag)
        testDecoding(testCase.serializer, testCase.nbtTag, testCase.value, testCase.compareBy)
    }


    protected inline fun <reified T> NbtFormat.testEncoding(
        value: T,
        nbtTag: NbtTag
    ): Unit =
        testEncoding(this.serializersModule.serializer(), value, nbtTag)

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


    protected inline fun <reified T> NbtFormat.testDecoding(
        nbtTag: NbtTag,
        value: T,
        compareBy: CompareBy<T> = CompareBy.Self
    ): Unit =
        testDecoding(this.serializersModule.serializer(), nbtTag, value, compareBy)

    protected fun <T> NbtFormat.testDecoding(
        serializer: KSerializer<T>,
        nbtTag: NbtTag,
        value: T,
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


    protected fun <T> NbtFormat.testSerializationForNbtException(
        serializer: KSerializer<T>,
        value: T,
        nbtTag: NbtTag,
        failureAssertions: (failure: SerializationException) -> Unit = {}
    ) {
        run { // Serialize Value
            val failure = assertFailsWith<NbtEncodingException> { // Serialize Value
                val writer = VerifyingNbtWriter(nbtTag)
                NbtWriterEncoder(this, writer).encodeSerializableValue(serializer, value)

                writer.assertComplete()
            }
            failureAssertions(failure)
        }

        run { // Deserialize Value
            val failure = assertFailsWith<NbtDecodingException> { // Deserialize Value
                val reader = VerifyingNbtReader(nbtTag)
                val decoder = NbtReaderDecoder(this, reader)
                decoder.decodeSerializableValue(serializer)

                reader.assertComplete()
            }
            failureAssertions(failure)
        }
    }

    protected fun <T> NbtFormat.testSerializationForNbtException(
        testCase: TestCase<T>,
        failureAssertions: (failure: SerializationException) -> Unit = {}
    ): Unit =
        testSerializationForNbtException(testCase.serializer, testCase.value, testCase.nbtTag, failureAssertions)
}
