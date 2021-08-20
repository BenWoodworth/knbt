package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtByteArray

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtByteArray",
    ReplaceWith("NbtByteArray", "net.benwoodworth.knbt.NbtByteArray"),
)
public typealias NbtByteArray = NbtByteArray

@Deprecated(
    "Replaced with NbtByteArray constructor",
    ReplaceWith("NbtByteArray(ByteArray(size, init))", "net.benwoodworth.knbt.NbtByteArray"),
)
public inline fun NbtByteArray(size: Int, init: (index: Int) -> Byte): NbtByteArray =
    NbtByteArray(ByteArray(size) { init(it) })

@Deprecated(
    "Replaced with NbtByteArray constructor",
    ReplaceWith("NbtByteArray(byteArrayOf(*elements))", "net.benwoodworth.knbt.NbtByteArray"),
)
public fun nbtByteArrayOf(vararg elements: Byte): NbtByteArray = NbtByteArray(byteArrayOf(*elements))

@Deprecated(
    "Replaced with NbtByteArray constructor",
    ReplaceWith("NbtByteArray(this)", "net.benwoodworth.knbt.NbtByteArray"),
)
public fun ByteArray.toNbtByteArray(): NbtByteArray = NbtByteArray(this)

@Deprecated(
    "Replaced with NbtByteArray constructor",
    ReplaceWith("NbtByteArray(this.toByteArray())", "net.benwoodworth.knbt.NbtByteArray"),
)
public fun Collection<Byte>.toNbtByteArray(): NbtByteArray = NbtByteArray(this.toByteArray())
