package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import net.benwoodworth.knbt.NbtArray

internal enum class NbtListKind { List, ByteArray, IntArray, LongArray }

@OptIn(ExperimentalSerializationApi::class, ExperimentalUnsignedTypes::class)
private fun SerialDescriptor.getNbtListKind(
    additionalAnnotations: List<Annotation> = emptyList()
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

    if (elementsCount == 0) throw NbtException(
        "$serialName has @NbtArray and zero elements, but one is required for determining array type"
    )

    return when (val elementKind = getElementDescriptor(0).kind) {
        PrimitiveKind.BYTE -> NbtListKind.ByteArray
        PrimitiveKind.INT -> NbtListKind.IntArray
        PrimitiveKind.LONG -> NbtListKind.LongArray

        else -> throw NbtException(
            "$serialName has @NbtArray with element kind $elementKind, but BYTE, INT, or LONG is required"
        )
    }
}

internal val SerialDescriptor.nbtListKind: NbtListKind
    get() = getNbtListKind()

@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.getElementNbtListKind(index: Int): NbtListKind =
    getElementDescriptor(index).getNbtListKind(getElementAnnotations(index))
