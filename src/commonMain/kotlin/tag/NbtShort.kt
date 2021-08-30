package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtShort

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtShort",
    ReplaceWith("NbtShort", "net.benwoodworth.knbt.NbtShort"),
    DeprecationLevel.ERROR,
)
public typealias NbtShort = NbtShort

@Deprecated(
    "Replaced with NbtShort.value",
    ReplaceWith("this.value"),
    DeprecationLevel.ERROR,
)
public fun NbtShort.toShort(): Short = this.value

@Deprecated(
    "Replaced with NbtShort constructor",
    ReplaceWith("NbtShort(this.toShort())", "net.benwoodworth.knbt.NbtShort"),
    DeprecationLevel.ERROR,
)
public fun Int.toNbtShort(): NbtShort = NbtShort(this.toShort())

@Deprecated(
    "Replaced with NbtShort constructor",
    ReplaceWith("NbtShort(this)", "net.benwoodworth.knbt.NbtShort"),
    DeprecationLevel.ERROR,
)
public fun Short.toNbtShort(): NbtShort = NbtShort(this)
