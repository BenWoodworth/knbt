package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtByte

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtByte",
    ReplaceWith("NbtByte", "net.benwoodworth.knbt.NbtByte"),
)
public typealias NbtByte = NbtByte

@Deprecated(
    "Replaced with NbtByte.value",
    ReplaceWith("this.value"),
)
public fun NbtByte.toByte(): Byte = this.value

@Deprecated(
    "Replaced with NbtByte constructor",
    ReplaceWith("NbtByte(this.toByte())", "net.benwoodworth.knbt.NbtByte"),
)
public fun Int.toNbtByte(): NbtByte = NbtByte(this.toByte())

@Deprecated(
    "Replaced with NbtByte constructor",
    ReplaceWith("NbtByte(this)", "net.benwoodworth.knbt.NbtByte"),
)
public fun Byte.toNbtByte(): NbtByte = NbtByte(this)
