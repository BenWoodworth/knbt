package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtDouble
import net.benwoodworth.knbt.NbtFloat
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtList
import net.benwoodworth.knbt.NbtListBuilder
import net.benwoodworth.knbt.NbtLong
import net.benwoodworth.knbt.NbtLongArray
import net.benwoodworth.knbt.NbtShort
import net.benwoodworth.knbt.NbtString
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.add
import net.benwoodworth.knbt.addNbtCompound
import net.benwoodworth.knbt.addNbtList
import net.benwoodworth.knbt.buildNbtList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtListBuilder",
    ReplaceWith("NbtListBuilder<T>", "net.benwoodworth.knbt.NbtListBuilder"),
)
public typealias NbtListBuilder<T> = NbtListBuilder<T>

@Deprecated(
    "Moved to net.benwoodworth.knbt.buildNbtList",
    ReplaceWith("buildNbtList(builderAction)", "net.benwoodworth.knbt.buildNbtList"),
)
public inline fun <T : NbtTag> buildNbtList(
    builderAction: NbtListBuilder<T>.() -> Unit,
): NbtList<T> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return buildNbtList(builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtByte>.add(element: NbtByte): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtByteArray>.add(element: NbtByteArray): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtCompound>.add(element: NbtCompound): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtDouble>.add(element: NbtDouble): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtFloat>.add(element: NbtFloat): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtInt>.add(element: NbtInt): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtIntArray>.add(element: NbtIntArray): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun <T : NbtTag> NbtListBuilder<NbtList<T>>.add(element: NbtList<T>): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtLong>.add(element: NbtLong): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtLongArray>.add(element: NbtLongArray): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtShort>.add(element: NbtShort): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtString>.add(element: NbtString): Unit = add(element)


@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtByte>.add(element: Byte): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtShort>.add(element: Short): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtInt>.add(element: Int): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtLong>.add(element: Long): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtFloat>.add(element: Float): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtDouble>.add(element: Double): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtByteArray>.add(element: ByteArray): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtString>.add(element: String): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtIntArray>.add(element: IntArray): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
)
public fun NbtListBuilder<NbtLongArray>.add(element: LongArray): Unit = add(element)

@Deprecated(
    "Moved to net.benwoodworth.knbt.addNbtList",
    ReplaceWith("addNbtList(element)", "net.benwoodworth.knbt.addNbtList"),
)
public inline fun <T : NbtTag> NbtListBuilder<NbtList<NbtTag>>.addNbtList(
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return addNbtList(builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.addNbtList",
    ReplaceWith("addNbtList(element)", "net.benwoodworth.knbt.addNbtList"),
)
@JvmName("addNbtList\$T")
public inline fun <T : NbtTag> NbtListBuilder<NbtList<T>>.addNbtList(
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return addNbtList(builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.addNbtCompound",
    ReplaceWith("addNbtCompound(element)", "net.benwoodworth.knbt.addNbtCompound"),
)
public inline fun NbtListBuilder<NbtCompound>.addNbtCompound(
    builderAction: NbtCompoundBuilder.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return addNbtCompound(builderAction)
}
