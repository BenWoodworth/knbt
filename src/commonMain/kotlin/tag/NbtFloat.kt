package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtFloat
import net.benwoodworth.knbt.toFloat
import net.benwoodworth.knbt.toNbtFloat

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtFloat",
    ReplaceWith("NbtFloat", "net.benwoodworth.knbt.NbtFloat"),
)
public typealias NbtFloat = NbtFloat

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtFloat",
    ReplaceWith("this.toNbtFloat()", "net.benwoodworth.knbt.toNbtFloat"),
)
public fun Float.toNbtFloat(): NbtFloat = toNbtFloat()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toFloat",
    ReplaceWith("this.toFloat()", "net.benwoodworth.knbt.toFloat"),
)
public fun NbtFloat.toFloat(): Float = toFloat()
