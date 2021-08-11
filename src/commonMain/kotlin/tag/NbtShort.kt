package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtShort
import net.benwoodworth.knbt.toNbtShort
import net.benwoodworth.knbt.toShort

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtShort",
    ReplaceWith("NbtShort", "net.benwoodworth.knbt.NbtShort"),
)
public typealias NbtShort = NbtShort

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtShort",
    ReplaceWith("this.toNbtShort()", "net.benwoodworth.knbt.toNbtShort"),
)
public fun Short.toNbtShort(): NbtShort = toNbtShort()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtShort",
    ReplaceWith("this.toNbtShort()", "net.benwoodworth.knbt.toNbtShort"),
)
public fun Int.toNbtShort(): NbtShort = toNbtShort()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toShort",
    ReplaceWith("this.toShort()", "net.benwoodworth.knbt.toShort"),
)
public fun NbtShort.toShort(): Short = toShort()

