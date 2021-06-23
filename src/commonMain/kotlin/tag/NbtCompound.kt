package net.benwoodworth.knbt.tag

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.mapSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.asNbtEncoder
import net.benwoodworth.knbt.internal.NbtTagType
import kotlin.jvm.JvmName

@Serializable(NbtCompoundSerializer::class)
public class NbtCompound internal constructor(
    internal val value: Map<String, NbtTag>
) : NbtTag, Map<String, NbtTag> by value {

    override val type: NbtTagType get() = NbtTagType.TAG_Compound

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtCompound && value == other.value
        else -> value == other
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()

    public companion object {
        internal val empty = NbtCompound(emptyMap())

        @Suppress("UNUSED_PARAMETER")
        @Deprecated(
            "NbtCompound no longer has a type parameter",
            ReplaceWith("NbtCompound.serializer()", "net.benwoodworth.knbt.tag.NbtCompound"),
        )
        public inline fun <T0> serializer(typeSerial0: KSerializer<T0>): KSerializer<NbtCompound> = serializer()
    }
}

public fun nbtCompoundOf(): NbtCompound = NbtCompound.empty

@Suppress("unused")
@Deprecated("NbtCompound no longer has a type parameter", ReplaceWith("nbtCompoundOf()"))
@JvmName("nbtCompoundOf\$T")
public inline fun <T : NbtTag> nbtCompoundOf(): NbtCompound = nbtCompoundOf()

public fun nbtCompoundOf(vararg pairs: Pair<String, NbtTag>): NbtCompound =
    if (pairs.isEmpty()) NbtCompound.empty else NbtCompound(linkedMapOf(*pairs))

@Deprecated("NbtCompound no longer has a type parameter", ReplaceWith("nbtCompoundOf(*pairs)"))
@JvmName("nbtCompoundOf\$T")
public inline fun <T : NbtTag> nbtCompoundOf(vararg pairs: Pair<String, T>): NbtCompound = nbtCompoundOf(*pairs)

public fun Map<String, NbtTag>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(this.toMap())

@Deprecated("NbtCompound no longer has a type parameter", ReplaceWith("toNbtCompound()"))
@JvmName("toNbtCompound\$T")
public fun <T : NbtTag> Map<String, T>.toNbtCompound(): NbtCompound = toNbtCompound()

@JvmName("toNbtCompound\$Byte")
public fun Map<String, Byte>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtByte() })

@JvmName("toNbtCompound\$Short")
public fun Map<String, Short>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtShort() })

@JvmName("toNbtCompound\$Int")
public fun Map<String, Int>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtInt() })

@JvmName("toNbtCompound\$Long")
public fun Map<String, Long>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtLong() })

@JvmName("toNbtCompound\$Float")
public fun Map<String, Float>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtFloat() })

@JvmName("toNbtCompound\$Double")
public fun Map<String, Double>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtDouble() })

@JvmName("toNbtCompound\$String")
public fun Map<String, String>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtString() })

@JvmName("toNbtCompound\$ByteArray")
public fun Map<String, ByteArray>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtByteArray() })

@JvmName("toNbtCompound\$IntArray")
public fun Map<String, IntArray>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtIntArray() })

@JvmName("toNbtCompound\$LongArray")
public fun Map<String, LongArray>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtLongArray() })


internal object NbtCompoundSerializer : KSerializer<NbtCompound> {
    override val descriptor: SerialDescriptor = NbtCompoundDescriptor(NbtTag.serializer().descriptor)
    private val mapSerializer = MapSerializer(String.serializer(), NbtTag.serializer())

    override fun serialize(encoder: Encoder, value: NbtCompound): Unit =
        encoder.asNbtEncoder().encodeNbtTag(value)

    override fun deserialize(decoder: Decoder): NbtCompound {
        val map = mapSerializer.deserialize(decoder)
        return if (map.isEmpty()) NbtCompound.empty else NbtCompound(map)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private class NbtCompoundDescriptor(
        val elementDescriptor: SerialDescriptor,
    ) : SerialDescriptor by mapSerialDescriptor(String.serializer().descriptor, elementDescriptor) {
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.tag.NbtCompound"
    }
}
