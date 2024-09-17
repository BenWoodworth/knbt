package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/** TODO wording
 * The serial representation of a [value], except its [NbtName] is replaced with [name].
 *
 * Named NBT formats
 *
 * when [encodeToNamedNbtTag], captures the [value] and its name
 * when [decodeFromNamedNbtTag],
 *
 * when encoding (name replaces) // TODO When serializer is implemented
 * when decoding (name captures) // TODO When serializer is implemented
 */
@Serializable(NbtNamedSerializer::class)
public class NbtNamed<out T>(
    public val name: String,
    public val value: T
) {
    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is NbtNamed<*> -> false
        name != other.name -> false
        value != other.value -> false
        else -> true
    }

    override fun hashCode(): Int =
        name.hashCode() * 31 + value.hashCode()

    override fun toString(): String =
        "NbtNamed(name=$name, value=$value)"
}

private class NbtNamedSerializer<T>(
    private val valueSerializer: KSerializer<T>,
) : KSerializer<NbtNamed<T>> {
    override val descriptor = NbtNamedSerialDescriptor(valueSerializer.descriptor)

    @OptIn(ExperimentalNbtApi::class)
    override fun serialize(encoder: Encoder, value: NbtNamed<T>) {
        encoder.asNbtEncoder().encodeNbtName(value.name)
        encoder.encodeSerializableValue(valueSerializer, value.value)
    }

    @OptIn(ExperimentalNbtApi::class)
    override fun deserialize(decoder: Decoder): NbtNamed<T> {
        val name = decoder.asNbtDecoder().decodeNbtName()
        val value = decoder.decodeSerializableValue(valueSerializer)

        return NbtNamed(name, value)
    }
}

@OptIn(ExperimentalSerializationApi::class, ExperimentalNbtApi::class)
private class NbtNamedSerialDescriptor(
    private val valueSerialDescriptor: SerialDescriptor
) : SerialDescriptor by valueSerialDescriptor {
    override val serialName: String = "net.benwoodworth.knbt.NbtNamed<${valueSerialDescriptor.serialName}>"
    private val hashCode = valueSerialDescriptor.hashCode() * 31 + serialName.hashCode()

    override val annotations: List<Annotation> =
        buildList(valueSerialDescriptor.annotations.size + 1) {
            // First, since serialization will immediately check the list for it
            add(NbtName.Dynamic())

            // Copy over all except Dynamic, since it's already marked
            valueSerialDescriptor.annotations
                .forEach { if (it !is NbtName.Dynamic) add(it) }
        }

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is NbtNamedSerialDescriptor -> false
        valueSerialDescriptor != other.valueSerialDescriptor -> false
        else -> true
    }

    override fun hashCode(): Int = hashCode

    override fun toString(): String = serialName
}
