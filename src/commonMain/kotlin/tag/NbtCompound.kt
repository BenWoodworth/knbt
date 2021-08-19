package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtDouble
import net.benwoodworth.knbt.NbtFloat
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtLong
import net.benwoodworth.knbt.NbtLongArray
import net.benwoodworth.knbt.NbtShort
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtTag
import kotlin.jvm.JvmName

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtCompound",
    ReplaceWith("NbtCompound", "net.benwoodworth.knbt.NbtCompound"),
)
public typealias NbtCompound = NbtCompound

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith("NbtCompound(emptyMap())", "net.benwoodworth.knbt.NbtCompound"),
)
public fun nbtCompoundOf(): NbtCompound = NbtCompound(emptyMap())

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith("NbtCompound(mapOf(*pairs))", "net.benwoodworth.knbt.NbtCompound"),
)
public fun nbtCompoundOf(vararg pairs: Pair<String, NbtTag>): NbtCompound = NbtCompound(mapOf(*pairs))

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith("NbtCompound(this)", "net.benwoodworth.knbt.toNbtCompound"),
)
public fun Map<String, NbtTag>.toNbtCompound(): NbtCompound = NbtCompound(this)

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { (_, value) -> NbtByte(value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtByte",
    ),
)
@JvmName("toNbtCompound\$Byte")
public fun Map<String, Byte>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { (_, value) -> NbtByte(value) })

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { NbtShort(it.value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtShort",
    ),
)
@JvmName("toNbtCompound\$Short")
public fun Map<String, Short>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { NbtShort(it.value) })

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { (_, value) -> NbtInt(value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtInt",
    ),
)
@JvmName("toNbtCompound\$Int")
public fun Map<String, Int>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { (_, value) -> NbtInt(value) })

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { NbtLong(it.value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtLong",
    ),
)
@JvmName("toNbtCompound\$Long")
public fun Map<String, Long>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { NbtLong(it.value) })

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { NbtFloat(it.value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtFloat",
    ),
)
@JvmName("toNbtCompound\$Float")
public fun Map<String, Float>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { NbtFloat(it.value) })

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { NbtDouble(it.value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtDouble",
    ),
)
@JvmName("toNbtCompound\$Double")
public fun Map<String, Double>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { NbtDouble(it.value) })

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { NbtString(it.value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtString",
    ),
)
@JvmName("toNbtCompound\$String")
public fun Map<String, String>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { NbtString(it.value) })

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { NbtByteArray(it.value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtByteArray",
    ),
)
@JvmName("toNbtCompound\$ByteArray")
public fun Map<String, ByteArray>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { NbtByteArray(it.value) })

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { NbtIntArray(it.value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtIntArray",
    ),
)
@JvmName("toNbtCompound\$IntArray")
public fun Map<String, IntArray>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { NbtIntArray(it.value) })

@Deprecated(
    "Replaced with NbtCompound constructor",
    ReplaceWith(
        "NbtCompound(this.mapValues { NbtLongArray(it.value) })",
        "net.benwoodworth.knbt.NbtCompound", "net.benwoodworth.knbt.NbtLongArray",
    ),
)
@JvmName("toNbtCompound\$LongArray")
public fun Map<String, LongArray>.toNbtCompound(): NbtCompound =
    NbtCompound(this.mapValues { NbtLongArray(it.value) })
