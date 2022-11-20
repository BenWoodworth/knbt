package net.benwoodworth.knbt.test.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer

class SurrogateSerializer<T, TSurrogate>(
    private val surrogateSerializer: KSerializer<TSurrogate>,
    private val toSurrogate: T.() -> TSurrogate,
    private val fromSurrogate: TSurrogate.() -> T,
) : KSerializer<T> {
    override val descriptor: SerialDescriptor = surrogateSerializer.descriptor

    override fun serialize(encoder: Encoder, value: T): Unit =
        encoder.encodeSerializableValue(surrogateSerializer, value.toSurrogate())

    override fun deserialize(decoder: Decoder): T =
        decoder.decodeSerializableValue(surrogateSerializer).fromSurrogate()


    companion object {
        inline operator fun <T, reified TSurrogate> invoke(
            noinline toSurrogate: T.() -> TSurrogate,
            noinline fromSurrogate: TSurrogate.() -> T,
        ): SurrogateSerializer<T, TSurrogate> =
            SurrogateSerializer(serializer(), toSurrogate, fromSurrogate)
    }
}
