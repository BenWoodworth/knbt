package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtDslMarker
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName

@NbtDslMarker
public class NbtCompoundBuilder @PublishedApi internal constructor() {
    private val tags by lazy { LinkedHashMap<String, NbtTag>() }
    private var empty = true
    private var built = false

    public fun put(key: String, value: NbtTag) {
        if (built) throw UnsupportedOperationException("Compound has already been built")
        empty = false
        tags[key] = value
    }

    @PublishedApi
    internal fun build(): NbtCompound {
        built = true
        return if (empty) NbtCompound.empty else NbtCompound(tags)
    }
}

public inline fun buildNbtCompound(builderAction: NbtCompoundBuilder.() -> Unit): NbtCompound {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return NbtCompoundBuilder().apply(builderAction).build()
}

public fun NbtCompoundBuilder.put(key: String, value: Byte): Unit = put(key, NbtByte(value))
public fun NbtCompoundBuilder.put(key: String, value: Short): Unit = put(key, NbtShort(value))
public fun NbtCompoundBuilder.put(key: String, value: Int): Unit = put(key, NbtInt(value))
public fun NbtCompoundBuilder.put(key: String, value: Long): Unit = put(key, NbtLong(value))
public fun NbtCompoundBuilder.put(key: String, value: Float): Unit = put(key, NbtFloat(value))
public fun NbtCompoundBuilder.put(key: String, value: Double): Unit = put(key, NbtDouble(value))
public fun NbtCompoundBuilder.put(key: String, value: ByteArray): Unit = put(key, NbtByteArray(value))
public fun NbtCompoundBuilder.put(key: String, value: String): Unit = put(key, NbtString(value))
public fun NbtCompoundBuilder.put(key: String, value: IntArray): Unit = put(key, NbtIntArray(value))
public fun NbtCompoundBuilder.put(key: String, value: LongArray): Unit = put(key, NbtLongArray(value))

public inline fun NbtCompoundBuilder.putNbtList(
    key: String,
    builderAction: NbtListBuilder<NbtList<NbtTag>>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return put(key, buildNbtList(builderAction))
}

@JvmName("putNbtList\$T")
public inline fun <T : NbtTag> NbtCompoundBuilder.putNbtList(
    key: String,
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return put(key, buildNbtList(builderAction))
}

public inline fun NbtCompoundBuilder.putNbtCompound(
    key: String,
    builderAction: NbtCompoundBuilder.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return put(key, buildNbtCompound(builderAction))
}
