package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtDouble

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtDouble",
    ReplaceWith("NbtDouble", "net.benwoodworth.knbt.NbtDouble"),
)
public typealias NbtDouble = NbtDouble

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtDouble",
    ReplaceWith("NbtDouble(this)", "net.benwoodworth.knbt.NbtDouble"),
)
public fun Double.toNbtDouble(): NbtDouble = NbtDouble(this)

@Deprecated(
    "Moved to net.benwoodworth.knbt.toDouble",
    ReplaceWith("this.value"),
)
public fun NbtDouble.toDouble(): Double = this.value
