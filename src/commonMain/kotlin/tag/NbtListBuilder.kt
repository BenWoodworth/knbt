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
    DeprecationLevel.ERROR,
)
public typealias NbtListBuilder<T> = NbtListBuilder<T>

@Deprecated(
    "Moved to net.benwoodworth.knbt.buildNbtList",
    ReplaceWith("buildNbtList(builderAction)", "net.benwoodworth.knbt.buildNbtList"),
    DeprecationLevel.ERROR,
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
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtByte>.add(element: NbtByte) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtByteArray>.add(element: NbtByteArray) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtCompound>.add(element: NbtCompound) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtDouble>.add(element: NbtDouble) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtFloat>.add(element: NbtFloat) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtInt>.add(element: NbtInt) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtIntArray>.add(element: NbtIntArray) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun <T : NbtTag> NbtListBuilder<NbtList<T>>.add(element: NbtList<T>) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtLong>.add(element: NbtLong) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtLongArray>.add(element: NbtLongArray) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtShort>.add(element: NbtShort) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtString>.add(element: NbtString) {
    add(element)
}


@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtByte>.add(element: Byte) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtShort>.add(element: Short) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtInt>.add(element: Int) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtLong>.add(element: Long) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtFloat>.add(element: Float) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtDouble>.add(element: Double) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtByteArray>.add(element: ByteArray) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtString>.add(element: String) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtIntArray>.add(element: IntArray) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.add",
    ReplaceWith("add(element)", "net.benwoodworth.knbt.add"),
    DeprecationLevel.ERROR,
)
public fun NbtListBuilder<NbtLongArray>.add(element: LongArray) {
    add(element)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.addNbtList",
    ReplaceWith("addNbtList<T>(builderAction)", "net.benwoodworth.knbt.addNbtList"),
    DeprecationLevel.ERROR,
)
public inline fun <T : NbtTag> NbtListBuilder<NbtList<NbtTag>>.addNbtList(
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    addNbtList(builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.addNbtList",
    ReplaceWith("addNbtList<T>(builderAction)", "net.benwoodworth.knbt.addNbtList"),
    DeprecationLevel.ERROR,
)
@JvmName("addNbtList\$T")
public inline fun <T : NbtTag> NbtListBuilder<NbtList<T>>.addNbtList(
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    addNbtList(builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.addNbtCompound",
    ReplaceWith("addNbtCompound(builderAction)", "net.benwoodworth.knbt.addNbtCompound"),
    DeprecationLevel.ERROR,
)
public inline fun NbtListBuilder<NbtCompound>.addNbtCompound(
    builderAction: NbtCompoundBuilder.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    addNbtCompound(builderAction)
}
