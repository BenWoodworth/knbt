package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtCompoundBuilder
import net.benwoodworth.knbt.NbtList
import net.benwoodworth.knbt.NbtListBuilder
import net.benwoodworth.knbt.NbtLongArray
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.putNbtCompound
import net.benwoodworth.knbt.putNbtList
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName

@Deprecated(
    "Moved to net.benwoodworth.knbt.NbtCompoundBuilder",
    ReplaceWith("NbtCompoundBuilder", "net.benwoodworth.knbt.NbtCompoundBuilder"),
    DeprecationLevel.ERROR,
)
public typealias NbtCompoundBuilder = NbtCompoundBuilder

@Deprecated(
    "Moved to net.benwoodworth.knbt.buildNbtCompound",
    ReplaceWith("buildNbtCompound(builderAction)", "net.benwoodworth.knbt.buildNbtCompound"),
    DeprecationLevel.ERROR,
)
public inline fun buildNbtCompound(builderAction: NbtCompoundBuilder.() -> Unit): NbtCompound {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return buildNbtCompound(builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: Byte) {
    put(key, value)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: Short) {
    put(key, value)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: Int) {
    put(key, value)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: Long) {
    put(key, value)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: Float) {
    put(key, value)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: Double) {
    put(key, value)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: ByteArray) {
    put(key, value)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: String) {
    put(key, value)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: IntArray) {
    put(key, value)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
    DeprecationLevel.ERROR,
)
public fun NbtCompoundBuilder.put(key: String, value: LongArray) {
    put(key, NbtLongArray(value))
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.putNbtList",
    ReplaceWith("this.putNbtList(key, builderAction)", "net.benwoodworth.knbt.putNbtList"),
    DeprecationLevel.ERROR,
)
public inline fun NbtCompoundBuilder.putNbtList(
    key: String,
    builderAction: NbtListBuilder<NbtList<NbtTag>>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    putNbtList(key, builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.putNbtList",
    ReplaceWith("this.putNbtList<T>(key, builderAction)", "net.benwoodworth.knbt.putNbtList"),
    DeprecationLevel.ERROR,
)
@JvmName("putNbtList\$T")
public inline fun <T : NbtTag> NbtCompoundBuilder.putNbtList(
    key: String,
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    putNbtList(key, builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.putNbtCompound",
    ReplaceWith("this.putNbtCompound(key, builderAction)", "net.benwoodworth.knbt.putNbtCompound"),
    DeprecationLevel.ERROR,
)
public inline fun NbtCompoundBuilder.putNbtCompound(
    key: String,
    builderAction: NbtCompoundBuilder.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    putNbtCompound(key, builderAction)
}
