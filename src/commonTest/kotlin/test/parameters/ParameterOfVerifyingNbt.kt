package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtFormatBuilder
import net.benwoodworth.knbt.NbtNamed
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.internal.*
import net.benwoodworth.knbt.test.verify.VerifyingNbtReader
import net.benwoodworth.knbt.test.verify.VerifyingNbtWriter
import net.benwoodworth.knbt.toNbtCompound
import kotlin.test.fail

internal fun ParameterizeScope.parameterOfVerifyingNbt(
    includeNamedRootNbt: Boolean = false, // TODO Temporary, since most tests won't support naming until it's redesigned
    builderAction: NbtFormatBuilder.() -> Unit = {}
) = parameter {
    val baseCapabilities = NbtCapabilities(
        namedRoot = false,
        definiteLengthEncoding = false,
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
    fun <T> verifyEncoderOrDecoder(
        serializer: KSerializer<T>,
        value: T,
        encodedTag: NbtNamed<NbtTag>,
        testDecodedValue: (value: T, decodedValue: T) -> Unit = { _, _ -> }
    ) {
        verifyEncoderOrDecoder(serializer, value, encodedTag.toNbtCompound(), testDecodedValue)
    }

    abstract fun <T> verifyEncoderOrDecoder(
        serializer: KSerializer<T>,
        value: T,
        encodedTag: NbtTag,
        testDecodedValue: (value: T, decodedValue: T) -> Unit = { _, _ -> }
    )
}

internal class EncoderVerifyingNbt(
    val name: String,
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

    fun <T> verifyEncoder(serializer: SerializationStrategy<T>, value: T, encodedTag: NbtNamed<NbtTag>) {
        verifyEncoder(serializer, value, encodedTag.toNbtCompound())
    }

    fun <T> verifyEncoder(serializer: SerializationStrategy<T>, value: T, encodedTag: NbtTag) {
        try {
            val context = SerializationNbtContext()
            val writer = VerifyingNbtWriter(encodedTag)
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
        encodedTag: NbtNamed<NbtTag>,
        testDecodedValue: (decodedValue: T) -> Unit = {}
    ) {
        verifyDecoder(deserializer, encodedTag.toNbtCompound(), testDecodedValue)
    }

    fun <T> verifyDecoder(
        deserializer: DeserializationStrategy<T>,
        encodedTag: NbtTag,
        testDecodedValue: (decodedValue: T) -> Unit = {}
    ) {
        try {
            val context = SerializationNbtContext()
            val reader = VerifyingNbtReader(encodedTag, capabilities)
            val decoder = NbtReaderDecoder(nbt, context, reader)
            val decodedValue = decoder.decodeSerializableValue(deserializer)

            reader.assertComplete()

            testDecodedValue(decodedValue)
        } catch (e: NbtEncodingException) {
            fail("Decoding should not result in an NbtEncodingException", e)
        }
    }
}
