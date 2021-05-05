package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtDslMarker
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName

@NbtDslMarker
public class NbtCompoundBuilder<T : NbtTag> @PublishedApi internal constructor() {
    private val tags by lazy { LinkedHashMap<String, T>() }
    private var empty = true
    private var built = false

    public fun put(key: String, value: T) {
        if (built) throw UnsupportedOperationException("Compound has already been built")
        empty = false
        tags[key] = value
    }

    @PublishedApi
    internal fun build(): NbtCompound<T> {
        built = true
        return if (empty) NbtCompound.empty else NbtCompound(tags)
    }
}

public inline fun buildNbtCompound(builderAction: NbtCompoundBuilder<NbtTag>.() -> Unit): NbtCompound<NbtTag> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return NbtCompoundBuilder<NbtTag>().apply(builderAction).build()
}

@JvmName("buildNbtCompound\$T")
public inline fun <T : NbtTag> buildNbtCompound(builderAction: NbtCompoundBuilder<T>.() -> Unit): NbtCompound<T> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return NbtCompoundBuilder<T>().apply(builderAction).build()
}

public fun NbtCompoundBuilder<in NbtByte>.put(key: String, value: Byte): Unit = put(key, NbtByte(value))
public fun NbtCompoundBuilder<in NbtShort>.put(key: String, value: Short): Unit = put(key, NbtShort(value))
public fun NbtCompoundBuilder<in NbtInt>.put(key: String, value: Int): Unit = put(key, NbtInt(value))
public fun NbtCompoundBuilder<in NbtLong>.put(key: String, value: Long): Unit = put(key, NbtLong(value))
public fun NbtCompoundBuilder<in NbtFloat>.put(key: String, value: Float): Unit = put(key, NbtFloat(value))
public fun NbtCompoundBuilder<in NbtDouble>.put(key: String, value: Double): Unit = put(key, NbtDouble(value))
public fun NbtCompoundBuilder<in NbtByteArray>.put(key: String, value: ByteArray): Unit = put(key, NbtByteArray(value))
public fun NbtCompoundBuilder<in NbtString>.put(key: String, value: String): Unit = put(key, NbtString(value))
public fun NbtCompoundBuilder<in NbtIntArray>.put(key: String, value: IntArray): Unit = put(key, NbtIntArray(value))
public fun NbtCompoundBuilder<in NbtLongArray>.put(key: String, value: LongArray): Unit = put(key, NbtLongArray(value))

// Avoids overload resolution ambiguity with Int literals:
// - NbtCompoundBuilder<in NbtInt>.put(key: String, value: Int)
// - NbtCompoundBuilder<in NbtLong>.put(key: String, value: Long)
@JvmName("putInt")
public fun NbtCompoundBuilder<in NbtTag>.put(key: String, value: Int): Unit = put(key, NbtInt(value))

public inline fun NbtCompoundBuilder<in NbtList<NbtTag>>.putNbtList(
    key: String,
    builderAction: NbtListBuilder<NbtList<NbtTag>>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return put(key, buildNbtList(builderAction))
}

@JvmName("putNbtList\$T")
public inline fun <T : NbtTag> NbtCompoundBuilder<in NbtList<T>>.putNbtList(
    key: String,
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return put(key, buildNbtList(builderAction))
}

public inline fun <T : NbtTag> NbtCompoundBuilder<in NbtCompound<T>>.putNbtCompound(
    key: String,
    builderAction: NbtCompoundBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return put(key, buildNbtCompound(builderAction))
}

@JvmName("putNbtCompound\$T")
public inline fun NbtCompoundBuilder<in NbtTag>.putNbtCompound(
    key: String,
    builderAction: NbtCompoundBuilder<NbtTag>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return put(key, buildNbtCompound(builderAction))
}
