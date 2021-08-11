package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.toNbtString

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtString",
    ReplaceWith("NbtString", "net.benwoodworth.knbt.NbtString"),
)
public typealias NbtString = NbtString

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtString",
    ReplaceWith("this.toNbtString()", "net.benwoodworth.knbt.toNbtString"),
)
public fun String.toNbtString(): NbtString = toNbtString()

