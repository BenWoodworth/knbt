package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtLongArray

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtLongArray",
    ReplaceWith("NbtLongArray", "net.benwoodworth.knbt.NbtLongArray"),
)
public typealias NbtLongArray = NbtLongArray

@Deprecated(
    "Replaced with NbtLongArray constructor",
    ReplaceWith("NbtLongArray(LongArray(size, init))", "net.benwoodworth.knbt.NbtLongArray"),
)
public inline fun NbtLongArray(size: Int, init: (index: Int) -> Long): NbtLongArray =
    NbtLongArray(LongArray(size) { init(it) })

@Deprecated(
    "Replaced with NbtLongArray constructor",
    ReplaceWith("NbtLongArray(longArrayOf(*elements))", "net.benwoodworth.knbt.NbtLongArray"),
)
public fun nbtLongArrayOf(vararg elements: Long): NbtLongArray = NbtLongArray(longArrayOf(*elements))

@Deprecated(
    "Replaced with NbtLongArray constructor",
    ReplaceWith("NbtLongArray(this)", "net.benwoodworth.knbt.NbtLongArray"),
)
public fun LongArray.toNbtLongArray(): NbtLongArray = NbtLongArray(this)

@Deprecated(
    "Replaced with NbtLongArray constructor",
    ReplaceWith("NbtLongArray(this.toLongArray())", "net.benwoodworth.knbt.NbtLongArray"),
)
public fun Collection<Long>.toNbtLongArray(): NbtLongArray = NbtLongArray(this.toLongArray())
