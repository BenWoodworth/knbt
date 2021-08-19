package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.NbtTagType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
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

    internal fun addInternal(element: T): Boolean {
        if (built) throw UnsupportedOperationException("List has already been built")

        if (elementType == NbtTagType.TAG_End) {
            elementType = element.type
        } else {
            require(element.type == elementType) { "Cannot add a ${element.type} to a list of $elementType" }
        }

        elements.add(element)
        return true
    }

    @PublishedApi
    @Suppress("UNCHECKED_CAST")
    @OptIn(UnsafeNbtApi::class)
    internal fun build(): NbtList<T> {
        built = true
        return NbtList(elements)
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <T : NbtTag> buildNbtList(
    @BuilderInference builderAction: NbtListBuilder<T>.() -> Unit,
): NbtList<T> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return NbtListBuilder<T>().apply(builderAction).build()
}

public fun NbtListBuilder<NbtByte>.add(element: NbtByte): Boolean = addInternal(element)
public fun NbtListBuilder<NbtByteArray>.add(element: NbtByteArray): Boolean = addInternal(element)
public fun NbtListBuilder<NbtCompound>.add(element: NbtCompound): Boolean = addInternal(element)
public fun NbtListBuilder<NbtDouble>.add(element: NbtDouble): Boolean = addInternal(element)
public fun NbtListBuilder<NbtFloat>.add(element: NbtFloat): Boolean = addInternal(element)
public fun NbtListBuilder<NbtInt>.add(element: NbtInt): Boolean = addInternal(element)
public fun NbtListBuilder<NbtIntArray>.add(element: NbtIntArray): Boolean = addInternal(element)
public fun <T : NbtTag> NbtListBuilder<NbtList<T>>.add(element: NbtList<T>): Boolean = addInternal(element)
public fun NbtListBuilder<NbtLong>.add(element: NbtLong): Boolean = addInternal(element)
public fun NbtListBuilder<NbtLongArray>.add(element: NbtLongArray): Boolean = addInternal(element)
public fun NbtListBuilder<NbtShort>.add(element: NbtShort): Boolean = addInternal(element)
public fun NbtListBuilder<NbtString>.add(element: NbtString): Boolean = addInternal(element)

public fun NbtListBuilder<NbtByte>.add(element: Byte): Boolean = addInternal(NbtByte(element))
public fun NbtListBuilder<NbtShort>.add(element: Short): Boolean = addInternal(NbtShort(element))
public fun NbtListBuilder<NbtInt>.add(element: Int): Boolean = addInternal(NbtInt(element))
public fun NbtListBuilder<NbtLong>.add(element: Long): Boolean = addInternal(NbtLong(element))
public fun NbtListBuilder<NbtFloat>.add(element: Float): Boolean = addInternal(NbtFloat(element))
public fun NbtListBuilder<NbtDouble>.add(element: Double): Boolean = addInternal(NbtDouble(element))
public fun NbtListBuilder<NbtByteArray>.add(element: ByteArray): Boolean = addInternal(NbtByteArray(element))
public fun NbtListBuilder<NbtString>.add(element: String): Boolean = addInternal(NbtString(element))
public fun NbtListBuilder<NbtIntArray>.add(element: IntArray): Boolean = addInternal(NbtIntArray(element))
public fun NbtListBuilder<NbtLongArray>.add(element: LongArray): Boolean = addInternal(NbtLongArray(element))

@OptIn(ExperimentalTypeInference::class)
public inline fun <T : NbtTag> NbtListBuilder<NbtList<NbtTag>>.addNbtList(
    @BuilderInference builderAction: NbtListBuilder<T>.() -> Unit,
): Boolean {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return add(buildNbtList(builderAction))
}

@OptIn(ExperimentalTypeInference::class)
@JvmName("addNbtList\$T")
public inline fun <T : NbtTag> NbtListBuilder<NbtList<T>>.addNbtList(
    @BuilderInference builderAction: NbtListBuilder<T>.() -> Unit,
): Boolean {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return add(buildNbtList(builderAction))
}

public inline fun NbtListBuilder<NbtCompound>.addNbtCompound(
    builderAction: NbtCompoundBuilder.() -> Unit,
): Boolean {
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

    public fun put(key: String, value: NbtTag): NbtTag? {
        if (built) throw UnsupportedOperationException("Compound has already been built")
        empty = false
        return tags.put(key, value)
    }

    @PublishedApi
    internal fun build(): NbtCompound {
        built = true
        return NbtCompound(tags)
    }
}

public inline fun buildNbtCompound(builderAction: NbtCompoundBuilder.() -> Unit): NbtCompound {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return NbtCompoundBuilder().apply(builderAction).build()
}

public fun NbtCompoundBuilder.put(key: String, value: Byte): NbtTag? = put(key, NbtByte(value))
public fun NbtCompoundBuilder.put(key: String, value: Short): NbtTag? = put(key, NbtShort(value))
public fun NbtCompoundBuilder.put(key: String, value: Int): NbtTag? = put(key, NbtInt(value))
public fun NbtCompoundBuilder.put(key: String, value: Long): NbtTag? = put(key, NbtLong(value))
public fun NbtCompoundBuilder.put(key: String, value: Float): NbtTag? = put(key, NbtFloat(value))
public fun NbtCompoundBuilder.put(key: String, value: Double): NbtTag? = put(key, NbtDouble(value))
public fun NbtCompoundBuilder.put(key: String, value: ByteArray): NbtTag? = put(key, NbtByteArray(value))
public fun NbtCompoundBuilder.put(key: String, value: String): NbtTag? = put(key, NbtString(value))
public fun NbtCompoundBuilder.put(key: String, value: IntArray): NbtTag? = put(key, NbtIntArray(value))
public fun NbtCompoundBuilder.put(key: String, value: LongArray): NbtTag? = put(key, NbtLongArray(value))

@OptIn(ExperimentalTypeInference::class)
@JvmName("putNbtList\$T")
public inline fun <T : NbtTag> NbtCompoundBuilder.putNbtList(
    key: String,
    @BuilderInference builderAction: NbtListBuilder<T>.() -> Unit,
): NbtTag? {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return put(key, buildNbtList(builderAction))
}

public inline fun NbtCompoundBuilder.putNbtCompound(
    key: String,
    builderAction: NbtCompoundBuilder.() -> Unit,
): NbtTag? {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return put(key, buildNbtCompound(builderAction))
}
//endregion
