package net.benwoodworth.knbt.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import net.benwoodworth.knbt.NbtDecodingException

@OptIn(ExperimentalSerializationApi::class)
private fun rootClassSerialDescriptor(classDescriptor: SerialDescriptor): SerialDescriptor =
    buildClassSerialDescriptor("net.benwoodworth.knbt.internal.RootClass", classDescriptor) {
        element(classDescriptor.serialName, classDescriptor)
    }

internal class RootClassSerializer<T>(
    private val classSerializer: SerializationStrategy<T>,
) : SerializationStrategy<T> {
    override val descriptor: SerialDescriptor = rootClassSerialDescriptor(classSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, classSerializer, value)
        }
    }
}

internal class RootClassDeserializer<T>(
    private val classDeserializer: DeserializationStrategy<T>,
) : DeserializationStrategy<T> {
    override val descriptor: SerialDescriptor = rootClassSerialDescriptor(classDeserializer.descriptor)

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): T {
        var root: T? = null
        var rootDecoded = false

        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> {
                        root = decodeSerializableElement(descriptor, index, classDeserializer)
                        rootDecoded = true
                    }
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }

        if (!rootDecoded) {
            val serialName = classDeserializer.descriptor.serialName
            throw NbtDecodingException(
                "Root tag '$serialName' is required for class with serial name '$serialName', but it was missing."
            )
        }

        @Suppress("UNCHECKED_CAST")
        return root as T
    }
}
