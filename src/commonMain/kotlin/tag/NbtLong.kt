package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtLong
import net.benwoodworth.knbt.toLong
import net.benwoodworth.knbt.toNbtLong

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtLong",
    ReplaceWith("NbtLong", "net.benwoodworth.knbt.NbtLong"),
)
public typealias NbtLong = NbtLong

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtLong",
    ReplaceWith("this.toNbtLong()", "net.benwoodworth.knbt.toNbtLong"),
)
public fun Long.toNbtLong(): NbtLong = toNbtLong()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtLong",
    ReplaceWith("this.toNbtLong()", "net.benwoodworth.knbt.toNbtLong"),
)
public fun Int.toNbtLong(): NbtLong = toNbtLong()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toLong",
    ReplaceWith("this.toLong()", "net.benwoodworth.knbt.toLong"),
)
public fun NbtLong.toLong(): Long = toLong()

