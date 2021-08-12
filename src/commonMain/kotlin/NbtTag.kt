package net.benwoodworth.knbt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.internal.NbtTagType
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

@Polymorphic
public sealed interface NbtTag {
    /**
     * For internal use only. Will be marked as internal once Kotlin supports it on sealed interface members.
     * @suppress
     */
    @InternalNbtApi
    public val type: NbtTagType // TODO Make internal

    public companion object {
        public fun serializer(): KSerializer<NbtTag> = PolymorphicSerializer(NbtTag::class)
    }
}

@JvmInline
@Serializable(with = NbtByteSerializer::class)
public value class NbtByte internal constructor(internal val value: Byte) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Byte

    override fun toString(): String = value.toString()
}

@JvmInline
@Serializable(NbtShortSerializer::class)
public value class NbtShort internal constructor(internal val value: Short) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Short

    override fun toString(): String = value.toString()
}

@JvmInline
@Serializable(NbtIntSerializer::class)
public value class NbtInt internal constructor(internal val value: Int) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Int

    override fun toString(): String = value.toString()
}

@JvmInline
@Serializable(NbtLongSerializer::class)
public value class NbtLong internal constructor(internal val value: Long) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Long

    override fun toString(): String = value.toString()
}

@JvmInline
@Serializable(NbtFloatSerializer::class)
public value class NbtFloat internal constructor(internal val value: Float) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Float

    override fun toString(): String = value.toString()
}

@JvmInline
@Serializable(NbtDoubleSerializer::class)
public value class NbtDouble internal constructor(internal val value: Double) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Double

    override fun toString(): String = value.toString()
}

@Suppress("OVERRIDE_BY_INLINE")
@Serializable(NbtByteArraySerializer::class)
public class NbtByteArray private constructor(
    internal val value: ByteArray,
    private val list: List<Byte>,
) : NbtTag, List<Byte> by list {
    override val type: NbtTagType get() = NbtTagType.TAG_Byte_Array

    @PublishedApi
    internal constructor(value: ByteArray) : this(value, value.asList())

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtByteArray && value.contentEquals(other.value)
        else -> list == other
    }

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = value.contentToString()
}

@JvmInline
@Serializable(NbtStringSerializer::class)
public value class NbtString internal constructor(internal val value: String) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_String

    override fun toString(): String = value
}

@Serializable(NbtListSerializer::class)
public class NbtList<out T : NbtTag> internal constructor(
    internal val value: List<T>,
) : NbtTag, List<T> by value {
    override val type: NbtTagType get() = NbtTagType.TAG_List

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtList<*> && value == other.value
        else -> value == other
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()

    public companion object {
        internal val empty: NbtList<Nothing> = NbtList(emptyList())
    }
}

@Serializable(NbtCompoundSerializer::class)
public class NbtCompound internal constructor(
    internal val value: Map<String, NbtTag>
) : NbtTag, Map<String, NbtTag> by value {

    override val type: NbtTagType get() = NbtTagType.TAG_Compound

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtCompound && value == other.value
        else -> value == other
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value.toString()

    public companion object {
        internal val empty = NbtCompound(emptyMap())
    }
}

@Suppress("OVERRIDE_BY_INLINE")
@Serializable(NbtIntArraySerializer::class)
public class NbtIntArray private constructor(
    internal val value: IntArray,
    private val list: List<Int>,
) : NbtTag, List<Int> by list {
    override val type: NbtTagType get() = NbtTagType.TAG_Int_Array

    @PublishedApi
    internal constructor(value: IntArray) : this(value, value.asList())

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtIntArray && value.contentEquals(other.value)
        else -> list == other
    }

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = value.contentToString()
}

@Suppress("OVERRIDE_BY_INLINE")
@Serializable(NbtLongArraySerializer::class)
public class NbtLongArray private constructor(
    internal val value: LongArray,
    private val list: List<Long>,
) : NbtTag, List<Long> by list {
    override val type: NbtTagType get() = NbtTagType.TAG_Long_Array

    @PublishedApi
    internal constructor(value: LongArray) : this(value, value.asList())

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtLongArray && value.contentEquals(other.value)
        else -> list == other
    }

    override fun hashCode(): Int = value.contentHashCode()

    override fun toString(): String = value.contentToString()
}


public fun NbtByte.toByte(): Byte = value
public fun Int.toNbtByte(): NbtByte = NbtByte(toByte())
public fun Byte.toNbtByte(): NbtByte = NbtByte(this)

public fun Short.toNbtShort(): NbtShort = NbtShort(this)
public fun Int.toNbtShort(): NbtShort = NbtShort(toShort())
public fun NbtShort.toShort(): Short = value

public fun Int.toNbtInt(): NbtInt = NbtInt(this)
public fun NbtInt.toInt(): Int = value

