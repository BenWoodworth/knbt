package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtDouble
import net.benwoodworth.knbt.toDouble
import net.benwoodworth.knbt.toNbtDouble

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtDouble",
    ReplaceWith("NbtDouble", "net.benwoodworth.knbt.NbtDouble"),
)
public typealias NbtDouble = NbtDouble

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtDouble",
    ReplaceWith("this.toNbtDouble()", "net.benwoodworth.knbt.toNbtDouble"),
)
public fun Double.toNbtDouble(): NbtDouble = this.toNbtDouble()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toDouble",
    ReplaceWith("this.toDouble()", "net.benwoodworth.knbt.toDouble"),
)
public fun NbtDouble.toDouble(): Double = this.toDouble()
