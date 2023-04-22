@file:Suppress(
    "NO_EXPLICIT_RETURN_TYPE_IN_API_MODE",
    "UselessCallOnNotNull",
    "ConvertArgumentToSet",
)

package net.benwoodworth.knbt

import kotlin.js.JsName
import kotlin.jvm.JvmName
import kotlin.reflect.KProperty

/**
 * ### knbt v0.12 migration note:
 * [NbtCompound.get] now throws [NoSuchElementException] instead of returning `null`.
 * Opting in will not be needed after the migration release.
 *
 * #### How to migrate:
 * ```
 * compound["name"]!!  // Change to: compound["name"]
 * compound["name"]    // Change to: compound.getOrNull("name")
 * ```
 */
@RequiresOptIn
@Suppress("ClassName")
@JsName("MIGRATION_Acknowledge_that_NbtCompound_now_has_a_stricter_get")
public annotation class `MIGRATION Acknowledge that NbtCompound now has a stricter get`

@Deprecated("For organizing deprecations")
public sealed class NbtCompoundDeprecations {
    private val content: Map<String, NbtTag>
        get() = throw UnsupportedOperationException("Map API moved to `content`")

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
    @`MIGRATION Acknowledge that NbtCompound now has a stricter get`
    public abstract operator fun get(name: String): NbtTag

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.keys"),
        DeprecationLevel.ERROR
    )
    public val keys: Set<String>
        get() = content.keys

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.entries"),
        DeprecationLevel.ERROR
    )
    public val entries: Set<Map.Entry<String, NbtTag>>
        get() = content.entries

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.values"),
        DeprecationLevel.ERROR
    )
    public val values: Collection<NbtTag>
        get() = content.values


    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.getValue(key)"),
        DeprecationLevel.ERROR
    )
    public fun getValue(key: String) =
        content.getValue(key)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.map(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> map(transform: (Map.Entry<String, NbtTag>) -> R) =
        content.map(transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.count(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun count(predicate: (Map.Entry<String, NbtTag>) -> Boolean) =
        content.count(predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.filter(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun filter(predicate: (Map.Entry<String, NbtTag>) -> Boolean) =
        content.filter(predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.filterKeys(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun filterKeys(predicate: (String) -> Boolean) =
        content.filterKeys(predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.filterValues(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun filterValues(predicate: (NbtTag) -> Boolean) =
        content.filterValues(predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.firstNotNullOfOrNull(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> firstNotNullOfOrNull(transform: (Map.Entry<String, NbtTag>) -> R?) =
        content.firstNotNullOfOrNull(transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.getValue(thisRef, property)"),
        DeprecationLevel.ERROR
    )
    public operator fun getValue(thisRef: Any?, property: KProperty<*>) =
        content.getValue(thisRef, property)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.all(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun all(predicate: (Map.Entry<String, NbtTag>) -> Boolean) =
        content.all(predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.any()"),
        DeprecationLevel.ERROR
    )
    public fun any() =
        content.any()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.any(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun any(predicate: (Map.Entry<String, NbtTag>) -> Boolean) =
        content.any(predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.asIterable()"),
        DeprecationLevel.ERROR
    )
    public fun asIterable() =
        content.asIterable()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.asSequence()"),
        DeprecationLevel.ERROR
    )
    public fun asSequence() =
        content.asSequence()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.containsKey(key)"),
        DeprecationLevel.ERROR
    )
    public fun containsKey(key: String) =
        content.containsKey(key)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.containsValue(value)"),
        DeprecationLevel.ERROR
    )
    public fun containsValue(value: NbtTag) =
        content.containsValue(value)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.count()"),
        DeprecationLevel.ERROR
    )
    public fun count() =
        content.count()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.filterNot(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun filterNot(predicate: (Map.Entry<String, NbtTag>) -> Boolean) =
        content.filterNot(predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.filterNotTo(destination, predicate)"),
        DeprecationLevel.ERROR
    )
    public fun filterNotTo(
        destination: MutableMap<in String, in NbtTag>,
        predicate: (Map.Entry<String, NbtTag>) -> Boolean
    ) =
        content.filterNotTo(destination, predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.filterTo(destination, predicate)"),
        DeprecationLevel.ERROR
    )
    public fun filterTo(
        destination: MutableMap<in String, in NbtTag>,
        predicate: (Map.Entry<String, NbtTag>) -> Boolean
    ) =
        content.filterTo(destination, predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.firstNotNullOf(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Any> firstNotNullOf(transform: (Map.Entry<String, NbtTag>) -> R?) =
        content.firstNotNullOf(transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.flatMap(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> flatMap(transform: (Map.Entry<String, NbtTag>) -> Iterable<R>) =
        content.flatMap(transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.flatMap(transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapSequence")
    public fun <R> flatMap(transform: (Map.Entry<String, NbtTag>) -> Sequence<R>) =
        content.flatMap(transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.flatMapTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R, C : MutableList<R>> flatMapTo(
        destination: C,
        transform: (Map.Entry<String, NbtTag>) -> Iterable<R>
    ) =
        content.flatMapTo(destination, transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.flatMapTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapSequenceTo")
    public fun <R, C : MutableList<R>> flatMapTo(
        destination: C,
        transform: (Map.Entry<String, NbtTag>) -> Sequence<R>
    ) =
        content.flatMapTo(destination, transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.forEach(action)"),
        DeprecationLevel.ERROR
    )
    public fun forEach(action: (Map.Entry<String, NbtTag>) -> Unit) =
        content.forEach(action)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.getOrElse(key, defaultValue)"),
        DeprecationLevel.ERROR
    )
    public fun getOrElse(key: String, defaultValue: () -> NbtTag) =
        content.getOrElse(key, defaultValue)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.isNotEmpty()"),
        DeprecationLevel.ERROR
    )
    public fun isNotEmpty() =
        content.isNotEmpty()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.isNullOrEmpty()"),
        DeprecationLevel.ERROR
    )
    public fun isNullOrEmpty() =
        content.isNullOrEmpty()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.iterator()"),
        DeprecationLevel.ERROR
    )
    public operator fun iterator() =
        content.iterator()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.mapKeys(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> mapKeys(transform: (Map.Entry<String, NbtTag>) -> R) =
        content.mapKeys(transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.mapKeysTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> mapKeysTo(
        destination: MutableMap<R, NbtTag>,
        transform: (Map.Entry<String, NbtTag>) -> R
    ) =
        content.mapKeysTo(destination, transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.mapNotNull(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Any> mapNotNull(transform: (Map.Entry<String, NbtTag>) -> R?) =
        content.mapNotNull(transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.mapNotNullTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Any, C : MutableCollection<in R>> mapNotNullTo(
        destination: C,
        transform: (Map.Entry<String, NbtTag>) -> R?
    ) =
        content.mapNotNullTo(destination, transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.mapTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R, C : MutableCollection<in R>> mapTo(destination: C, transform: (Map.Entry<String, NbtTag>) -> R) =
        content.mapTo(destination, transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.mapValues(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> mapValues(transform: (Map.Entry<String, NbtTag>) -> R) =
        content.mapValues(transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.mapValuesTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R, M : MutableMap<in String, in R>> mapValuesTo(
        destination: M,
        transform: (Map.Entry<String, NbtTag>) -> R
    ) =
        content.mapValuesTo(destination, transform)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxBy(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> maxBy(selector: (Map.Entry<String, NbtTag>) -> R) =
        content.maxBy(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxByOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> maxByOrNull(selector: (Map.Entry<String, NbtTag>) -> R) =
        content.maxByOrNull(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> maxOf(selector: (Map.Entry<String, NbtTag>) -> R) =
        content.maxOf(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun maxOf(selector: (Map.Entry<String, NbtTag>) -> Float) =
        content.maxOf(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun maxOf(selector: (Map.Entry<String, NbtTag>) -> Double) =
        content.maxOf(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> maxOfOrNull(selector: (Map.Entry<String, NbtTag>) -> R) =
        content.maxOfOrNull(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun maxOfOrNull(selector: (Map.Entry<String, NbtTag>) -> Float) =
        content.maxOfOrNull(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun maxOfOrNull(selector: (Map.Entry<String, NbtTag>) -> Double) =
        content.maxOfOrNull(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxOfWith(comparator, selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R> maxOfWith(comparator: Comparator<in R>, selector: (Map.Entry<String, NbtTag>) -> R) =
        content.maxOfWith(comparator, selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxOfWithOrNull(comparator, selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R> maxOfWithOrNull(comparator: Comparator<in R>, selector: (Map.Entry<String, NbtTag>) -> R) =
        content.maxOfWithOrNull(comparator, selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxWith(comparator)"),
        DeprecationLevel.ERROR
    )
    public fun maxWith(comparator: Comparator<in Map.Entry<String, NbtTag>>) =
        content.maxWith(comparator)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.maxWithOrNull(comparator)"),
        DeprecationLevel.ERROR
    )
    public fun maxWithOrNull(comparator: Comparator<in Map.Entry<String, NbtTag>>) =
        content.maxWithOrNull(comparator)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minBy(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> minBy(selector: (Map.Entry<String, NbtTag>) -> R) =
        content.minBy(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minByOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> minByOrNull(selector: (Map.Entry<String, NbtTag>) -> R) =
        content.minByOrNull(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> minOf(selector: (Map.Entry<String, NbtTag>) -> R) =
        content.minOf(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun minOf(selector: (Map.Entry<String, NbtTag>) -> Float) =
        content.minOf(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun minOf(selector: (Map.Entry<String, NbtTag>) -> Double) =
        content.minOf(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> minOfOrNull(selector: (Map.Entry<String, NbtTag>) -> R) =
        content.minOfOrNull(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun minOfOrNull(selector: (Map.Entry<String, NbtTag>) -> Float) =
        content.minOfOrNull(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun minOfOrNull(selector: (Map.Entry<String, NbtTag>) -> Double) =
        content.minOfOrNull(selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minOfWith(comparator, selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R> minOfWith(comparator: Comparator<in R>, selector: (Map.Entry<String, NbtTag>) -> R) =
        content.minOfWith(comparator, selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minOfWithOrNull(comparator, selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R> minOfWithOrNull(comparator: Comparator<in R>, selector: (Map.Entry<String, NbtTag>) -> R) =
        content.minOfWithOrNull(comparator, selector)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minWith(comparator, NbtTag>>)"),
        DeprecationLevel.ERROR
    )
    public fun minWith(comparator: Comparator<in Map.Entry<String, NbtTag>>) =
        content.minWith(comparator)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minWithOrNull(comparator, NbtTag>>)"),
        DeprecationLevel.ERROR
    )
    public fun minWithOrNull(comparator: Comparator<in Map.Entry<String, NbtTag>>) =
        content.minWithOrNull(comparator)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minus(key)"),
        DeprecationLevel.ERROR
    )
    public operator fun minus(key: String) =
        content.minus(key)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minus(keys)"),
        DeprecationLevel.ERROR
    )
    public operator fun minus(keys: Iterable<String>) =
        content.minus(keys)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minus(keys)"),
        DeprecationLevel.ERROR
    )
    public operator fun minus(keys: Sequence<String>) =
        content.minus(keys)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.minus(keys)"),
        DeprecationLevel.ERROR
    )
    public operator fun minus(keys: Array<out String>) =
        content.minus(keys)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.none()"),
        DeprecationLevel.ERROR
    )
    public fun none() =
        content.none()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.none(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun none(predicate: (Map.Entry<String, NbtTag>) -> Boolean) =
        content.none(predicate)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.orEmpty()"),
        DeprecationLevel.ERROR
    )
    public fun orEmpty() =
        content.orEmpty()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.plus(pair)"),
        DeprecationLevel.ERROR
    )
    public operator fun plus(pair: Pair<String, NbtTag>) =
        content.plus(pair)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.plus(map)"),
        DeprecationLevel.ERROR
    )
    public operator fun plus(map: Map<out String, NbtTag>) =
        content.plus(map)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.plus(pairs)"),
        DeprecationLevel.ERROR
    )
    public operator fun plus(pairs: Iterable<Pair<String, NbtTag>>) =
        content.plus(pairs)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.plus(pairs)"),
        DeprecationLevel.ERROR
    )
    public operator fun plus(pairs: Sequence<Pair<String, NbtTag>>) =
        content.plus(pairs)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.plus(pairs)"),
        DeprecationLevel.ERROR
    )
    public operator fun plus(pairs: Array<out Pair<String, NbtTag>>) =
        content.plus(pairs)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.toList()"),
        DeprecationLevel.ERROR
    )
    public fun toList() =
        content.toList()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.toMap()"),
        DeprecationLevel.ERROR
    )
    public fun toMap() =
        content.toMap()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.toMap(destination)"),
        DeprecationLevel.ERROR
    )
    public fun toMap(destination: MutableMap<in String, in NbtTag>) =
        content.toMap(destination)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.toMutableMap()"),
        DeprecationLevel.ERROR
    )
    public fun toMutableMap() =
        content.toMutableMap()

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.content.withDefault(defaultValue)"),
        DeprecationLevel.ERROR
    )
    public fun withDefault(defaultValue: (String) -> NbtTag) =
        content.withDefault(defaultValue)

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.takeUnless { it.content.isEmpty() } ?: defaultValue()"),
        DeprecationLevel.ERROR
    )
    public fun ifEmpty(defaultValue: () -> NbtCompound) =
        (takeIf { it.content.isNotEmpty() } ?: defaultValue()) as NbtCompound

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.also { it.content.onEach(action) }"),
        DeprecationLevel.ERROR
    )
    public fun onEach(action: (Map.Entry<String, NbtTag>) -> Unit) =
        (also { it.content.onEach(action) }) as NbtCompound

    @Deprecated(
        "Map API moved to `content`",
        ReplaceWith("this.also { it.content.onEachIndexed(action) }"),
        DeprecationLevel.ERROR
    )
    public fun onEachIndexed(action: (Int, Map.Entry<String, NbtTag>) -> Unit) =
        (also { it.content.onEachIndexed(action) }) as NbtCompound
}
