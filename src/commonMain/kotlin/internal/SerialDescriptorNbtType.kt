package net.benwoodworth.knbt.internal

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import net.benwoodworth.knbt.NbtArray
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtList
import net.benwoodworth.knbt.NbtType
import net.benwoodworth.knbt.NbtType.TAG_End
import net.benwoodworth.knbt.NbtType.TAG_List

/**
 * Marks a [SerialDescriptor] as a specific [NbtType] when serialized by an [NbtFormat].
 *
 * This is useful for pinning a [SerialDescriptor]'s [NbtType], regardless of how the descriptor is used.
 * For example, an [NbtList] property should always be serialized as a [TAG_List] element, even if it's marked as an
 * [NbtArray].
 *
 * This is also useful for [TAG_End] descriptors, since there's no corresponding [SerialKind]. The [Nothing] and `null`
 * descriptors *are* hardcoded to be recognized as [TAG_End] descriptors, but they can't be used when implementing a
 * custom serializer. Using these descriptors in a custom serializer requires them to be renamed, but doing so obscures
 * the original descriptor when introspecting for the [Nothing] or `null` descriptor.
 * See Issue [#2788](https://github.com/Kotlin/kotlinx.serialization/issues/2788) for more details.
 */
internal annotation class SerialDescriptorNbtType(val type: NbtType)
