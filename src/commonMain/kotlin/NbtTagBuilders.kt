package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.NbtTagType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName

/**
 * Build an [NbtTag] suitable for being written to an NBT file.
 *
 * @return a [name]d [NbtCompound] built using the [builderAction].
 */
public inline fun buildNbt(
    name: String,
    builderAction: NbtCompoundBuilder.() -> Unit,
): NbtCompound =
    buildNbtCompound {
        putNbtCompound(name, builderAction)
    }

//region NbtListBuilder
@NbtDslMarker
public class NbtListBuilder<T : NbtTag> @PublishedApi internal constructor(size: Int = -1) {
    private val elements by lazy {
        if (size >= 0) ArrayList<T>(size) else ArrayList<T>()
    }

    private var elementType: NbtTagType = NbtTagType.TAG_End
    private var built = false

    internal fun addInternal(element: T) {
        if (built) throw UnsupportedOperationException("List has already been built")

        if (elementType == NbtTagType.TAG_End) {
            elementType = element.type
        } else {
            require(element.type == elementType) { "Cannot add a ${element.type} to a list of $elementType" }
        }

        elements.add(element)
    }

    @PublishedApi
    internal fun build(): NbtList<T> {
        built = true
        return if (elementType == NbtTagType.TAG_End) {
            NbtList.empty
        } else {
            NbtList(elements)
        }
    }
}

public inline fun <T : NbtTag> buildNbtList(
    builderAction: NbtListBuilder<T>.() -> Unit,
): NbtList<T> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return NbtListBuilder<T>().apply(builderAction).build()
}

public fun NbtListBuilder<NbtByte>.add(element: NbtByte): Unit = addInternal(element)
public fun NbtListBuilder<NbtByteArray>.add(element: NbtByteArray): Unit = addInternal(element)
public fun NbtListBuilder<NbtCompound>.add(element: NbtCompound): Unit = addInternal(element)
public fun NbtListBuilder<NbtDouble>.add(element: NbtDouble): Unit = addInternal(element)
public fun NbtListBuilder<NbtFloat>.add(element: NbtFloat): Unit = addInternal(element)
public fun NbtListBuilder<NbtInt>.add(element: NbtInt): Unit = addInternal(element)
public fun NbtListBuilder<NbtIntArray>.add(element: NbtIntArray): Unit = addInternal(element)
public fun <T : NbtTag> NbtListBuilder<NbtList<T>>.add(element: NbtList<T>): Unit = addInternal(element)
public fun NbtListBuilder<NbtLong>.add(element: NbtLong): Unit = addInternal(element)
public fun NbtListBuilder<NbtLongArray>.add(element: NbtLongArray): Unit = addInternal(element)
public fun NbtListBuilder<NbtShort>.add(element: NbtShort): Unit = addInternal(element)
public fun NbtListBuilder<NbtString>.add(element: NbtString): Unit = addInternal(element)

public fun NbtListBuilder<NbtByte>.add(element: Byte): Unit = addInternal(NbtByte(element))
public fun NbtListBuilder<NbtShort>.add(element: Short): Unit = addInternal(NbtShort(element))
public fun NbtListBuilder<NbtInt>.add(element: Int): Unit = addInternal(NbtInt(element))
public fun NbtListBuilder<NbtLong>.add(element: Long): Unit = addInternal(NbtLong(element))
public fun NbtListBuilder<NbtFloat>.add(element: Float): Unit = addInternal(NbtFloat(element))
public fun NbtListBuilder<NbtDouble>.add(element: Double): Unit = addInternal(NbtDouble(element))
public fun NbtListBuilder<NbtByteArray>.add(element: ByteArray): Unit = addInternal(NbtByteArray(element))
public fun NbtListBuilder<NbtString>.add(element: String): Unit = addInternal(NbtString(element))
public fun NbtListBuilder<NbtIntArray>.add(element: IntArray): Unit = addInternal(NbtIntArray(element))
public fun NbtListBuilder<NbtLongArray>.add(element: LongArray): Unit = addInternal(NbtLongArray(element))

public inline fun <T : NbtTag> NbtListBuilder<NbtList<NbtTag>>.addNbtList(
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return add(buildNbtList(builderAction))
}

@JvmName("addNbtList\$T")
public inline fun <T : NbtTag> NbtListBuilder<NbtList<T>>.addNbtList(
    builderAction: NbtListBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return add(buildNbtList(builderAction))
}

public inline fun NbtListBuilder<NbtCompound>.addNbtCompound(
    builderAction: NbtCompoundBuilder.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return add(buildNbtCompound(builderAction))
}
//endregion

//region NbtCompoundBuilder
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
//endregion