public fun Long.toNbtLong(): NbtLong = NbtLong(this)
public fun Int.toNbtLong(): NbtLong = NbtLong(toLong())
public fun NbtLong.toLong(): Long = value

public fun Float.toNbtFloat(): NbtFloat = NbtFloat(this)
public fun NbtFloat.toFloat(): Float = value

public fun Double.toNbtDouble(): NbtDouble = NbtDouble(this)
public fun NbtDouble.toDouble(): Double = value

//region NbtByteArray helpers
public inline fun NbtByteArray(size: Int, init: (index: Int) -> Byte): NbtByteArray =
    NbtByteArray(ByteArray(size) { index -> init(index) })

public fun nbtByteArrayOf(vararg elements: Byte): NbtByteArray = NbtByteArray(elements)

public fun ByteArray.toNbtByteArray(): NbtByteArray = NbtByteArray(this.copyOf())
public fun Collection<Byte>.toNbtByteArray(): NbtByteArray = NbtByteArray(this.toByteArray())
//endregion

public fun String.toNbtString(): NbtString = NbtString(this)

//region NbtList helpers
internal val NbtList<*>.elementType: NbtTagType
    get() = if (value.isEmpty()) NbtTagType.TAG_End else value.first().type

public fun <T : NbtTag> nbtListOf(): NbtList<T> = NbtList.empty

public fun <T : NbtTag> nbtListOf(vararg elements: T): NbtList<T> =
    if (elements.isEmpty()) NbtList.empty else NbtList(elements.asList())

public fun <T : NbtTag> List<T>.toNbtList(): NbtList<T> = when (size) {
    0 -> NbtList.empty
    1 -> NbtList(listOf(first()))
    else -> {
        var elementType = NbtTagType.TAG_End
        val elements = map { element ->
            if (elementType == NbtTagType.TAG_End) {
                elementType = element.type
            } else {
                require(element.type == elementType) { "NbtList elements must all have the same type" }
            }
            element
        }
        NbtList(elements)
    }
}

@JvmName("toNbtList\$Byte")
public fun List<Byte>.toNbtList(): NbtList<NbtByte> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtByte() })

@JvmName("toNbtList\$Short")
public fun List<Short>.toNbtList(): NbtList<NbtShort> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtShort() })

@JvmName("toNbtList\$Int")
public fun List<Int>.toNbtList(): NbtList<NbtInt> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtInt() })

@JvmName("toNbtList\$Long")
public fun List<Long>.toNbtList(): NbtList<NbtLong> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtLong() })

@JvmName("toNbtList\$Float")
public fun List<Float>.toNbtList(): NbtList<NbtFloat> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtFloat() })

@JvmName("toNbtList\$Double")
public fun List<Double>.toNbtList(): NbtList<NbtDouble> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtDouble() })

@JvmName("toNbtList\$String")
public fun List<String>.toNbtList(): NbtList<NbtString> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtString() })

@JvmName("toNbtList\$ByteArray")
public fun List<ByteArray>.toNbtList(): NbtList<NbtByteArray> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtByteArray() })

@JvmName("toNbtList\$IntArray")
public fun List<IntArray>.toNbtList(): NbtList<NbtIntArray> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtIntArray() })

@JvmName("toNbtList\$LongArray")
public fun List<LongArray>.toNbtList(): NbtList<NbtLongArray> =
    if (size == 0) NbtList.empty else NbtList(map { it.toNbtLongArray() })
//endregion

//region NbtCompound helpers
public fun nbtCompoundOf(): NbtCompound = NbtCompound.empty

public fun nbtCompoundOf(vararg pairs: Pair<String, NbtTag>): NbtCompound =
    if (pairs.isEmpty()) NbtCompound.empty else NbtCompound(linkedMapOf(*pairs))

public fun Map<String, NbtTag>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(this.toMap())

@JvmName("toNbtCompound\$Byte")
public fun Map<String, Byte>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtByte() })

@JvmName("toNbtCompound\$Short")
public fun Map<String, Short>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtShort() })

@JvmName("toNbtCompound\$Int")
public fun Map<String, Int>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtInt() })

@JvmName("toNbtCompound\$Long")
public fun Map<String, Long>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtLong() })

@JvmName("toNbtCompound\$Float")
public fun Map<String, Float>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtFloat() })

@JvmName("toNbtCompound\$Double")
public fun Map<String, Double>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtDouble() })

@JvmName("toNbtCompound\$String")
public fun Map<String, String>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtString() })

@JvmName("toNbtCompound\$ByteArray")
public fun Map<String, ByteArray>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtByteArray() })

@JvmName("toNbtCompound\$IntArray")
public fun Map<String, IntArray>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtIntArray() })

@JvmName("toNbtCompound\$LongArray")
public fun Map<String, LongArray>.toNbtCompound(): NbtCompound =
    if (isEmpty()) NbtCompound.empty else NbtCompound(mapValues { it.value.toNbtLongArray() })
