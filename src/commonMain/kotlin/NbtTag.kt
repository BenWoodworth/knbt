package net.benwoodworth.knbt

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.appendNbtString
import net.benwoodworth.knbt.internal.toNbtString
import kotlin.jvm.JvmInline
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

@Serializable(with = NbtTagSerializer::class)
public sealed interface NbtTag {
    /**
     * For internal use only. Will be marked as internal once Kotlin supports it on sealed interface members.
     * @suppress
     */
    @InternalNbtApi
    public val type: NbtTagType // TODO Make internal
}

@JvmInline
@Serializable(with = NbtByteSerializer::class)
public value class NbtByte(public val value: Byte) : NbtTag, @Suppress("DEPRECATION") NbtByteDeprecations {
    override val type: NbtTagType get() = NbtTagType.TAG_Byte

    override fun toString(): String = "${value}b"
}

/**
 * Create an [NbtByte] representing a [Boolean]: `false = 0b`, `true = 1b`.
 */
public fun NbtByte.Companion.fromBoolean(value: Boolean): NbtByte =
    NbtByte(if (value) 1 else 0)

/**
 * Convert [this] [NbtByte] to its [Boolean] representation: `0b = false`, `1b = true`.
 *
 * In order to match Minecraft's lenient behavior, all other values convert to `true`.
 */
public fun NbtByte.toBoolean(): Boolean =
    value != 0.toByte()

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
public class NbtByteArray(public val content: List<Byte>) : NbtTag, @Suppress("DEPRECATION") NbtByteArrayDeprecations() {
    override val type: NbtTagType get() = NbtTagType.TAG_Byte_Array

    public val size: Int
        get() = content.size

    public operator fun get(index: Int): Byte =
        content[index]

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtByteArray && content == other.content)

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String =
        content.joinToString(separator = ",", prefix = "[B;", postfix = "]") { "${it}B" }
}

public fun NbtByteArray.getOrNull(index: Int): Byte? =
    content.getOrNull(index)

@JvmInline
@Serializable(NbtStringSerializer::class)
public value class NbtString(public val value: String) : NbtTag {
    override val type: NbtTagType get() = NbtTagType.TAG_String

    override fun toString(): String = value.toNbtString(true)
}

@Serializable(NbtListSerializer::class)
public class NbtList<out T : NbtTag> private constructor(
    public val content: List<T>,
) : NbtTag, @Suppress("DEPRECATION") NbtListLikeDeprecations<T>() {
    override val type: NbtTagType get() = NbtTagType.TAG_List

    internal val elementType: NbtTagType
        get() = if (content.isEmpty()) NbtTagType.TAG_End else content.first().type

    public val size: Int
        get() = content.size

    public operator fun get(index: Int): T =
        content[index]

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtList<*> && content == other.content)

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String =
        content.joinToString(prefix = "[", postfix = "]", separator = ",")

    @Suppress("FINAL_UPPER_BOUND")
    public companion object {
        @UnsafeNbtApi
        internal operator fun <T : NbtTag> invoke(content: List<T>): NbtList<T> = NbtList(content)

        // Specific constructors, since NbtLists can only contain a single tag type
        @JvmName("invoke\$Nothing")
        @Suppress("CONFLICTING_UPPER_BOUNDS") // https://youtrack.jetbrains.com/issue/KT-57274
        public operator fun <TNothing : Nothing> invoke(content: List<TNothing>): NbtList<TNothing> =
            NbtList(content)

        @JvmName("invoke\$NbtByte")
        public operator fun <TNbtByte : NbtByte> invoke(content: List<TNbtByte>): NbtList<TNbtByte> =
            NbtList(content)

        @JvmName("invoke\$NbtShort")
        public operator fun <TNbtShort : NbtShort> invoke(content: List<TNbtShort>): NbtList<TNbtShort> =
            NbtList(content)

        @JvmName("invoke\$NbtInt")
        public operator fun <TNbtInt : NbtInt> invoke(content: List<TNbtInt>): NbtList<TNbtInt> =
            NbtList(content)

        @JvmName("invoke\$NbtLong")
        public operator fun <TNbtLong : NbtLong> invoke(content: List<TNbtLong>): NbtList<TNbtLong> =
            NbtList(content)

        @JvmName("invoke\$NbtFloat")
        public operator fun <TNbtFloat : NbtFloat> invoke(content: List<TNbtFloat>): NbtList<TNbtFloat> =
            NbtList(content)

        @JvmName("invoke\$NbtDouble")
        public operator fun <TNbtDouble : NbtDouble> invoke(content: List<TNbtDouble>): NbtList<TNbtDouble> =
            NbtList(content)

        @JvmName("invoke\$NbtByteArray")
        public operator fun <TNbtByteArray : NbtByteArray> invoke(content: List<TNbtByteArray>): NbtList<TNbtByteArray> =
            NbtList(content)

        @JvmName("invoke\$NbtString")
        public operator fun <TNbtString : NbtString> invoke(content: List<TNbtString>): NbtList<TNbtString> =
            NbtList(content)

        @JvmName("invoke\$NbtList")
        public operator fun <TNbtList : NbtList<*>> invoke(content: List<TNbtList>): NbtList<TNbtList> =
            NbtList(content)

        @JvmName("invoke\$NbtCompound")
        public operator fun <TNbtCompound : NbtCompound> invoke(content: List<TNbtCompound>): NbtList<TNbtCompound> =
            NbtList(content)

        @JvmName("invoke\$NbtIntArray")
        public operator fun <TNbtIntArray : NbtIntArray> invoke(content: List<TNbtIntArray>): NbtList<TNbtIntArray> =
            NbtList(content)

        @JvmName("invoke\$NbtLongArray")
        public operator fun <TNbtLongArray : NbtLongArray> invoke(content: List<TNbtLongArray>): NbtList<TNbtLongArray> =
            NbtList(content)
    }
}

