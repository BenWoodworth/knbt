package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import net.benwoodworth.knbt.ExperimentalNbtApi
import net.benwoodworth.knbt.NbtArray
import net.benwoodworth.knbt.NbtName

internal enum class NbtListKind { List, ByteArray, IntArray, LongArray }

@OptIn(ExperimentalSerializationApi::class, ExperimentalUnsignedTypes::class)
private fun SerialDescriptor.getNbtListKind(
    context: NbtContext,
    additionalAnnotations: List<Annotation>
): NbtListKind {
    when (this) {
        ByteArraySerializer().descriptor -> return NbtListKind.ByteArray
        IntArraySerializer().descriptor -> return NbtListKind.IntArray
        LongArraySerializer().descriptor -> return NbtListKind.LongArray

        UByteArraySerializer().descriptor -> return NbtListKind.ByteArray
        UIntArraySerializer().descriptor -> return NbtListKind.IntArray
        ULongArraySerializer().descriptor -> return NbtListKind.LongArray
    }

    val hasNbtArrayAnnotation = annotations.any { it is NbtArray } || additionalAnnotations.any { it is NbtArray }
    if (!hasNbtArrayAnnotation) return NbtListKind.List

    if (elementsCount == 0) {
        val message = "$serialName has @NbtArray and zero elements, but one is required for determining array type"
        throw NbtException(context, message)
    }

    return when (val elementKind = getElementDescriptor(0).kind) {
        PrimitiveKind.BYTE -> NbtListKind.ByteArray
        PrimitiveKind.INT -> NbtListKind.IntArray
        PrimitiveKind.LONG -> NbtListKind.LongArray

        else -> {
            val message = "$serialName has @NbtArray with element kind $elementKind, but BYTE, INT, or LONG is required"
            throw NbtException(context, message)
        }
    }
}

internal fun SerialDescriptor.getNbtListKind(context: NbtContext): NbtListKind =
    getNbtListKind(context, emptyList())

@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.getElementNbtListKind(context: NbtContext, index: Int): NbtListKind =
    getElementDescriptor(index).getNbtListKind(context, getElementAnnotations(index))


@OptIn(ExperimentalSerializationApi::class)
internal val SerialDescriptor.nbtName: String?
    get() = annotations
        .firstOrNull { it is NbtName }
        ?.let { it as NbtName }
        ?.name
        ?: "".takeIf { nbtNameIsDynamic }


@OptIn(ExperimentalSerializationApi::class, ExperimentalNbtApi::class)
internal val SerialDescriptor.nbtNameIsDynamic: Boolean
    get() = annotations.any { it is NbtName.Dynamic }
