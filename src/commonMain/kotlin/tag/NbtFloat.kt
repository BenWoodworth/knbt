package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtFloat

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtFloat",
    ReplaceWith("NbtFloat", "net.benwoodworth.knbt.NbtFloat"),
)
public typealias NbtFloat = NbtFloat

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtFloat",
    ReplaceWith("NbtFloat(this)", "net.benwoodworth.knbt.NbtFloat"),
)
public fun Float.toNbtFloat(): NbtFloat = NbtFloat(this)

@Deprecated(
    "Moved to net.benwoodworth.knbt.toFloat",
    ReplaceWith("this.value"),
)
public fun NbtFloat.toFloat(): Float = this.value
