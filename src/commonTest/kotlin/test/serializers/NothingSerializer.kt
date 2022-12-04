package net.benwoodworth.knbt.test.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// https://github.com/Kotlin/kotlinx.serialization/pull/1991#issuecomment-1266690688
object NothingSerializer : KSerializer<Nothing> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("kotlin.Nothing", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Nothing): Unit =
        throw SerializationException("'kotlin.Nothing' cannot be serialized")

    override fun deserialize(decoder: Decoder): Nothing =
        throw SerializationException("'kotlin.Nothing' does not have instances")
}
