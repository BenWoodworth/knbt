package net.benwoodworth.knbt.test.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*

@OptIn(ExperimentalSerializationApi::class)
class ListSerializerWithAnnotations<T>(
    private val elementSerializer: KSerializer<T>,
    private val annotations: List<Annotation> = emptyList(),
    private val elementAnnotations: List<Annotation> = emptyList(),
) : KSerializer<List<T>> {
    private val listDescriptor = ListSerializer(elementSerializer).descriptor

    override val descriptor: SerialDescriptor =
        object : SerialDescriptor by listDescriptor {
            override val serialName: String =
                ListSerializerWithAnnotations::class.simpleName!!

            override val annotations: List<Annotation> =
                listDescriptor.annotations + this@ListSerializerWithAnnotations.annotations

            @ExperimentalSerializationApi
            override fun getElementAnnotations(index: Int): List<Annotation> =
                listDescriptor.getElementAnnotations(index) + elementAnnotations
        }

    override fun serialize(encoder: Encoder, value: List<T>): Unit =
        encoder.encodeCollection(descriptor, value) { i, element ->
            encodeSerializableElement(descriptor, i, elementSerializer, element)
        }

    override fun deserialize(decoder: Decoder): List<T> =
        decoder.decodeStructure(descriptor) {
            if (decodeSequentially()) {
                var previousValue: T? = null
                List(decodeCollectionSize(descriptor)) { i ->
                    decodeSerializableElement(descriptor, i, elementSerializer, previousValue)
                        .also { previousValue = it }
                }
            } else {
                buildList {
                    var previousValue: T? = null
                    while (true) {
                        val index = decodeElementIndex(descriptor)
                        if (index == CompositeDecoder.DECODE_DONE) return@buildList
                        previousValue = decodeSerializableElement(descriptor, index, elementSerializer, previousValue)
                        add(index, previousValue!!)
                    }
                }
            }
        }
}
