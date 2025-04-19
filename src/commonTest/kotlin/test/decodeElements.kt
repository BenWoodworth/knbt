package net.benwoodworth.knbt.test

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.decodeStructure

data object NotDecoded

//inline fun CompositeDecoder.decodeStructureElements(
//    descriptor: SerialDescriptor,
//    decodeElement
//)

@OptIn(ExperimentalSerializationApi::class)
inline fun Decoder.decodeStructureAndElements(
    descriptor: SerialDescriptor,
    crossinline decodeElement: (index: Int) -> Any?
): List<Any?> {
    return decodeStructure(descriptor) {
        if (decodeSequentially()) {
            MutableList(decodeCollectionSize(descriptor), decodeElement)
        } else {
            val result = ArrayList<Any?>()

            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    CompositeDecoder.DECODE_DONE -> break
                    CompositeDecoder.UNKNOWN_NAME -> error("Encountered an element with unknown name, but decoder did not throw")
                    else -> {
                        if (result.lastIndex < index) {
                            repeat(index - result.lastIndex) {
                                result.add(NotDecoded)
                            }
                        }

                        result[index] = decodeElement(index)
                    }
                }
            }

            result.forEachIndexed { index, decodedValue ->
                require(decodedValue != NotDecoded || descriptor.isElementOptional(index)) {
                    "Element '${descriptor.getElementName(index)}' was not decoded, and was not optional"
                }
            }

            result
        }
    }
}
