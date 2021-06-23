package net.benwoodworth.knbt.tag

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.PolymorphicSerializer
import net.benwoodworth.knbt.InternalNbtApi
import net.benwoodworth.knbt.internal.NbtTagType

@Polymorphic
public sealed interface NbtTag {
    /**
     * For internal use only. Will be marked as internal once Kotlin supports it on sealed interface members.
     * @suppress
     */
    @InternalNbtApi
    public val type: NbtTagType // TODO Make internal

    public companion object {
        public fun serializer(): KSerializer<NbtTag> = PolymorphicSerializer(NbtTag::class)
    }
}
