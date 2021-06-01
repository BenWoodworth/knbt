@file:OptIn(ExperimentalSerializationApi::class, ExperimentalNbtApi::class)

package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*

/**
 * Nest elements in a compound tag with the given [name].
 *
 * Example usage:
 * ```kotlin
 * @Serializable
 * @NbtRoot(name = "root")
 * class Example(val string: String, val int: Int)
 *
 * // Serializes to: {root : {string : "Hello, world!", int : 42}}
 * Nbt.encodeToNbtTag(Example(string = "Hello, World!", int = 42))
 * ```
 *
 * *Note*: Using the default value in Kotlin 1.5.0 causes an exception:
 * [KT-46739](https://youtrack.jetbrains.com/issue/KT-46739)
 */
@SerialInfo
@ExperimentalNbtApi
@Target(AnnotationTarget.CLASS)
public annotation class NbtRoot(val name: String = "")

internal fun nbtRootSerialDescriptor(nbtRoot: NbtRoot, rootDescriptor: SerialDescriptor): SerialDescriptor =
    buildClassSerialDescriptor("net.benwoodworth.knbt.NbtRoot", rootDescriptor) {
        element(nbtRoot.name, rootDescriptor)
    }

internal class NbtRootSerializer<T>(
    nbtRoot: NbtRoot,
    private val rootSerializer: SerializationStrategy<T>,
) : SerializationStrategy<T> {
    override val descriptor: SerialDescriptor = nbtRootSerialDescriptor(nbtRoot, rootSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, rootSerializer, value)
        }
    }
}

internal class NbtRootDeserializer<T>(
    private val nbtRoot: NbtRoot,
    private val rootDeserializer: DeserializationStrategy<T>,
) : DeserializationStrategy<T> {
    override val descriptor: SerialDescriptor = nbtRootSerialDescriptor(nbtRoot, rootDeserializer.descriptor)

    override fun deserialize(decoder: Decoder): T {
        var root: T? = null
        var rootDecoded = false

        decoder.decodeStructure(descriptor) {
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> {
                        root = decodeSerializableElement(descriptor, index, rootDeserializer)
                        rootDecoded = true
                    }
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }

        if (!rootDecoded) {
            val fieldName = nbtRoot.name
            val typeName = rootDeserializer.descriptor.serialName
            throw NbtDecodingException("Root tag '$fieldName' is required for type with serial name '$typeName', but it was missing")
        }

        @Suppress("UNCHECKED_CAST")
        return root as T
    }
}
