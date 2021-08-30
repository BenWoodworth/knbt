package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtDouble
import net.benwoodworth.knbt.NbtFloat
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtList
import net.benwoodworth.knbt.NbtLong
import net.benwoodworth.knbt.NbtLongArray
import net.benwoodworth.knbt.NbtShort
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtTag
import kotlin.jvm.JvmName

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtList<T>",
    ReplaceWith("NbtList<T>", "net.benwoodworth.knbt.NbtList<T>"),
    DeprecationLevel.ERROR,
)
public typealias NbtList<T> = NbtList<T>

@OptIn(UnsafeNbtApi::class)
@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith("NbtList(emptyList<T>())", "net.benwoodworth.knbt.NbtList"),
    DeprecationLevel.ERROR,
)
public fun <T : NbtTag> nbtListOf(): NbtList<T> =
    NbtList(emptyList<T>())

@OptIn(UnsafeNbtApi::class)
@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith("NbtList(listOf(*elements))", "net.benwoodworth.knbt.NbtList"),
    DeprecationLevel.ERROR,
)
public fun <T : NbtTag> nbtListOf(vararg elements: T): NbtList<T> = NbtList(listOf(*elements))

@OptIn(UnsafeNbtApi::class)
@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith("NbtList(this)", "net.benwoodworth.knbt.NbtList"),
    DeprecationLevel.ERROR,
)
public fun <T : NbtTag> List<T>.toNbtList(): NbtList<T> = NbtList(this)

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtByte(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtByte",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$Byte")
public fun List<Byte>.toNbtList(): NbtList<NbtByte> =
    NbtList(this.map { NbtByte(it) })

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtShort(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtShort",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$Short")
public fun List<Short>.toNbtList(): NbtList<NbtShort> =
    NbtList(this.map { NbtShort(it) })

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtInt(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtInt",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$Int")
public fun List<Int>.toNbtList(): NbtList<NbtInt> =
    NbtList(this.map { NbtInt(it) })

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtLong(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtLong",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$Long")
public fun List<Long>.toNbtList(): NbtList<NbtLong> =
    NbtList(this.map { NbtLong(it) })

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtFloat(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtFloat",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$Float")
public fun List<Float>.toNbtList(): NbtList<NbtFloat> =
    NbtList(this.map { NbtFloat(it) })

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtDouble(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtDouble",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$Double")
public fun List<Double>.toNbtList(): NbtList<NbtDouble> =
    NbtList(this.map { NbtDouble(it) })

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtString(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtString",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$String")
public fun List<String>.toNbtList(): NbtList<NbtString> =
    NbtList(this.map { NbtString(it) })

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtByteArray(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtByteArray",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$ByteArray")
public fun List<ByteArray>.toNbtList(): NbtList<NbtByteArray> =
    NbtList(this.map { NbtByteArray(it) })

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtIntArray(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtIntArray",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$IntArray")
public fun List<IntArray>.toNbtList(): NbtList<NbtIntArray> =
    NbtList(this.map { NbtIntArray(it) })

@Deprecated(
    "Replaced with NbtList constructor",
    ReplaceWith(
        "NbtList(this.map { NbtLongArray(it) })",
        "net.benwoodworth.knbt.NbtList", "net.benwoodworth.knbt.NbtLongArray",
    ),
    DeprecationLevel.ERROR,
)
@JvmName("toNbtList\$LongArray")
public fun List<LongArray>.toNbtList(): NbtList<NbtLongArray> =
    NbtList(this.map { NbtLongArray(it) })
