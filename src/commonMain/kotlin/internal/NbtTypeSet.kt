package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.NbtType

internal class NbtTypeSet private constructor(
    /**
     * Bits indicating whether each [NbtType] in this set, with `1` indicating that it is contained, and
     * [NbtType.bit] assigning bit positions.
     */
    private val elementBits: Int
) {
    private companion object {
        private val NbtType.bit: Int
            get() = 1 shl id.toInt()
    }

    constructor(elements: Collection<NbtType>) : this(
        elements.fold(0) { bits, tagType -> bits or tagType.bit }
    )

    operator fun contains(type: NbtType): Boolean =
        (elementBits and type.bit) != 0

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtTypeSet && elementBits == other.elementBits)

    override fun hashCode(): Int = elementBits

    override fun toString(): String =
        NbtType.entries
            .filter { it in this }
            .joinToString(prefix = "[", postfix = "]")
}
