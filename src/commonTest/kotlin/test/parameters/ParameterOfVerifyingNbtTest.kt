package net.benwoodworth.knbt.test.parameters

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test

class ParameterOfVerifyingNbtTest {
    @Test
    fun verifying_nbt_should_pass_for_any_serializable_type() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        val serializableType by parameterOfSerializableTypeEdgeCases()

        val serializer = object : KSerializer<Unit> {
            override val descriptor = serializableType.baseDescriptor

            override fun serialize(encoder: Encoder, value: Unit) =
                serializableType.encodeValue(encoder, descriptor)

            override fun deserialize(decoder: Decoder) =
                serializableType.decodeValue(decoder, descriptor)
        }

        nbt.verifyEncoderOrDecoder(serializer, Unit, serializableType.valueTag)
    }
}