//endregion

//region NbtIntArray helpers
public inline fun NbtIntArray(size: Int, init: (index: Int) -> Int): NbtIntArray =
    NbtIntArray(IntArray(size) { index -> init(index) })

public fun nbtIntArrayOf(vararg elements: Int): NbtIntArray = NbtIntArray(elements)

public fun IntArray.toNbtIntArray(): NbtIntArray = NbtIntArray(this.copyOf())
public fun Collection<Int>.toNbtIntArray(): NbtIntArray = NbtIntArray(this.toIntArray())
//endregion

//region NbtLongArray helpers
public inline fun NbtLongArray(size: Int, init: (index: Int) -> Long): NbtLongArray =
    NbtLongArray(LongArray(size) { index -> init(index) })

public fun nbtLongArrayOf(vararg elements: Long): NbtLongArray = NbtLongArray(elements)

public fun LongArray.toNbtLongArray(): NbtLongArray = NbtLongArray(this.copyOf())
public fun Collection<Long>.toNbtLongArray(): NbtLongArray = NbtLongArray(this.toLongArray())
//endregion

//region NbtTag casting methods
private inline fun <reified T : NbtTag> NbtTag.cast(): T =
    this as? T ?: throw IllegalArgumentException("Element ${this::class.simpleName} is not an ${T::class.simpleName}")

/**
 * Convenience method to get this element as an [NbtByte]
 * @throws IllegalArgumentException if this element is not an [NbtByte]
 */
public val NbtTag.nbtByte: NbtByte get() = cast()

/**
 * Convenience method to get this element as an [NbtShort]
 * @throws IllegalArgumentException if this element is not an [NbtShort]
 */
public val NbtTag.nbtShort: NbtShort get() = cast()

/**
 * Convenience method to get this element as an [NbtInt]
 * @throws IllegalArgumentException if this element is not an [NbtInt]
 */
public val NbtTag.nbtInt: NbtInt get() = cast()

/**
 * Convenience method to get this element as an [NbtLong]
 * @throws IllegalArgumentException if this element is not an [NbtLong]
 */
public val NbtTag.nbtLong: NbtLong get() = cast()

/**
 * Convenience method to get this element as an [NbtFloat]
 * @throws IllegalArgumentException if this element is not an [NbtFloat]
 */
public val NbtTag.nbtFloat: NbtFloat get() = cast()

/**
 * Convenience method to get this element as an [NbtDouble]
 * @throws IllegalArgumentException if this element is not an [NbtDouble]
 */
public val NbtTag.nbtDouble: NbtDouble get() = cast()

/**
 * Convenience method to get this element as an [NbtByteArray]
 * @throws IllegalArgumentException if this element is not an [NbtByteArray]
 */
public val NbtTag.nbtByteArray: NbtByteArray get() = cast()

/**
 * Convenience method to get this element as an [NbtString]
 * @throws IllegalArgumentException if this element is not an [NbtString]
 */
public val NbtTag.nbtString: NbtString get() = cast()

/**
 * Convenience method to get this element as an [NbtList]
 * @throws IllegalArgumentException if this element is not an [NbtList]
 */
public val NbtTag.nbtList: NbtList<*> get() = cast()

/**
 * Convenience method to get this element as an [NbtCompound]
 * @throws IllegalArgumentException if this element is not an [NbtCompound]
 */
public val NbtTag.nbtCompound: NbtCompound get() = cast()

/**
 * Convenience method to get this element as an [NbtIntArray]
 * @throws IllegalArgumentException if this element is not an [NbtIntArray]
 */
public val NbtTag.nbtIntArray: NbtIntArray get() = cast()

/**
 * Convenience method to get this element as an [NbtLongArray]
 * @throws IllegalArgumentException if this element is not an [NbtLongArray]
 */
public val NbtTag.nbtLongArray: NbtLongArray get() = cast()

/**
 * Convenience method to get this element as an [NbtList]<[T]>
 * @throws IllegalArgumentException if this element is not an [NbtList]<[T]>
 */
@ExperimentalNbtApi
public inline fun <reified T : NbtTag> NbtTag.nbtList(): NbtList<T> = nbtList(T::class)

@PublishedApi
@Suppress("UNCHECKED_CAST")
internal fun <T : NbtTag> NbtTag.nbtList(type: KClass<T>): NbtList<T> = when {
    this !is NbtList<*> -> {
        throw IllegalArgumentException("Element ${this::class.simpleName} is not an NbtList<${type.simpleName}>")
    }
    isNotEmpty() && !type.isInstance(first()) -> {
        throw IllegalArgumentException("Element NbtList<${first()::class.simpleName}> is not an NbtList<${type.simpleName}>")
    }
    else -> this as NbtList<T>
}
//endregion
