package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtFormatBuilder
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.internal.*
import net.benwoodworth.knbt.test.verify.VerifyingNbtReader
import net.benwoodworth.knbt.test.verify.VerifyingNbtWriter
import kotlin.test.fail

internal fun ParameterizeScope.parameterOfVerifyingNbt(
    includeNamedRootNbt: Boolean = false, // TODO Temporary, since most tests won't support naming until it's redesigned
    builderAction: NbtFormatBuilder.() -> Unit = {}
) = parameter {
    sequenceOf(
        EncoderVerifyingNbt(
            "Encode NbtTag",
            createWriter = { tag -> VerifyingNbtWriter(tag) },
            NbtCapabilities(namedRoot = false),
            builderAction
        ),
        DecoderVerifyingNbt(
            "Decode NbtTag",
            createReader = { tag -> VerifyingNbtReader(tag) },
            NbtCapabilities(namedRoot = false),
            builderAction
        ),
        DecoderVerifyingNbt(
            "Decode NbtTag (non-sequentially)",
            createReader = { tag -> VerifyingNbtReader(tag, knownSizes = false) },
            NbtCapabilities(namedRoot = false),
            builderAction
        ),
        EncoderVerifyingNbt(
            "Encode Named NbtTag",
            createWriter = { tag -> VerifyingNbtWriter(tag) },
            NbtCapabilities(namedRoot = true),
            builderAction
        ),
        DecoderVerifyingNbt(
            "Decode Named NbtTag",
            createReader = { tag -> VerifyingNbtReader(tag) },
            NbtCapabilities(namedRoot = true),
            builderAction
        ),
    ).filter { nbt ->
        !nbt.capabilities.namedRoot || includeNamedRootNbt
    }
}

internal fun ParameterizeScope.parameterOfEncoderVerifyingNbt(
    includeNamedRootNbt: Boolean = false, // TODO Temporary, since most tests won't support naming until it's redesigned
    builderAction: NbtFormatBuilder.() -> Unit = {}
) = parameter {
    this@parameterOfEncoderVerifyingNbt.parameterOfVerifyingNbt(includeNamedRootNbt, builderAction)
        .arguments.filterIsInstance<EncoderVerifyingNbt>()
}

internal fun ParameterizeScope.parameterOfDecoderVerifyingNbt(
    includeNamedRootNbt: Boolean = false, // TODO Temporary, since most tests won't support naming until it's redesigned
    builderAction: NbtFormatBuilder.() -> Unit = {}
) = parameter {
    this@parameterOfDecoderVerifyingNbt.parameterOfVerifyingNbt(includeNamedRootNbt, builderAction)
        .arguments.filterIsInstance<DecoderVerifyingNbt>()
}

internal sealed class VerifyingNbt(
    private val name: String,
    val capabilities: NbtCapabilities,
    builderAction: NbtFormatBuilder.() -> Unit
) {
    protected val nbt = NbtFormat { builderAction() }
        .let { NbtFormat(name, it.configuration, it.serializersModule, capabilities) }

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
    capabilities: NbtCapabilities,
    builderAction: NbtFormatBuilder.() -> Unit
) : VerifyingNbt(name, capabilities, builderAction) {
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
        try {
            val context = SerializationNbtContext()
            val writer = createWriter(encodedTag)
            val encoder = NbtWriterEncoder(nbt, context, writer)

            encoder.encodeSerializableValue(serializer, value)
            writer.assertComplete()
        } catch (e: NbtDecodingException) {
            fail("Encoding should not result in an NbtDecodingException", e)
        }
    }
}

internal class DecoderVerifyingNbt(
    val name: String,
    private val createReader: (tag: NbtTag) -> VerifyingNbtReader,
    capabilities: NbtCapabilities,
    builderAction: NbtFormatBuilder.() -> Unit
) : VerifyingNbt(name, capabilities, builderAction) {
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
        try {
            val context = SerializationNbtContext()
            val reader = createReader(encodedTag)
            val decoder = NbtReaderDecoder(nbt, context, reader)
            val decodedValue = decoder.decodeSerializableValue(deserializer)

            reader.assertComplete()

            testDecodedValue(decodedValue)
        } catch (e: NbtEncodingException) {
            fail("Decoding should not result in an NbtEncodingException", e)
        }
    }
}
