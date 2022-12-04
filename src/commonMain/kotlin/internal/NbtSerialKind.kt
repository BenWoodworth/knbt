package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import net.benwoodworth.knbt.NbtType

internal sealed interface NbtSerialKind

internal enum class NbtListKind : NbtSerialKind { List, ByteArray, IntArray, LongArray }

internal interface NbtSerialDiscriminator {
    fun discriminateListKind(descriptor: SerialDescriptor): NbtListKind

    fun discriminateElementListKind(descriptor: SerialDescriptor, index: Int): NbtListKind
}

@OptIn(ExperimentalSerializationApi::class)
internal object DefaultNbtSerialDiscriminator : NbtSerialDiscriminator {
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

    override fun discriminateListKind(descriptor: SerialDescriptor): NbtListKind {
        val nbtType = descriptor.annotations.getNbtSerialKind() as? NbtListKind
        if (nbtType != null) return nbtType

        return when (descriptor) {
            ByteArraySerializer().descriptor -> NbtListKind.ByteArray
            IntArraySerializer().descriptor -> NbtListKind.IntArray
            LongArraySerializer().descriptor -> NbtListKind.LongArray

            else -> NbtListKind.List
        }
    }

    override fun discriminateElementListKind(descriptor: SerialDescriptor, index: Int): NbtListKind {
        val nbtType = descriptor.getElementAnnotations(index).getNbtSerialKind() as? NbtListKind
        if (nbtType != null) return nbtType

        return discriminateListKind(descriptor.getElementDescriptor(index))
    }
}
