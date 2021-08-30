package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtInt

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtInt",
    ReplaceWith("NbtInt", "net.benwoodworth.knbt.NbtInt"),
    DeprecationLevel.ERROR,
)
public typealias NbtInt = NbtInt

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtInt",
    ReplaceWith("NbtInt(this)", "net.benwoodworth.knbt.NbtInt"),
    DeprecationLevel.ERROR,
)
public fun Int.toNbtInt(): NbtInt = NbtInt(this)

@Deprecated(
    "Moved to net.benwoodworth.knbt.toInt",
    ReplaceWith("this.value"),
    DeprecationLevel.ERROR,
)
public fun NbtInt.toInt(): Int = this.value
