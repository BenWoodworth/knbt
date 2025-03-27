package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.encoding.Decoder
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.SerializableTypeEdgeCase
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
import net.benwoodworth.knbt.test.parameters.parameterOfSerializableTypeEdgeCases
import kotlin.test.assertEquals

class DecodeNbtTypeTest {        // TODO
    private class VerifyingDeserializer(
        private val baseType: SerializableTypeEdgeCase
    ) : DeserializationStrategy<Unit> {
        override val descriptor = baseType.baseDescriptor

        override fun deserialize(decoder: Decoder) {
            val decodedType = decoder.asNbtDecoder().decodeNbtType()
            assertEquals(baseType.valueTag.type, decodedType)

            baseType.decodeValue(decoder, descriptor)
        }
    }

//    @Test
    fun decoding_nbt_tag_type_should_return_the_encoded_tag_type() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt()
        val type by parameterOfSerializableTypeEdgeCases()

        val serializer = VerifyingDeserializer(type)
        nbt.verifyDecoder(serializer, type.valueTag)
    }

//    @Test
    fun decoding_nbt_tag_type_within_a_compound_should_return_the_next_element_type() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt()
        val type by parameterOfSerializableTypeEdgeCases()

        val serializer = object : DeserializationStrategy<NbtType> {
            override val descriptor = type.baseDescriptor

            override fun deserialize(decoder: Decoder): NbtType {
                var decodedType = decoder.asNbtDecoder().decodeNbtType()
                type.decodeValue(decoder, descriptor)

                return decodedType
            }
        }


    }

    // compound after 1st element

    // compound after all elements

    // list element

    // empty list element
}
