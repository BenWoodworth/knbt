package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.nbtByteArrayOf
import net.benwoodworth.knbt.toNbtByteArray

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtByteArray",
    ReplaceWith("NbtByteArray", "net.benwoodworth.knbt.NbtByteArray"),
)
public typealias NbtByteArray = NbtByteArray

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtByteArray",
    ReplaceWith("NbtByteArray(size, init)", "net.benwoodworth.knbt.NbtByteArray"),
)
public inline fun NbtByteArray(size: Int, init: (index: Int) -> Byte): NbtByteArray = NbtByteArray(size, init)

@Deprecated(
    "Moved to net.benwoodworth.knbt.nbtByteArrayOf",
    ReplaceWith("nbtByteArrayOf(*elements)", "net.benwoodworth.knbt.nbtByteArrayOf"),
)
public fun nbtByteArrayOf(vararg elements: Byte): NbtByteArray = nbtByteArrayOf(*elements)

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtByteArray",
    ReplaceWith("this.toNbtByteArray()", "net.benwoodworth.knbt.toNbtByteArray"),
)
public fun ByteArray.toNbtByteArray(): NbtByteArray = toNbtByteArray()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtByteArray",
    ReplaceWith("this.toNbtByteArray()", "net.benwoodworth.knbt.toNbtByteArray"),
)
public fun Collection<Byte>.toNbtByteArray(): NbtByteArray = toNbtByteArray()
