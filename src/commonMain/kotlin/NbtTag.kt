package net.benwoodworth.knbt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.appendNbtString
import net.benwoodworth.knbt.internal.toNbtString
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
        private val serializer = PolymorphicSerializer(NbtTag::class)

        public fun serializer(): KSerializer<NbtTag> = serializer
    }
}

@JvmInline
@Serializable(with = NbtByteSerializer::class)
public value class NbtByte(public val value: Byte) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Byte

    /**
     * Create an [NbtByte] containing a [Boolean]: `false = 0b`, `true = 1b`
     */
    public constructor(booleanValue: Boolean) : this(if (booleanValue) 1 else 0)

    /**
     * Get an [NbtByte] as a [Boolean]: `0b = false`, `1b = true`
     * @throws IllegalArgumentException if this is not `0b` or `1b`
     */
    public val booleanValue: Boolean
        get() = when (value) {
            0.toByte() -> false
            1.toByte() -> true
            else -> throw IllegalArgumentException("Expected value to be a boolean (0 or 1), but was $value")
        }

    override fun toString(): String = "${value}b"
}

@JvmInline
@Serializable(NbtShortSerializer::class)
public value class NbtShort(public val value: Short) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Short

    override fun toString(): String = "${value}s"
}

@JvmInline
@Serializable(NbtIntSerializer::class)
public value class NbtInt(public val value: Int) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Int

    override fun toString(): String = value.toString()
}

@JvmInline
@Serializable(NbtLongSerializer::class)
public value class NbtLong(public val value: Long) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Long

    override fun toString(): String = "${value}L"
}

@JvmInline
@Serializable(NbtFloatSerializer::class)
public value class NbtFloat(public val value: Float) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Float

    override fun toString(): String = "${value}f"
}

@JvmInline
@Serializable(NbtDoubleSerializer::class)
public value class NbtDouble(public val value: Double) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_Double

    override fun toString(): String = "${value}d"
}

@Serializable(NbtByteArraySerializer::class)
public class NbtByteArray private constructor(
    internal val content: ByteArray,
    private val list: List<Byte>,
) : NbtTag, List<Byte> by list {
    override val type: NbtTagType get() = NbtTagType.TAG_Byte_Array

    public constructor(content: ByteArray) : this(content, content.asList())

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtByteArray && content.contentEquals(other.content)
        else -> list == other
    }

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String =
        content.joinToString(separator = ",", prefix = "[B;", postfix = "]") { "${it}B" }

    override fun iterator(): ByteIterator = content.iterator()
}

@JvmInline
@Serializable(NbtStringSerializer::class)
public value class NbtString(public val value: String) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_String

    override fun toString(): String = value.toNbtString(true)
}

@Serializable(NbtListSerializer::class)
public class NbtList<out T : NbtTag> private constructor(
    private val content: List<T>,
) : NbtTag, List<T> by content {
    override val type: NbtTagType get() = NbtTagType.TAG_List

    internal val elementType: NbtTagType
        get() = if (isEmpty()) NbtTagType.TAG_End else first().type

    override fun equals(other: Any?): Boolean =
        if (content.isEmpty() && other is NbtTag) {
            other is NbtList<*> && other.isEmpty()
        } else {
            content == other
        }

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String =
        content.joinToString(prefix = "[", postfix = "]", separator = ",")

    public companion object {
        @UnsafeNbtApi
        internal operator fun <T : NbtTag> invoke(content: List<T>): NbtList<T> = NbtList(content)

        // Specific constructors, since NbtLists can only contain a single tag type
        @JvmName("invoke\$NbtByte")
        public operator fun invoke(content: List<NbtByte>): NbtList<NbtByte> = NbtList(content)

        @JvmName("invoke\$NbtShort")
        public operator fun invoke(content: List<NbtShort>): NbtList<NbtShort> = NbtList(content)

        @JvmName("invoke\$NbtInt")
        public operator fun invoke(content: List<NbtInt>): NbtList<NbtInt> = NbtList(content)

        @JvmName("invoke\$NbtLong")
        public operator fun invoke(content: List<NbtLong>): NbtList<NbtLong> = NbtList(content)

        @JvmName("invoke\$NbtFloat")
        public operator fun invoke(content: List<NbtFloat>): NbtList<NbtFloat> = NbtList(content)

        @JvmName("invoke\$NbtDouble")
        public operator fun invoke(content: List<NbtDouble>): NbtList<NbtDouble> = NbtList(content)

        @JvmName("invoke\$NbtByteArray")
        public operator fun invoke(content: List<NbtByteArray>): NbtList<NbtByteArray> = NbtList(content)

        @JvmName("invoke\$NbtString")
        public operator fun invoke(content: List<NbtString>): NbtList<NbtString> = NbtList(content)

        @JvmName("invoke\$NbtList")
        public operator fun invoke(content: List<NbtList<*>>): NbtList<NbtList<*>> = NbtList(content)

        @JvmName("invoke\$NbtCompound")
        public operator fun invoke(content: List<NbtCompound>): NbtList<NbtCompound> = NbtList(content)

        @JvmName("invoke\$NbtIntArray")
        public operator fun invoke(content: List<NbtIntArray>): NbtList<NbtIntArray> = NbtList(content)

        @JvmName("invoke\$NbtLongArray")
        public operator fun invoke(content: List<NbtLongArray>): NbtList<NbtLongArray> = NbtList(content)
    }
}

@Serializable(NbtCompoundSerializer::class)
public class NbtCompound(
    private val content: Map<String, NbtTag>,
) : NbtTag, Map<String, NbtTag> by content {
    override val type: NbtTagType get() = NbtTagType.TAG_Compound

    override fun equals(other: Any?): Boolean = content == other

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String =
        content.entries.joinToString(separator = ",", prefix = "{", postfix = "}") { (name, value) ->
            buildString {
                appendNbtString(name)
                append(':')
                append(value)
            }
        }
}

@Serializable(NbtIntArraySerializer::class)
public class NbtIntArray private constructor(
    internal val content: IntArray,
    private val list: List<Int>,
) : NbtTag, List<Int> by list {
    override val type: NbtTagType get() = NbtTagType.TAG_Int_Array

    public constructor(content: IntArray) : this(content, content.asList())

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtIntArray && content.contentEquals(other.content)
        else -> list == other
    }

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String =
        content.joinToString(separator = ",", prefix = "[I;", postfix = "]")

    override fun iterator(): IntIterator = content.iterator()
}

@Serializable(NbtLongArraySerializer::class)
public class NbtLongArray private constructor(
    internal val content: LongArray,
    private val list: List<Long>,
) : NbtTag, List<Long> by list {
    override val type: NbtTagType get() = NbtTagType.TAG_Long_Array

    public constructor(content: LongArray) : this(content, content.asList())

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other is NbtTag -> other is NbtLongArray && content.contentEquals(other.content)
        else -> list == other
    }

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String =
        content.joinToString(separator = ",", prefix = "[L;", postfix = "]") { "${it}L" }

    override fun iterator(): LongIterator = content.iterator()
}

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
