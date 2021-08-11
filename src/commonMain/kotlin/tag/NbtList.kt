package net.benwoodworth.knbt.tag

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
import net.benwoodworth.knbt.nbtListOf
import net.benwoodworth.knbt.toNbtList
import kotlin.jvm.JvmName

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtList<T>",
    ReplaceWith("NbtList<T>", "net.benwoodworth.knbt.NbtList<T>"),
)
public typealias NbtList<T> = NbtList<T>

@Deprecated(
    "Moved to net.benwoodworth.knbt.nbtListOf",
    ReplaceWith("nbtListOf()", "net.benwoodworth.knbt.nbtListOf"),
)
public fun <T : NbtTag> nbtListOf(): NbtList<T> = nbtListOf()

@Deprecated(
    "Moved to net.benwoodworth.knbt.nbtListOf",
    ReplaceWith("nbtListOf(*elements)", "net.benwoodworth.knbt.nbtListOf"),
)
public fun <T : NbtTag> nbtListOf(vararg elements: T): NbtList<T> = nbtListOf(*elements)

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
public fun <T : NbtTag> List<T>.toNbtList(): NbtList<T> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$Byte")
public fun List<Byte>.toNbtList(): NbtList<NbtByte> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$Short")
public fun List<Short>.toNbtList(): NbtList<NbtShort> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$Int")
public fun List<Int>.toNbtList(): NbtList<NbtInt> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$Long")
public fun List<Long>.toNbtList(): NbtList<NbtLong> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$Float")
public fun List<Float>.toNbtList(): NbtList<NbtFloat> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$Double")
public fun List<Double>.toNbtList(): NbtList<NbtDouble> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$String")
public fun List<String>.toNbtList(): NbtList<NbtString> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$ByteArray")
public fun List<ByteArray>.toNbtList(): NbtList<NbtByteArray> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$IntArray")
public fun List<IntArray>.toNbtList(): NbtList<NbtIntArray> = toNbtList()

@Deprecated(
    "Moved to net.benwoodworth.knbt.toNbtList",
    ReplaceWith("this.toNbtList()", "net.benwoodworth.knbt.toNbtList"),
)
@JvmName("toNbtList\$LongArray")
public fun List<LongArray>.toNbtList(): NbtList<NbtLongArray> = toNbtList()
