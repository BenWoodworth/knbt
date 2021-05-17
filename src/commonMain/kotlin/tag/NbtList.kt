package net.benwoodworth.knbt.tag

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.benwoodworth.knbt.asNbtEncoder
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.NbtTagType.TAG_End
import net.benwoodworth.knbt.internal.NbtTagType.TAG_List
import kotlin.jvm.JvmName

@Serializable(NbtListSerializer::class)
public class NbtList<out T : NbtTag> internal constructor(
    internal val value: List<T>,
) : NbtTag, List<T> by value {
    override val type: NbtTagType get() = TAG_List

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtList<*> && value == other.value)

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()

    internal companion object {
        val empty: NbtList<Nothing> = NbtList(emptyList())
    }
}

internal val NbtList<*>.elementType: NbtTagType
    get() = if (value.isEmpty()) TAG_End else value.first().type

public fun <T : NbtTag> nbtListOf(): NbtList<T> = NbtList.empty

public fun <T : NbtTag> nbtListOf(vararg elements: T): NbtList<T> =
    if (elements.isEmpty()) NbtList.empty else NbtList(elements.asList())

public fun <T : NbtTag> List<T>.toNbtList(): NbtList<T> = when (size) {
    0 -> NbtList.empty
    1 -> NbtList(listOf(first()))
    else -> {
        var elementType = TAG_End
        val elements = map { element ->
            if (elementType == TAG_End) {
                elementType = element.type
            } else {
                require(element.type == elementType) { "NbtList elements must all have the same type" }
            }
            element
        }
        NbtList(elements)
    }
}

@JvmName("toNbtList\$Byte")
public fun List<Byte>.toNbtList(): NbtList<NbtByte> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtByte() })

@JvmName("toNbtList\$Short")
public fun List<Short>.toNbtList(): NbtList<NbtShort> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtShort() })

@JvmName("toNbtList\$Int")
public fun List<Int>.toNbtList(): NbtList<NbtInt> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtInt() })

@JvmName("toNbtList\$Long")
public fun List<Long>.toNbtList(): NbtList<NbtLong> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtLong() })

@JvmName("toNbtList\$Float")
public fun List<Float>.toNbtList(): NbtList<NbtFloat> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtFloat() })

@JvmName("toNbtList\$Double")
public fun List<Double>.toNbtList(): NbtList<NbtDouble> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtDouble() })

@JvmName("toNbtList\$String")
public fun List<String>.toNbtList(): NbtList<NbtString> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtString() })

@JvmName("toNbtList\$ByteArray")
public fun List<ByteArray>.toNbtList(): NbtList<NbtByteArray> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtByteArray() })

@JvmName("toNbtList\$IntArray")
public fun List<IntArray>.toNbtList(): NbtList<NbtIntArray> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtIntArray() })

@JvmName("toNbtList\$LongArray")
public fun List<LongArray>.toNbtList(): NbtList<NbtLongArray> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtLongArray() })


internal class NbtListSerializer<T : NbtTag>(
    elementSerializer: KSerializer<T>,
) : KSerializer<NbtList<T>> {
    override val descriptor: SerialDescriptor = NbtListDescriptor(elementSerializer.descriptor)
    private val listSerializer = ListSerializer(elementSerializer)

    override fun serialize(encoder: Encoder, value: NbtList<T>): Unit =
        encoder.asNbtEncoder().encodeNbtTag(value)

    override fun deserialize(decoder: Decoder): NbtList<T> {
        val list = listSerializer.deserialize(decoder)
        return if (list.isEmpty()) NbtList.empty else NbtList(list)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private class NbtListDescriptor(
        val elementDescriptor: SerialDescriptor,
    ) : SerialDescriptor by listSerialDescriptor(elementDescriptor) {
        @ExperimentalSerializationApi
        override val serialName: String = "net.benwoodworth.knbt.NbtList"
    }
}
