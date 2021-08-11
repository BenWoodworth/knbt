package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.toInt
import net.benwoodworth.knbt.toNbtInt

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtInt",
    ReplaceWith("NbtInt", "net.benwoodworth.knbt.NbtInt"),
)
public typealias NbtInt = NbtInt

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtInt",
    ReplaceWith("this.toNbtInt()", "net.benwoodworth.knbt.toNbtInt"),
)
public fun Int.toNbtInt(): NbtInt = toNbtInt()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toInt",
    ReplaceWith("this.toInt()", "net.benwoodworth.knbt.toInt"),
)
public fun NbtInt.toInt(): Int = toInt()
