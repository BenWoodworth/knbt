package net.benwoodworth.knbt.test

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.NbtName

private class WithNbtNameSerializer<T>( // TODO Add in previous commit
    val serializer: KSerializer<T>,
    nbtName: String
) : KSerializer<T> {
    override val descriptor: SerialDescriptor =
        serializer.descriptor.withNbtName(nbtName)

    override fun serialize(encoder: Encoder, value: T): Unit =
        encoder.encodeSerializableValue(serializer, value)

    override fun deserialize(decoder: Decoder): T =
        decoder.decodeSerializableValue(serializer)
}

@OptIn(SealedSerializationApi::class)
private data class WithNbtNameSerialDescriptor(
    val serialDescriptor: SerialDescriptor,
    val nbtName: String,
) : SerialDescriptor by serialDescriptor {
    override val serialName: String = "WithNbtName<${serialDescriptor.serialName}>($nbtName)"

    override val annotations: List<Annotation> =
        serialDescriptor.annotations
            .filter { it !is NbtName }
            .plus(NbtName(nbtName))
}

fun <T> KSerializer<T>.withNbtName(nbtName: String): KSerializer<T> =
    WithNbtNameSerializer(this, nbtName)

fun SerialDescriptor.withNbtName(nbtName: String): SerialDescriptor =
    WithNbtNameSerialDescriptor(this, nbtName)
