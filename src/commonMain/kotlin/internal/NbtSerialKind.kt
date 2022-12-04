package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import net.benwoodworth.knbt.NbtType

internal sealed interface NbtSerialKind

internal enum class NbtListKind : NbtSerialKind { List, ByteArray, IntArray, LongArray }

@OptIn(ExperimentalSerializationApi::class)
private fun List<Annotation>.getNbtSerialKind(): NbtSerialKind? {
    val nbtType = firstOrNull { it is NbtType } as NbtType?
        ?: return null

    return when (nbtType.type.toNbtTagType()) {
        NbtTagType.TAG_List -> NbtListKind.List
        NbtTagType.TAG_Byte_Array -> NbtListKind.ByteArray
        NbtTagType.TAG_Int_Array -> NbtListKind.IntArray
        NbtTagType.TAG_Long_Array -> NbtListKind.LongArray
        else -> null
    }
}

@OptIn(ExperimentalSerializationApi::class)
internal val SerialDescriptor.nbtListKind: NbtListKind
    get() {
        val nbtType = annotations.getNbtSerialKind() as? NbtListKind
        if (nbtType != null) return nbtType

        return when (this) {
            ByteArraySerializer().descriptor -> NbtListKind.ByteArray
            IntArraySerializer().descriptor -> NbtListKind.IntArray
            LongArraySerializer().descriptor -> NbtListKind.LongArray

            else -> NbtListKind.List
        }
    }

@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.getElementNbtListKind(index: Int): NbtListKind {
    val nbtType = getElementAnnotations(index).getNbtSerialKind() as? NbtListKind
    if (nbtType != null) return nbtType

    return getElementDescriptor(index).nbtListKind
}
