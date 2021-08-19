package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtIntArray

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtIntArray",
    ReplaceWith("NbtIntArray", "net.benwoodworth.knbt.NbtIntArray"),
)
public typealias NbtIntArray = NbtIntArray

@Deprecated(
    "Replaced with NbtIntArray constructor",
    ReplaceWith("NbtIntArray(IntArray(size, init))", "net.benwoodworth.knbt.NbtIntArray"),
)
public inline fun NbtIntArray(size: Int, init: (index: Int) -> Int): NbtIntArray =
    NbtIntArray(IntArray(size) { init(it) })

@Deprecated(
    "Replaced with NbtIntArray constructor",
    ReplaceWith("NbtIntArray(intArrayOf(*elements))", "net.benwoodworth.knbt.NbtIntArray"),
)
public fun nbtIntArrayOf(vararg elements: Int): NbtIntArray = NbtIntArray(intArrayOf(*elements))

@Deprecated(
    "Replaced with NbtIntArray constructor",
    ReplaceWith("NbtIntArray(this)", "net.benwoodworth.knbt.NbtIntArray"),
)
public fun IntArray.toNbtIntArray(): NbtIntArray = NbtIntArray(this)

@Deprecated(
    "Replaced with NbtIntArray constructor",
    ReplaceWith("NbtIntArray(this.toList())", "net.benwoodworth.knbt.NbtIntArray"),
)
public fun Collection<Int>.toNbtIntArray(): NbtIntArray = NbtIntArray(this.toList())
