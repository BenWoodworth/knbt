package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtLong

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtLong",
    ReplaceWith("NbtLong", "net.benwoodworth.knbt.NbtLong"),
)
public typealias NbtLong = NbtLong

@Deprecated(
    "Replaced with NbtLong constructor",
    ReplaceWith("NbtLong(this)", "net.benwoodworth.knbt.NbtLong"),
)
public fun Long.toNbtLong(): NbtLong = NbtLong(this)

@Deprecated(
    "Replaced with NbtLong constructor",
    ReplaceWith("NbtLong(this.toLong())", "net.benwoodworth.knbt.NbtLong"),
)
public fun Int.toNbtLong(): NbtLong = NbtLong(this.toLong())

@Deprecated(
    "Replaced with NbtLong constructor",
    ReplaceWith("this.value"),
)
public fun NbtLong.toLong(): Long = this.value

