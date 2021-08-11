package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.nbtIntArrayOf
import net.benwoodworth.knbt.toNbtIntArray

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtIntArray",
    ReplaceWith("NbtIntArray", "net.benwoodworth.knbt.NbtIntArray"),
)
public typealias NbtIntArray = NbtIntArray

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtIntArray",
    ReplaceWith("NbtIntArray(size, init)", "net.benwoodworth.knbt.NbtIntArray"),
)
public inline fun NbtIntArray(size: Int, init: (index: Int) -> Int): NbtIntArray = NbtIntArray(size, init)

@Deprecated(
    "Moved to net.benwoodworth.knbt.nbtIntArrayOf",
    ReplaceWith("nbtIntArrayOf(*elements)", "net.benwoodworth.knbt.NbtIntArray"),
)
public fun nbtIntArrayOf(vararg elements: Int): NbtIntArray = nbtIntArrayOf(*elements)

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtIntArray",
    ReplaceWith("this.toNbtIntArray()", "net.benwoodworth.knbt.toNbtIntArray"),
)
public fun IntArray.toNbtIntArray(): NbtIntArray = toNbtIntArray()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtIntArray",
    ReplaceWith("this.toNbtIntArray()", "net.benwoodworth.knbt.toNbtIntArray"),
)
public fun Collection<Int>.toNbtIntArray(): NbtIntArray = toNbtIntArray()
