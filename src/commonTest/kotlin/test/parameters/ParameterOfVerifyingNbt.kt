package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtFormatBuilder
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.internal.NbtReaderDecoder
import net.benwoodworth.knbt.internal.NbtWriterEncoder
import net.benwoodworth.knbt.test.verify.VerifyingNbtReader
import net.benwoodworth.knbt.test.verify.VerifyingNbtWriter

internal fun ParameterizeScope.parameterOfVerifyingNbt(
    builderAction: NbtFormatBuilder.() -> Unit = {}
) = parameter {
    sequenceOf(
        EncoderVerifyingNbt(
            "Encode NbtTag",
            createWriter = { tag -> VerifyingNbtWriter(tag) },
            builderAction
        ),
        DecoderVerifyingNbt(
            "Decode NbtTag",
            createReader = { tag -> VerifyingNbtReader(tag) },
            builderAction
        ),
        DecoderVerifyingNbt(
            "Decode NbtTag (non-sequentially)",
            createReader = { tag -> VerifyingNbtReader(tag, knownSizes = false) },
            builderAction
        ),
    )
}

internal fun ParameterizeScope.parameterOfEncoderVerifyingNbt(
    builderAction: NbtFormatBuilder.() -> Unit = {}
) = parameter {
    this@parameterOfEncoderVerifyingNbt.parameterOfVerifyingNbt(builderAction)
        .arguments.filterIsInstance<EncoderVerifyingNbt>()
}

internal fun ParameterizeScope.parameterOfDecoderVerifyingNbt(
    builderAction: NbtFormatBuilder.() -> Unit = {}
) = parameter {
    this@parameterOfDecoderVerifyingNbt.parameterOfVerifyingNbt(builderAction)
        .arguments.filterIsInstance<DecoderVerifyingNbt>()
}

internal sealed class VerifyingNbt(
    private val name: String,
    builderAction: NbtFormatBuilder.() -> Unit
) {
    protected val nbt = NbtFormat { builderAction() }

    override fun toString(): String = name

    /**
     * Tests the [NbtWriterEncoder] or the [NbtReaderDecoder].
     *
     * Tests the [NbtWriterEncoder] by encoding the [value] to an equivalent [encodedTag] and verifying
     * that the correct calls are made to its [writer][NbtWriterEncoder.writer].
     *
     * Tests the [NbtReaderDecoder] by decoding a value from the [encodedTag] and verifying that the
     * correct calls are made to its [reader][NbtReaderDecoder.reader], then asserts that the decoded value
     * [equals][Any.equals] the original [value].
     */
    abstract fun <T> verifyEncoderOrDecoder(
        serializer: KSerializer<T>,
        value: T,
        encodedTag: NbtTag,
        testDecodedValue: (value: T, decodedValue: T) -> Unit = { _, _ -> }
    )
}

internal class EncoderVerifyingNbt(
    val name: String,
    private val createWriter: (tag: NbtTag) -> VerifyingNbtWriter,
    builderAction: NbtFormatBuilder.() -> Unit
) : VerifyingNbt(name, builderAction) {
    @Deprecated("Use verifyEncoder()", level = DeprecationLevel.HIDDEN)
    override fun <T> verifyEncoderOrDecoder(
        serializer: KSerializer<T>,
        value: T,
        encodedTag: NbtTag,
        testDecodedValue: (value: T, decodedValue: T) -> Unit
    ) {
        verifyEncoder(serializer, value, encodedTag)
    }

    fun <T> verifyEncoder(serializer: SerializationStrategy<T>, value: T, encodedTag: NbtTag) {
        val writer = createWriter(encodedTag)
        NbtWriterEncoder(nbt, writer).encodeSerializableValue(serializer, value)

        writer.assertComplete()
    }
}

internal class DecoderVerifyingNbt(
    val name: String,
    private val createReader: (tag: NbtTag) -> VerifyingNbtReader,
    builderAction: NbtFormatBuilder.() -> Unit
) : VerifyingNbt(name, builderAction) {
    @Deprecated("Use verifyDecoder()", level = DeprecationLevel.HIDDEN)
    override fun <T> verifyEncoderOrDecoder(
        serializer: KSerializer<T>,
        value: T,
        encodedTag: NbtTag,
        testDecodedValue: (value: T, decodedValue: T) -> Unit
    ) {
        verifyDecoder(
            serializer,
            encodedTag,
            testDecodedValue = { decodedValue ->
                testDecodedValue(value, decodedValue)
            }
        )
    }

    fun <T> verifyDecoder(
        deserializer: DeserializationStrategy<T>,
        encodedTag: NbtTag,
        testDecodedValue: (decodedValue: T) -> Unit = {}
    ) {
        val reader = createReader(encodedTag)
        val decoder = NbtReaderDecoder(nbt, reader)
        val decodedValue = decoder.decodeSerializableValue(deserializer)

        reader.assertComplete()

        testDecodedValue(decodedValue)
    }
}