public fun <T : NbtTag> NbtList<T>.getOrNull(index: Int): T? =
    content.getOrNull(index)

@Serializable(NbtCompoundSerializer::class)
public class NbtCompound(public val content: Map<String, NbtTag>) : NbtTag, @Suppress("DEPRECATION") NbtCompoundDeprecations() {
    override val type: NbtTagType get() = NbtTagType.TAG_Compound

    /**
     * Returns the number of tags in this compound.
     */
    public val size: Int
        get() = content.size

    /**
     * Returns the [tag][NbtTag] in this compound with the given [name].
     *
     * ### knbt v0.12 migration note:
     * [NbtCompound.get] now throws [NoSuchElementException] instead of returning `null`.
     * Opting in will not be needed after the migration release.
     *
     * #### How to migrate:
     * ```
     * compound["name"]!!  // Change to: compound["name"]
     * compound["name"]    // Change to: compound.getOrNull("name")
     * ```
     *
     * @throws NoSuchElementException if this compound does not [contain][contains] a tag with the given [name].
     */
    @`MIGRATION Acknowledge that NbtCompound now has a stricter get` // When removed, also remove optIn from build script
    public override operator fun get(name: String): NbtTag =
        content[name] ?: throw NoSuchElementException("does not contain a tag named \"$name\"")

    /**
     * Returns `true` if this compound contains a tag with the given [name].
     */
    public operator fun contains(name: String): Boolean =
        name in content

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtCompound && content == other.content)

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

/**
 * Returns the [tag][NbtTag] in this compound with the given [name], or `null` if there is no such tag.
 */
public fun NbtCompound.getOrNull(name: String): NbtTag? =
    content[name]

@Serializable(NbtIntArraySerializer::class)
public class NbtIntArray(public val content: List<Int>) : NbtTag, @Suppress("DEPRECATION") NbtIntArrayDeprecations() {
    override val type: NbtTagType get() = NbtTagType.TAG_Int_Array

    public val size: Int
        get() = content.size

    public operator fun get(index: Int): Int =
        content[index]

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtIntArray && content == other.content)

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String =
        content.joinToString(separator = ",", prefix = "[I;", postfix = "]")
}

public fun NbtIntArray.getOrNull(index: Int): Int? =
    content.getOrNull(index)

