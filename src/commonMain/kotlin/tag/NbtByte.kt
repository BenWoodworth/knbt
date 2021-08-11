package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.toByte
import net.benwoodworth.knbt.toNbtByte

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtByte",
    ReplaceWith("NbtByte", "net.benwoodworth.knbt.NbtByte"),
)
public typealias NbtByte = NbtByte

@Deprecated(
    "Moved to net.benwoodworth.knbt.toByte",
    ReplaceWith("this.toByte()", "net.benwoodworth.knbt.toByte"),
)
public fun NbtByte.toByte(): Byte = toByte()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtByte",
    ReplaceWith("this.toNbtByte()", "net.benwoodworth.knbt.toNbtByte"),
)
public fun Int.toNbtByte(): NbtByte = toNbtByte()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtByte",
    ReplaceWith("this.toNbtByte()", "net.benwoodworth.knbt.toNbtByte"),
)
public fun Byte.toNbtByte(): NbtByte = toNbtByte()
