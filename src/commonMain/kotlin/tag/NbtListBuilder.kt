package net.benwoodworth.knbt.tag

import net.benwoodworth.knbt.NbtDslMarker
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.NbtTagType.TAG_End
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmName

@NbtDslMarker
public class NbtListBuilder<T : NbtTag> @PublishedApi internal constructor() {
    private val elements by lazy { ArrayList<T>() }
    private var elementType: NbtTagType = TAG_End
    private var built = false

    internal fun addInternal(element: T) {
        if (built) throw UnsupportedOperationException("List has already been built")

        if (elementType == TAG_End) {
            elementType = element.type
        } else {
            require(element.type == elementType) { "Cannot add a ${element.type} to a list of $elementType" }
        }

        elements.add(element)
    }

    @PublishedApi
    internal fun build(): NbtList<T> {
        built = true
        return if (elementType == TAG_End) {
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
public fun <T : NbtTag> NbtListBuilder<NbtCompound<T>>.add(element: NbtCompound<T>): Unit = addInternal(element)
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

public inline fun NbtListBuilder<NbtCompound<NbtTag>>.addNbtCompound(
    builderAction: NbtCompoundBuilder<NbtTag>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return add(buildNbtCompound(builderAction))
}

@JvmName("addNbtCompound\$T")
public inline fun <T : NbtTag> NbtListBuilder<NbtCompound<T>>.addNbtCompound(
    builderAction: NbtCompoundBuilder<T>.() -> Unit,
) {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    return add(buildNbtCompound(builderAction))
}
