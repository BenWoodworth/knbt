package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtLongArray
import net.benwoodworth.knbt.nbtLongArrayOf
import net.benwoodworth.knbt.toNbtLongArray

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtLongArray",
    ReplaceWith("NbtLongArray", "net.benwoodworth.knbt.NbtLongArray"),
)
public typealias NbtLongArray = NbtLongArray

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtLongArray",
    ReplaceWith("NbtLongArray(size, init)", "net.benwoodworth.knbt.NbtLongArray"),
)
public inline fun NbtLongArray(size: Int, init: (index: Int) -> Long): NbtLongArray = NbtLongArray(size, init)

@Deprecated(
    "Moved to net.benwoodworth.knbt.nbtLongArrayOf",
    ReplaceWith("nbtLongArrayOf(*elements)", "net.benwoodworth.knbt.nbtLongArrayOf"),
)
public fun nbtLongArrayOf(vararg elements: Long): NbtLongArray = nbtLongArrayOf(*elements)

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtLongArray",
    ReplaceWith("this.toNbtLongArray()", "net.benwoodworth.knbt.toNbtLongArray"),
)
public fun LongArray.toNbtLongArray(): NbtLongArray = toNbtLongArray()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtLongArray",
    ReplaceWith("this.toNbtLongArray()", "net.benwoodworth.knbt.toNbtLongArray"),
)
public fun Collection<Long>.toNbtLongArray(): NbtLongArray = toNbtLongArray()
