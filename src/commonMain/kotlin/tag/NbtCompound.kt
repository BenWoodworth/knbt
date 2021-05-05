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

@JvmName("toNbtCompound\$Byte")
public fun Map<String, Byte>.toNbtCompound(): NbtCompound<NbtByte> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtByte() })

@JvmName("toNbtCompound\$Short")
public fun Map<String, Short>.toNbtCompound(): NbtCompound<NbtShort> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtShort() })

@JvmName("toNbtCompound\$Int")
public fun Map<String, Int>.toNbtCompound(): NbtCompound<NbtInt> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtInt() })

@JvmName("toNbtCompound\$Long")
public fun Map<String, Long>.toNbtCompound(): NbtCompound<NbtLong> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtLong() })

@JvmName("toNbtCompound\$Float")
public fun Map<String, Float>.toNbtCompound(): NbtCompound<NbtFloat> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtFloat() })

@JvmName("toNbtCompound\$Double")
public fun Map<String, Double>.toNbtCompound(): NbtCompound<NbtDouble> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtDouble() })

@JvmName("toNbtCompound\$String")
public fun Map<String, String>.toNbtCompound(): NbtCompound<NbtString> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtString() })

@JvmName("toNbtCompound\$ByteArray")
public fun Map<String, ByteArray>.toNbtCompound(): NbtCompound<NbtByteArray> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtByteArray() })

@JvmName("toNbtCompound\$IntArray")
public fun Map<String, IntArray>.toNbtCompound(): NbtCompound<NbtIntArray> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtIntArray() })

@JvmName("toNbtCompound\$LongArray")
public fun Map<String, LongArray>.toNbtCompound(): NbtCompound<NbtLongArray> =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtLongArray() })


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
