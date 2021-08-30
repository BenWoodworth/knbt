package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtString

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtString",
    ReplaceWith("NbtString", "net.benwoodworth.knbt.NbtString"),
    DeprecationLevel.ERROR,
)
public typealias NbtString = NbtString

@Deprecated(
    "Replaced with NbtString constructor",
    ReplaceWith("NbtString(this)", "net.benwoodworth.knbt.NbtString"),
    DeprecationLevel.ERROR,
)
public fun String.toNbtString(): NbtString = NbtString(this)

