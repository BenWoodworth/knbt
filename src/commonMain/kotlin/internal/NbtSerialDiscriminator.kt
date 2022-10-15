package net.benwoodworth.knbt.internal

import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.builtins.LongArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor

internal sealed interface NbtSerialKind

internal enum class NbtListKind : NbtSerialKind { List, ByteArray, IntArray, LongArray }

internal interface NbtSerialDiscriminator {
    fun discriminateListKind(descriptor: SerialDescriptor): NbtListKind
}

internal object DefaultNbtSerialDiscriminator : NbtSerialDiscriminator {
    override fun discriminateListKind(descriptor: SerialDescriptor): NbtListKind =
        when (descriptor) {
            ByteArraySerializer().descriptor -> NbtListKind.ByteArray
            IntArraySerializer().descriptor -> NbtListKind.IntArray
            LongArraySerializer().descriptor -> NbtListKind.LongArray

            else -> NbtListKind.List
        }
}
