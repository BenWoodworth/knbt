package net.benwoodworth.knbt.internal

internal class NbtTagTypeSet(
    elements: Collection<NbtTagType>
) {
    /**
     * Bits indicating whether each [NbtTagType] in this set, with `1` indicating that it is contained, and
     * [NbtTagType.bit] assigning bit positions.
     */
    private val elementBits: Int =
        elements.fold(0) { bits, tagType -> bits or tagType.bit }

    private val NbtTagType.bit: Int
        get() = 1 shl id.toInt()

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
