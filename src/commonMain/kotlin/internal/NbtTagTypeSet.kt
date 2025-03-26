package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.NbtTagType

internal class NbtTagTypeSet private constructor(
    /**
     * Bits indicating whether each [NbtTagType] in this set, with `1` indicating that it is contained, and
     * [NbtTagType.bit] assigning bit positions.
     */
    private val elementBits: Int
) {
    private companion object {
        private val NbtTagType.bit: Int
            get() = 1 shl id.toInt()
    }

    constructor(elements: Collection<NbtTagType>) : this(
        elements.fold(0) { bits, tagType -> bits or tagType.bit }
    )

    operator fun contains(type: NbtTagType): Boolean =
        (elementBits and type.bit) != 0

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtTagTypeSet && elementBits == other.elementBits)

    override fun hashCode(): Int = elementBits

    override fun toString(): String =
        NbtTagType.entries
            .filter { it in this }
            .joinToString(prefix = "[", postfix = "]")
}
