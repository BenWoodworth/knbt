@file:OptIn(ExperimentalSerializationApi::class, ExperimentalNbtApi::class)

package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

/**
 * Override the [NbtConfiguration]'s [variant] and [compression], and
 * nest elements in a compound tag with the given [tagName].
 *
 * Example usage:
 * ```
 * @Serializable
 * @NbtFile(variant = Java, compression = None, tagName = "root")
 * data class ExampleFile(val string: String, val int: Int)
 *
 * // Encodes to {root:{string:"Hello, world!",int:42}}
 * val encoded = Nbt.encodeToNbtTag(ExampleFile("Hello, world!", 42))
 * ```
 */
@ExperimentalNbtApi
@SerialInfo
@Target(AnnotationTarget.CLASS)
public annotation class NbtFile(
    val variant: NbtVariant,
    val compression: NbtCompression,
    val tagName: String = "",
)

internal fun NbtFile.getFileNbt(nbt: Nbt): Nbt {
    val nbtFile = this
    return Nbt(nbt) {
        variant = nbtFile.variant
        compression = nbtFile.compression
    }
}

internal fun nbtFileSerialDescriptor(nbtFile: NbtFile, rootDescriptor: SerialDescriptor): SerialDescriptor =
    buildClassSerialDescriptor("net.benwoodworth.knbt.NbtFile", rootDescriptor) {
        element(nbtFile.tagName, rootDescriptor)
    }

internal class NbtFileSerializer<T>(
    nbtFile: NbtFile,
    private val rootSerializer: SerializationStrategy<T>,
) : SerializationStrategy<T> {
    override val descriptor: SerialDescriptor = nbtFileSerialDescriptor(nbtFile, rootSerializer.descriptor)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, rootSerializer, value)
        }
    }
}

internal class NbtFileDeserializer<T>(
    nbtFile: NbtFile,
    private val rootDeserializer: DeserializationStrategy<T>,
) : DeserializationStrategy<T> {
    override val descriptor: SerialDescriptor = nbtFileSerialDescriptor(nbtFile, rootDeserializer.descriptor)

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
                    DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
        }

        require(rootDecoded) { // TODO Message needed?
            "Field 'beans' is required for type with serial name 'files.LevelFile.Root', but it was missing"
        }

        @Suppress("UNCHECKED_CAST")
        return root as T
    }
}
