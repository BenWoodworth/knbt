package net.benwoodworth.knbt.tag

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.asNbtEncoder
import net.benwoodworth.knbt.internal.NbtTagType
import kotlin.jvm.JvmName

@Serializable(NbtCompoundSerializer::class)
public class NbtCompound<out T : NbtTag> internal constructor(
    internal val value: Map<String, T>
) : NbtTag, Map<String, T> by value {

    override val type: NbtTagType get() = NbtTagType.TAG_Compound

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtCompound<*> && value == other.value)

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()

    internal companion object {
        val empty = NbtCompound<Nothing>(emptyMap())
    }
}

public fun nbtCompoundOf(): NbtCompound<NbtTag> = NbtCompound.empty

@JvmName("nbtCompoundOf\$T")
public fun <T : NbtTag> nbtCompoundOf(): NbtCompound<T> = NbtCompound.empty

public fun <T : NbtTag> nbtCompoundOf(vararg pairs: Pair<String, T>): NbtCompound<T> =
    if (pairs.isEmpty()) NbtCompound.empty else NbtCompound(linkedMapOf(*pairs))

public fun <T : NbtTag> Map<String, T>.toNbtCompound(): NbtCompound<T> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(this.toMap())


private class NbtCompoundSerializer<T : NbtTag>(
    elementSerializer: KSerializer<T>,
) : KSerializer<NbtCompound<T>> {
    private val mapSerializer: KSerializer<Map<String, T>> =
        MapSerializer(String.serializer(), elementSerializer)

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("net.benwoodworth.knbt.NbtCompound")

    override fun serialize(encoder: Encoder, value: NbtCompound<T>): Unit =
        encoder.asNbtEncoder().encodeSerializableValue(mapSerializer, value.value)

    override fun deserialize(decoder: Decoder): NbtCompound<T> {
        val map = mapSerializer.deserialize(decoder)
        return if (map.isEmpty()) NbtCompound.empty else NbtCompound(map)
    }
}
