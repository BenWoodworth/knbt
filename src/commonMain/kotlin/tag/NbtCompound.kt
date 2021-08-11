package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.nbtCompoundOf
import net.benwoodworth.knbt.toNbtCompound
import kotlin.jvm.JvmName

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtCompound",
    ReplaceWith("NbtCompound", "net.benwoodworth.knbt.NbtCompound"),
)
public typealias NbtCompound = NbtCompound

@Deprecated(
    "Moved to net.benwoodworth.knbt.nbtCompoundOf",
    ReplaceWith("nbtCompoundOf()", "net.benwoodworth.knbt.nbtCompoundOf"),
)
public fun nbtCompoundOf(): NbtCompound = nbtCompoundOf()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("nbtCompoundOf(*pairs)", "net.benwoodworth.knbt.nbtCompoundOf"),
)
public fun nbtCompoundOf(vararg pairs: Pair<String, NbtTag>): NbtCompound = nbtCompoundOf(*pairs)

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
public fun Map<String, NbtTag>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$Byte")
public fun Map<String, Byte>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$Short")
public fun Map<String, Short>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$Int")
public fun Map<String, Int>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$Long")
public fun Map<String, Long>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$Float")
public fun Map<String, Float>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$Double")
public fun Map<String, Double>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$String")
public fun Map<String, String>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$ByteArray")
public fun Map<String, ByteArray>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$IntArray")
public fun Map<String, IntArray>.toNbtCompound(): NbtCompound = toNbtCompound()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtCompound",
    ReplaceWith("this.toNbtCompound()", "net.benwoodworth.knbt.toNbtCompound"),
)
@JvmName("toNbtCompound\$LongArray")
public fun Map<String, LongArray>.toNbtCompound(): NbtCompound = toNbtCompound()
