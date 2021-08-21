package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.NbtTagType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName

//region NbtListBuilder
@NbtDslMarker
public class NbtListBuilder<T : NbtTag> @PublishedApi internal constructor(size: Int = -1) {
    private val elements by lazy {
        if (size >= 0) ArrayList<T>(size) else ArrayList<T>()
    }

    private var elementType: NbtTagType = NbtTagType.TAG_End
    private var built = false

    @PublishedApi
    internal fun add(tag: T): Boolean {
        if (built) throw UnsupportedOperationException("List has already been built")

        if (elementType == NbtTagType.TAG_End) {
            elementType = tag.type
        } else {
            require(tag.type == elementType) { "Cannot add a ${tag.type} to a list of $elementType" }
        }

        elements.add(tag)
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

/**
 * Build an [NbtList] suitable for being written to an NBT file.
 *
 * @return a [name]d [NbtList] built using the [builderAction].
 */
@OptIn(ExperimentalTypeInference::class)
public inline fun <T : NbtTag> buildNbtList(
    name: String,
    @BuilderInference builderAction: NbtListBuilder<T>.() -> Unit,
): NbtCompound =
    buildNbtCompound {
        putNbtList(name, builderAction)
    }

public fun NbtListBuilder<NbtByte>.add(tag: NbtByte): Boolean = add(tag)
public fun NbtListBuilder<NbtByteArray>.add(tag: NbtByteArray): Boolean = add(tag)
public fun NbtListBuilder<NbtCompound>.add(tag: NbtCompound): Boolean = add(tag)
public fun NbtListBuilder<NbtDouble>.add(tag: NbtDouble): Boolean = add(tag)
public fun NbtListBuilder<NbtFloat>.add(tag: NbtFloat): Boolean = add(tag)
public fun NbtListBuilder<NbtInt>.add(tag: NbtInt): Boolean = add(tag)
public fun NbtListBuilder<NbtIntArray>.add(tag: NbtIntArray): Boolean = add(tag)
public fun <T : NbtTag> NbtListBuilder<NbtList<T>>.add(tag: NbtList<T>): Boolean = add(tag)
public fun NbtListBuilder<NbtLong>.add(tag: NbtLong): Boolean = add(tag)
public fun NbtListBuilder<NbtLongArray>.add(tag: NbtLongArray): Boolean = add(tag)
public fun NbtListBuilder<NbtShort>.add(tag: NbtShort): Boolean = add(tag)
public fun NbtListBuilder<NbtString>.add(tag: NbtString): Boolean = add(tag)

public fun NbtListBuilder<NbtByte>.add(value: Byte): Boolean = add(NbtByte(value))
public fun NbtListBuilder<NbtShort>.add(value: Short): Boolean = add(NbtShort(value))
public fun NbtListBuilder<NbtInt>.add(value: Int): Boolean = add(NbtInt(value))
public fun NbtListBuilder<NbtLong>.add(value: Long): Boolean = add(NbtLong(value))
public fun NbtListBuilder<NbtFloat>.add(value: Float): Boolean = add(NbtFloat(value))
public fun NbtListBuilder<NbtDouble>.add(value: Double): Boolean = add(NbtDouble(value))
public fun NbtListBuilder<NbtByteArray>.add(value: ByteArray): Boolean = add(NbtByteArray(value))
public fun NbtListBuilder<NbtString>.add(value: String): Boolean = add(NbtString(value))
public fun NbtListBuilder<NbtIntArray>.add(value: IntArray): Boolean = add(NbtIntArray(value))
public fun NbtListBuilder<NbtLongArray>.add(value: LongArray): Boolean = add(NbtLongArray(value))

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

    public fun put(key: String, tag: NbtTag): NbtTag? {
        if (built) throw UnsupportedOperationException("Compound has already been built")
        empty = false
        return tags.put(key, tag)
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

/**
 * Build an [NbtCompound] suitable for being written to an NBT file.
 *
 * @return a [name]d [NbtCompound] built using the [builderAction].
 */
public inline fun buildNbtCompound(
    name: String,
    builderAction: NbtCompoundBuilder.() -> Unit,
): NbtCompound =
    buildNbtCompound {
        putNbtCompound(name, builderAction)
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
