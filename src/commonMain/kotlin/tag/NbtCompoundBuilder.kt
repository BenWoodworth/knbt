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
)
public typealias NbtCompoundBuilder = NbtCompoundBuilder

@Deprecated(
    "Moved to net.benwoodworth.knbt.buildNbtCompound",
    ReplaceWith("buildNbtCompound(builderAction)", "net.benwoodworth.knbt.buildNbtCompound")
)
public inline fun buildNbtCompound(builderAction: NbtCompoundBuilder.() -> Unit): NbtCompound {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return buildNbtCompound(builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: Byte): Unit = put(key, value)

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: Short): Unit = put(key, value)

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: Int): Unit = put(key, value)

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: Long): Unit = put(key, value)

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: Float): Unit = put(key, value)

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: Double): Unit = put(key, value)

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: ByteArray): Unit = put(key, value)

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: String): Unit = put(key, value)

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: IntArray): Unit = put(key, value)

@Deprecated(
    "Moved to net.benwoodworth.knbt.put",
    ReplaceWith("this.put(key, value)", "net.benwoodworth.knbt.put"),
)
public fun NbtCompoundBuilder.put(key: String, value: LongArray): Unit = put(key, NbtLongArray(value))

@Deprecated(
    "Moved to net.benwoodworth.knbt.putNbtList",
    ReplaceWith("this.putNbtList(key, builderAction)", "net.benwoodworth.knbt.putNbtList"),
)
public inline fun NbtCompoundBuilder.putNbtList(
    key: String,
    builderAction: NbtListBuilder<NbtList<NbtTag>>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return putNbtList(key, builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.putNbtList",
    ReplaceWith("this.putNbtList<T>(key, builderAction)", "net.benwoodworth.knbt.putNbtList"),
)
@JvmName("putNbtList\$T")
public inline fun <T : NbtTag> NbtCompoundBuilder.putNbtList(
    key: String,
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return putNbtList(key, builderAction)
}

@Deprecated(
    "Moved to net.benwoodworth.knbt.putNbtCompound",
    ReplaceWith("this.putNbtCompound(key, builderAction)", "net.benwoodworth.knbt.putNbtCompound"),
)
public inline fun NbtCompoundBuilder.putNbtCompound(
    key: String,
    builderAction: NbtCompoundBuilder.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return putNbtCompound(key, builderAction)
}
