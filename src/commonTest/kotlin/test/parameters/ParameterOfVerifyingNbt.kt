package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.*
import net.benwoodworth.knbt.test.verify.VerifyingNbtReader
import net.benwoodworth.knbt.test.verify.VerifyingNbtWriter
import kotlin.test.fail

internal fun ParameterizeScope.parameterOfVerifyingNbt(
    builderAction: NbtFormatBuilder.() -> Unit = {}
) = parameter {
    val baseCapabilities = NbtCapabilities(
        namedRoot = false,
        definiteLengthEncoding = false,
        rootTagTypes = NbtTypeSet(NbtType.entries),
    )

    sequenceOf(
        EncoderVerifyingNbt(
            "Encode NbtTag",
            baseCapabilities,
            builderAction
        ),
        DecoderVerifyingNbt(
            "Decode NbtTag",
            baseCapabilities,
            builderAction
        ),
        DecoderVerifyingNbt(
            "Decode NbtTag (non-sequentially)",
            baseCapabilities.copy(definiteLengthEncoding = true),
            builderAction
        ),
        EncoderVerifyingNbt(
            "Encode Named NbtTag",
            baseCapabilities.copy(namedRoot = true),
            builderAction
        ),
        DecoderVerifyingNbt(
            "Decode Named NbtTag",
            baseCapabilities.copy(namedRoot = true),
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
    builderAction: NbtFormatBuilder.() -> Unit
) : NbtFormat() {
    private val baseNbt = Nbt { builderAction() }
    override val serializersModule get() = baseNbt.serializersModule
    override val configuration get() = baseNbt.configuration

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
        encodedTag: NbtNamed<NbtTag>?,
        testDecodedValue: (value: T, decodedValue: T) -> Unit = { _, _ -> }
    )

    fun <T> verifyEncoderOrDecoder(
        serializer: KSerializer<T>,
        value: T,
        encodedTag: NbtTag?,
        testDecodedValue: (value: T, decodedValue: T) -> Unit = { _, _ -> }
    ) {
        // TODO non-empty serializer NbtName?
        val namedTag = encodedTag?.let { NbtNamed("", it) }

        verifyEncoderOrDecoder(serializer, value, namedTag, testDecodedValue)
    }
}

internal class EncoderVerifyingNbt(
    override val name: String,
    override val capabilities: NbtCapabilities,
    builderAction: NbtFormatBuilder.() -> Unit
) : VerifyingNbt(builderAction) {
    @Deprecated("Use verifyEncoder()", level = DeprecationLevel.HIDDEN)
    override fun <T> verifyEncoderOrDecoder(
        serializer: KSerializer<T>,
        value: T,
        encodedTag: NbtNamed<NbtTag>?,
        testDecodedValue: (value: T, decodedValue: T) -> Unit
    ) {
        verifyEncoder(serializer, value, encodedTag)
    }

    fun <T> verifyEncoder(serializer: SerializationStrategy<T>, value: T, encodedTag: NbtNamed<NbtTag>?) {
        try {
            val context = SerializationNbtContext(this)
            val writer = VerifyingNbtWriter(encodedTag)
            val encoder = NbtWriterEncoder(this, context, writer)

            encoder.encodeSerializableValue(serializer, value)
            writer.assertComplete()
        } catch (e: NbtDecodingException) {
            fail("Encoding should not result in an NbtDecodingException", e)
        }
    }

    fun <T> verifyEncoder(serializer: SerializationStrategy<T>, value: T, encodedTag: NbtTag?) {
        // TODO non-empty serializer NbtName?
        val namedTag = encodedTag?.let { NbtNamed("", it) }

        verifyEncoder(serializer, value, namedTag)
    }
}

internal class DecoderVerifyingNbt(
    override val name: String,
    override val capabilities: NbtCapabilities,
    builderAction: NbtFormatBuilder.() -> Unit
) : VerifyingNbt(builderAction) {
    @Deprecated("Use verifyDecoder()", level = DeprecationLevel.HIDDEN)
    override fun <T> verifyEncoderOrDecoder(
        serializer: KSerializer<T>,
        value: T,
        encodedTag: NbtNamed<NbtTag>?,
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
        encodedTag: NbtNamed<NbtTag>?,
        testDecodedValue: (decodedValue: T) -> Unit = {}
    ) {
        try {
            val context = SerializationNbtContext(this)
            val reader = VerifyingNbtReader(encodedTag, capabilities)
            val decoder = NbtReaderDecoder(this, context, reader)
            val decodedValue = decoder.decodeSerializableValue(deserializer)

            reader.assertComplete()

            testDecodedValue(decodedValue)
        } catch (e: NbtEncodingException) {
            fail("Decoding should not result in an NbtEncodingException", e)
        }
    }

    fun <T> verifyDecoder(
        deserializer: DeserializationStrategy<T>,
        encodedTag: NbtTag?,
        testDecodedValue: (decodedValue: T) -> Unit = {}
    ) {
        // TODO non-empty serializer NbtName?
        val namedTag = encodedTag?.let { NbtNamed("", it) }

        verifyDecoder(deserializer, namedTag, testDecodedValue)
    }
}