@Serializable(NbtLongArraySerializer::class)
public class NbtLongArray(public val content: List<Long>) : NbtTag, @Suppress("DEPRECATION") NbtLongArrayDeprecations() {
    override val type: NbtTagType get() = NbtTagType.TAG_Long_Array

    public val size: Int
        get() = content.size

    public operator fun get(index: Int): Long =
        content[index]

    override fun equals(other: Any?): Boolean =
        this === other || (other is NbtLongArray && content == other.content)

    override fun hashCode(): Int = content.hashCode()

    override fun toString(): String =
        content.joinToString(separator = ",", prefix = "[L;", postfix = "]") { "${it}L" }
}

public fun NbtLongArray.getOrNull(index: Int): Long? =
    content.getOrNull(index)

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
    size > 0 && !type.isInstance(this[0]) -> {
        throw IllegalArgumentException("Element NbtList<${this[0]::class.simpleName}> is not an NbtList<${type.simpleName}>")
    }
    else -> this as NbtList<T>
}
//endregion

//region NbtTag primitive convenience properties
/**
 * Returns the value of this tag as [Boolean]
 * @throws IllegalArgumentException if this tag is not an [NbtByte]
 */
public val NbtTag.boolean: Boolean get() = nbtByte.toBoolean()

/**
 * Returns the value of this tag as [Byte]
 * @throws IllegalArgumentException if this tag is not an [NbtByte]
 */
public val NbtTag.byte: Byte get() = nbtByte.value

/**
 * Returns the value of this tag as [Short]
 * @throws IllegalArgumentException if this tag is not an [NbtShort]
 */
public val NbtTag.short: Short get() = nbtShort.value

/**
 * Returns the value of this tag as [Int]
 * @throws IllegalArgumentException if this tag is not an [NbtInt]
 */
public val NbtTag.int: Int get() = nbtInt.value

/**
 * Returns the value of this tag as [Long]
 * @throws IllegalArgumentException if this tag is not an [NbtLong]
 */
public val NbtTag.long: Long get() = nbtLong.value

/**
 * Returns the value of this tag as [Float]
 * @throws IllegalArgumentException if this tag is not an [NbtFloat]
 */
public val NbtTag.float: Float get() = nbtFloat.value

/**
 * Returns the value of this tag as [Double]
 * @throws IllegalArgumentException if this tag is not an [NbtDouble]
 */
public val NbtTag.double: Double get() = nbtDouble.value

/**
 * Returns the value of this tag as [String]
 * @throws IllegalArgumentException if this tag is not an [NbtString]
 */
public val NbtTag.string: String get() = nbtString.value
//endregion

//region NbtTag primitiveOrNull convenience properties
/**
 * Returns the value of this tag as [Boolean], or `null` if this tag is not an [NbtByte]
 */
public val NbtTag.booleanOrNull: Boolean? get() = (this as? NbtByte)?.toBoolean()

/**
 * Returns the value of this tag as [Byte], or `null` if this tag is not an [NbtByte]
 */
public val NbtTag.byteOrNull: Byte? get() = (this as? NbtByte)?.value

/**
 * Returns the value of this tag as [Short], or `null` if this tag is not an [NbtShort]
 */
public val NbtTag.shortOrNull: Short? get() = (this as? NbtShort)?.value

/**
 * Returns the value of this tag as [Int], or `null` if this tag is not an [NbtInt]
 */
public val NbtTag.intOrNull: Int? get() = (this as? NbtInt)?.value

/**
 * Returns the value of this tag as [Long], or `null` if this tag is not an [NbtLong]
 */
public val NbtTag.longOrNull: Long? get() = (this as? NbtLong)?.value

/**
 * Returns the value of this tag as [Float], or `null` if this tag is not an [NbtFloat]
 */
public val NbtTag.floatOrNull: Float? get() = (this as? NbtFloat)?.value

/**
 * Returns the value of this tag as [Double], or `null` if this tag is not an [NbtDouble]
 */
public val NbtTag.doubleOrNull: Double? get() = (this as? NbtDouble)?.value

/**
 * Returns the value of this tag as [String], or `null` if this tag is not an [NbtString]
 */
public val NbtTag.stringOrNull: String? get() = (this as? NbtString)?.value
//endregion
