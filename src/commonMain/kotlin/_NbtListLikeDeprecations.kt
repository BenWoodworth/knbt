@file:Suppress(
    "UselessCallOnNotNull",
    "ConvertArgumentToSet",
    "UNUSED_PARAMETER"
)

package net.benwoodworth.knbt

import kotlin.jvm.JvmName
import kotlin.random.Random

@Deprecated("For organizing deprecations")
public sealed class NbtListLikeDeprecations<out T> {
    private val content: List<T>
        get() = throw UnsupportedOperationException("List API moved to `content`")

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith(
            "this.getOrNull(index)",
            "net.benwoodworth.knbt.getOrNull"
        ),
        DeprecationLevel.ERROR
    )
    public fun elementAtOrNull(index: Int): T? =
        content.elementAtOrNull(index)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.subList(fromIndex, toIndex)"),
        DeprecationLevel.ERROR
    )
    public fun subList(fromIndex: Int, toIndex: Int): List<T> =
        content.subList(fromIndex, toIndex)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.listIterator()"),
        DeprecationLevel.ERROR
    )
    public fun listIterator(): ListIterator<T> =
        content.listIterator()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.listIterator(index)"),
        DeprecationLevel.ERROR
    )
    public fun listIterator(index: Int): ListIterator<T> =
        content.listIterator(index)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.isEmpty()"),
        DeprecationLevel.ERROR
    )
    public fun isEmpty(): Boolean =
        content.isEmpty()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.iterator()"),
        DeprecationLevel.ERROR
    )
    public operator fun iterator(): Iterator<T> =
        content.iterator()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.lastIndex"),
        DeprecationLevel.ERROR
    )
    public val lastIndex: Int
        get() = content.lastIndex

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.indices"),
        DeprecationLevel.ERROR
    )
    public val indices: IntRange
        get() = content.indices

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toList()"),
        DeprecationLevel.ERROR
    )
    public fun toList(): List<T> =
        content.toList()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.last()"),
        DeprecationLevel.ERROR
    )
    public fun last(): T =
        content.last()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.last(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun last(predicate: (T) -> Boolean): T =
        content.last(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.contains(element)"),
        DeprecationLevel.ERROR
    )
    public operator fun contains(element: @UnsafeVariance T): Boolean =
        content.contains(element)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.asReversed()"),
        DeprecationLevel.ERROR
    )
    public fun asReversed(): List<T> =
        content.asReversed()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.binarySearch(fromIndex, toIndex, comparison)"),
        DeprecationLevel.ERROR
    )
    public fun binarySearch(fromIndex: Int = 0, toIndex: Int = content.size, comparison: (T) -> Int): Int {
        return content.binarySearch(fromIndex, toIndex, comparison)
    }

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.binarySearch(element, comparator, fromIndex, toIndex)"),
        DeprecationLevel.ERROR
    )
    public fun binarySearch(
        element: @UnsafeVariance T,
        comparator: Comparator<in T>,
        fromIndex: Int = 0,
        toIndex: Int = content.size
    ): Int =
        content.binarySearch(element, comparator, fromIndex, toIndex)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.binarySearchBy(key, fromIndex, toIndex, selector)"),
        DeprecationLevel.ERROR
    )
    public fun <K : Comparable<K>> binarySearchBy(
        key: K?,
        fromIndex: Int = 0,
        toIndex: Int = content.size, /*crossinline*/
        selector: (T) -> K?
    ): Int =
        content.binarySearchBy(key, fromIndex, toIndex, selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.component1()"),
        DeprecationLevel.ERROR
    )
    public fun component1(): T =
        content.component1()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.component2()"),
        DeprecationLevel.ERROR
    )
    public fun component2(): T =
        content.component2()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.component3()"),
        DeprecationLevel.ERROR
    )
    public fun component3(): T =
        content.component3()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.component4()"),
        DeprecationLevel.ERROR
    )
    public fun component4(): T =
        content.component4()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.component5()"),
        DeprecationLevel.ERROR
    )
    public fun component5(): T =
        content.component5()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.dropLast(n)"),
        DeprecationLevel.ERROR
    )
    public fun dropLast(n: Int): List<T> =
        content.dropLast(n)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.drop(n)"),
        DeprecationLevel.ERROR
    )
    public fun drop(n: Int): List<T> =
        content.drop(n)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.dropLastWhile(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun dropLastWhile(predicate: (T) -> Boolean): List<T> =
        content.dropLastWhile(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.elementAt(index)"),
        DeprecationLevel.ERROR
    )
    public fun elementAt(index: Int): T =
        content.elementAt(index)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.elementAtOrElse(index, defaultValue)"),
        DeprecationLevel.ERROR
    )
    public fun elementAtOrElse(index: Int, defaultValue: (Int) -> @UnsafeVariance T): T =
        content.elementAtOrElse(index, defaultValue)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.findLast(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun findLast(predicate: (T) -> Boolean): T? =
        content.findLast(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.find(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun find(predicate: (T) -> Boolean): T? =
        content.find(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.first()"),
        DeprecationLevel.ERROR
    )
    public fun first(): T =
        content.first()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.firstOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun firstOrNull(): T? =
        content.firstOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.first(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun first(predicate: (T) -> Boolean): T =
        content.first(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.foldRight(initial, operation)"),
        DeprecationLevel.ERROR
    )
    public fun <R> foldRight(initial: R, operation: (T, R) -> R): R =
        content.foldRight(initial, operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.fold(initial, operation)"),
        DeprecationLevel.ERROR
    )
    public fun <R> fold(initial: R, operation: (R, T) -> R): R =
        content.fold(initial, operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.foldRightIndexed(initial, operation)"),
        DeprecationLevel.ERROR
    )
    public fun <R> foldRightIndexed(initial: R, operation: (Int, T, R) -> R): R =
        content.foldRightIndexed(initial, operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.getOrElse(index, defaultValue)"),
        DeprecationLevel.ERROR
    )
    public fun getOrElse(index: Int, defaultValue: (Int) -> @UnsafeVariance T): T =
        content.getOrElse(index, defaultValue)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.indexOf(element)"),
        DeprecationLevel.ERROR
    )
    public fun indexOf(element: @UnsafeVariance T): Int =
        content.indexOf(element)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.indexOfFirst(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun indexOfFirst(predicate: (T) -> Boolean): Int =
        content.indexOfFirst(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.indexOfLast(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun indexOfLast(predicate: (T) -> Boolean): Int =
        content.indexOfLast(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.lastIndexOf(element)"),
        DeprecationLevel.ERROR
    )
    public fun lastIndexOf(element: @UnsafeVariance T): Int =
        content.lastIndexOf(element)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.lastOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun lastOrNull(): T? =
        content.lastOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.lastOrNull(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun lastOrNull(predicate: (T) -> Boolean): T? =
        content.lastOrNull(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.reduceRight(operation)"),
        DeprecationLevel.ERROR
    )
    public fun reduceRight(operation: (T, T) -> @UnsafeVariance T): T =
        content.reduceRight(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.reduce(operation)"),
        DeprecationLevel.ERROR
    )
    public fun reduce(operation: (T, T) -> @UnsafeVariance T): T =
        content.reduce(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.reduceRightIndexed(operation)"),
        DeprecationLevel.ERROR
    )
    public fun reduceRightIndexed(operation: (Int, T, T) -> @UnsafeVariance T): T =
        content.reduceRightIndexed(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.reduceRightIndexedOrNull(operation)"),
        DeprecationLevel.ERROR
    )
    public fun reduceRightIndexedOrNull(operation: (Int, T, T) -> @UnsafeVariance T): T? =
        content.reduceRightIndexedOrNull(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.reduceRightOrNull(operation)"),
        DeprecationLevel.ERROR
    )
    public fun reduceRightOrNull(operation: (T, T) -> @UnsafeVariance T): T? =
        content.reduceRightOrNull(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.single()"),
        DeprecationLevel.ERROR
    )
    public fun single(): T =
        content.single()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.singleOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun singleOrNull(): T? =
        content.singleOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.single(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun single(predicate: (T) -> Boolean): T =
        content.single(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.slice(indices)"),
        DeprecationLevel.ERROR
    )
    public fun slice(indices: IntRange): List<T> =
        content.slice(indices)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.slice(indices)"),
        DeprecationLevel.ERROR
    )
    public fun slice(indices: Iterable<Int>): List<T> =
        content.slice(indices)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.takeLast(n)"),
        DeprecationLevel.ERROR
    )
    public fun takeLast(n: Int): List<T> =
        content.takeLast(n)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.take(n)"),
        DeprecationLevel.ERROR
    )
    public fun take(n: Int): List<T> =
        content.take(n)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.takeLastWhile(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun takeLastWhile(predicate: (T) -> Boolean): List<T> =
        content.takeLastWhile(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.all(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun all(predicate: (T) -> Boolean): Boolean =
        content.all(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.any()"),
        DeprecationLevel.ERROR
    )
    public fun any(): Boolean =
        content.any()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.any(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun any(predicate: (T) -> Boolean): Boolean =
        content.any(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.asIterable()"),
        DeprecationLevel.ERROR
    )
    public fun asIterable(): Iterable<T> =
        content.asIterable()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.asSequence()"),
        DeprecationLevel.ERROR
    )
    public fun asSequence(): Sequence<T> =
        content.asSequence()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.associate(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <K, V> associate(transform: (T) -> Pair<K, V>): Map<K, V> =
        content.associate(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.associateBy(keySelector)"),
        DeprecationLevel.ERROR
    )
    public fun <K> associateBy(keySelector: (T) -> K): Map<K, T> =
        content.associateBy(keySelector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.associateBy(keySelector, valueTransform)"),
        DeprecationLevel.ERROR
    )
    public fun <K, V> associateBy(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, V> =
        content.associateBy(keySelector, valueTransform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.associateByTo(destination, keySelector)"),
        DeprecationLevel.ERROR
    )
    public fun <K, M : MutableMap<in K, in T>> associateByTo(destination: M, keySelector: (T) -> K): M =
        content.associateByTo(destination, keySelector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.associateByTo(destination, keySelector, valueTransform)"),
        DeprecationLevel.ERROR
    )
    public fun <K, V, M : MutableMap<in K, in V>> associateByTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M =
        content.associateByTo(destination, keySelector, valueTransform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.associateTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <K, V, M : MutableMap<in K, in V>> associateTo(destination: M, transform: (T) -> Pair<K, V>): M =
        content.associateTo(destination, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.associateWith(valueSelector)"),
        DeprecationLevel.ERROR
    )
    // Unsafe variance is okay here since pre-deprecation usages of this function must have already been safe.
    public fun <V> associateWith(valueSelector: (T) -> V): Map<@UnsafeVariance T, V> =
        content.associateWith(valueSelector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.associateWithTo(destination, valueSelector)"),
        DeprecationLevel.ERROR
    )
    public fun <V, M : MutableMap<in T, in V>> associateWithTo(destination: M, valueSelector: (T) -> V): M =
        content.associateWithTo(destination, valueSelector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.chunked(size)"),
        DeprecationLevel.ERROR
    )
    public fun chunked(size: Int): List<List<T>> =
        content.chunked(size)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.chunked(size, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> chunked(size: Int, transform: (List<T>) -> R): List<R> =
        content.chunked(size, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.containsAll(elements)"),
        DeprecationLevel.ERROR
    )
    public fun containsAll(elements: Collection<@UnsafeVariance T>): Boolean =
        content.containsAll(elements)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.count()"),
        DeprecationLevel.ERROR
    )
    public fun count(): Int =
        content.count()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.count(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun count(predicate: (T) -> Boolean): Int =
        content.count(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.distinct()"),
        DeprecationLevel.ERROR
    )
    public fun distinct(): List<T> =
        content.distinct()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.distinctBy(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <K> distinctBy(selector: (T) -> K): List<T> =
        content.distinctBy(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.dropWhile(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun dropWhile(predicate: (T) -> Boolean): List<T> =
        content.dropWhile(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.filter(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun filter(predicate: (T) -> Boolean): List<T> =
        content.filter(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.filterIndexed(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun filterIndexed(predicate: (Int, T) -> Boolean): List<T> =
        content.filterIndexed(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.filterIndexedTo(destination, predicate)"),
        DeprecationLevel.ERROR
    )
    public fun <C : MutableCollection<in T>> filterIndexedTo(destination: C, predicate: (Int, T) -> Boolean): C =
        content.filterIndexedTo(destination, predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.filterIsInstance<R>()"),
        DeprecationLevel.ERROR
    )
    public fun <R> filterIsInstance(): List<R> {
        error("List API moved to `content`") //content.filterIsInstance<R>()
    }

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.filterIsInstanceTo(destination)"),
        DeprecationLevel.ERROR
    )
    public fun <R, C : MutableCollection<in R>> filterIsInstanceTo(destination: C): C =
        error("List API moved to `content`") //content.filterIsInstanceTo(destination)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.filterNot(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun filterNot(predicate: (T) -> Boolean): List<T> =
        content.filterNot(predicate)

//    @Deprecated(
//        "List API moved to `content`",
//        ReplaceWith("this.content.filterNotNull()"),
//        DeprecationLevel.ERROR
//    )
//    public fun filterNotNull() =
//        content.filterNotNull()
//
//    @Deprecated(
//        "List API moved to `content`",
//        ReplaceWith("this.content.filterNotNullTo(destination)"),
//        DeprecationLevel.ERROR
//    )
//    public fun <C : MutableCollection<in T>> filterNotNullTo(destination: C) =
//        content.filterNotNullTo(destination)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.filterNotTo(destination, predicate)"),
        DeprecationLevel.ERROR
    )
    public fun <C : MutableCollection<in T>> filterNotTo(destination: C, predicate: (T) -> Boolean): C =
        content.filterNotTo(destination, predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.filterTo(destination, predicate)"),
        DeprecationLevel.ERROR
    )
    public fun <C : MutableCollection<in T>> filterTo(destination: C, predicate: (T) -> Boolean): C =
        content.filterTo(destination, predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.firstNotNullOf(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> firstNotNullOf(transform: (T) -> R?): R & Any =
        content.firstNotNullOf(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.firstNotNullOfOrNull(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> firstNotNullOfOrNull(transform: (T) -> R?): R? =
        content.firstNotNullOfOrNull(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.firstOrNull(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun firstOrNull(predicate: (T) -> Boolean): T? =
        content.firstOrNull(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.flatMap(transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapIterable")
    public fun <R> flatMap(transform: (T) -> Iterable<R>): List<R> =
        content.flatMap(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.flatMap(transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapSequence")
    public fun <R> flatMap(transform: (T) -> Sequence<R>): List<R> =
        content.flatMap(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.flatMapIndexed(transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapIndexedIterable")
    public fun <R> flatMapIndexed(transform: (Int, T) -> Iterable<R>): List<R> =
        content.flatMapIndexed(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.flatMapIndexed(transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapIndexedSequence")
    public fun <R> flatMapIndexed(transform: (Int, T) -> Sequence<R>): List<R> =
        content.flatMapIndexed(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.flatMapIndexedTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapIndexedIterableTo")
    public fun <R, C : MutableCollection<in R>> flatMapIndexedTo(
        destination: C,
        transform: (Int, T) -> Iterable<R>
    ): C =
        content.flatMapIndexedTo(destination, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.flatMapIndexedTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapIndexedSequenceTo")
    public fun <R, C : MutableCollection<in R>> flatMapIndexedTo(
        destination: C,
        transform: (Int, T) -> Sequence<R>
    ): C =
        content.flatMapIndexedTo(destination, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.flatMapTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapIterableTo")
    public fun <R, C : MutableCollection<in R>> flatMapTo(destination: C, transform: (T) -> Iterable<R>): C =
        content.flatMapTo(destination, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.flatMapTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    @JvmName("flatMapSequenceTo")
    public fun <R, C : MutableCollection<in R>> flatMapTo(destination: C, transform: (T) -> Sequence<R>): C =
        content.flatMapTo(destination, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.foldIndexed(initial, operation)"),
        DeprecationLevel.ERROR
    )
    public fun <R> foldIndexed(initial: R, operation: (Int, R, T) -> R): R =
        content.foldIndexed(initial, operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.forEach(action)"),
        DeprecationLevel.ERROR
    )
    public fun forEach(action: (T) -> Unit): Unit =
        content.forEach(action)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.forEachIndexed(action)"),
        DeprecationLevel.ERROR
    )
    public fun forEachIndexed(action: (Int, T) -> Unit): Unit =
        content.forEachIndexed(action)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.groupBy(keySelector)"),
        DeprecationLevel.ERROR
    )
    public fun <K> groupBy(keySelector: (T) -> K): Map<K, List<T>> =
        content.groupBy(keySelector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.groupBy(keySelector, valueTransform)"),
        DeprecationLevel.ERROR
    )
    public fun <K, V> groupBy(keySelector: (T) -> K, valueTransform: (T) -> V): Map<K, List<V>> =
        content.groupBy(keySelector, valueTransform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.groupByTo(destination, keySelector)"),
        DeprecationLevel.ERROR
    )
    public fun <K, M : MutableMap<in K, MutableList<@UnsafeVariance T>>> groupByTo(
        destination: M,
        keySelector: (T) -> K
    ): M =
        content.groupByTo(destination, keySelector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.groupByTo(destination, keySelector, valueTransform)"),
        DeprecationLevel.ERROR
    )
    public fun <K, V, M : MutableMap<in K, MutableList<@UnsafeVariance V>>> groupByTo(
        destination: M,
        keySelector: (T) -> K,
        valueTransform: (T) -> V
    ): M =
        content.groupByTo(destination, keySelector, valueTransform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.groupingBy(keySelector)"),
        DeprecationLevel.ERROR
    )
    // Unsafe variance is okay here since pre-deprecation usages of this function must have already been safe.
    public fun <K> groupingBy(/*crossinline*/ keySelector: (T) -> K): Grouping<@UnsafeVariance T, K> {
        return content.groupingBy(keySelector)
    }

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.intersect(other)"),
        DeprecationLevel.ERROR
    )
    public fun intersect(other: Iterable<Int>): Set<Any?> =
        content.intersect(other)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.isNotEmpty()"),
        DeprecationLevel.ERROR
    )
    public fun isNotEmpty(): Boolean =
        content.isNotEmpty()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.isNullOrEmpty()"),
        DeprecationLevel.ERROR
    )
    public fun isNullOrEmpty(): Boolean =
        content.isNullOrEmpty()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.joinToString(separator, prefix, postfix, limit, truncated, transform)"),
        DeprecationLevel.ERROR
    )
    public fun joinToString(
        separator: CharSequence = ", ",
        prefix: CharSequence = "",
        postfix: CharSequence = "",
        limit: Int = -1,
        truncated: CharSequence = "...",
        transform: ((T) -> CharSequence)? = null
    ): String =
        content.joinToString(separator, prefix, postfix, limit, truncated, transform)

//    @Deprecated(
////        "List API moved to `content`",
////        ReplaceWith("this.//        content.lastIndexOf(element)"),
////        DeprecationLevel.ERROR
////    )
//    public fun lastIndexOf(element: T) =
//        content.lastIndexOf(element)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.map(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> map(transform: (T) -> R): List<R> =
        content.map(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.mapIndexed(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> mapIndexed(transform: (Int, T) -> R): List<R> =
        content.mapIndexed(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.mapIndexedNotNull(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> mapIndexedNotNull(transform: (Int, T) -> R?): List<R> =
        content.mapIndexedNotNull(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.mapIndexedNotNullTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>, C : MutableCollection<in R>> mapIndexedNotNullTo(
        destination: C,
        transform: (Int, T) -> R?
    ): C =
        content.mapIndexedNotNullTo(destination, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.mapIndexedTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>, C : MutableCollection<in R>> mapIndexedTo(
        destination: C,
        transform: (Int, T) -> R
    ): C =
        content.mapIndexedTo(destination, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.mapNotNull(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> mapNotNull(transform: (T) -> R?): List<R> =
        content.mapNotNull(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.mapNotNullTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>, C : MutableCollection<in R>> mapNotNullTo(destination: C, transform: (T) -> R?): C =
        content.mapNotNullTo(destination, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.mapTo(destination, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>, C : MutableCollection<in R>> mapTo(destination: C, transform: (T) -> R): C =
        content.mapTo(destination, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxBy(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> maxBy(selector: (T) -> R): T =
        content.maxBy(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxByOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> maxByOrNull(selector: (T) -> R): T? =
        content.maxByOrNull(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> maxOf(selector: (T) -> R): R =
        content.maxOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun maxOf(selector: (T) -> Float): Float =
        content.maxOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun maxOf(selector: (T) -> Double): Double =
        content.maxOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> maxOfOrNull(selector: (T) -> R): R? =
        content.maxOfOrNull(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun maxOfOrNull(selector: (T) -> Float): Float? =
        content.maxOfOrNull(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun maxOfOrNull(selector: (T) -> Double): Double? =
        content.maxOfOrNull(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOfWith(comparator, selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> maxOfWith(comparator: Comparator<in R>, selector: (T) -> R): R =
        content.maxOfWith(comparator, selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOfWithOrNull(comparator, selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R> maxOfWithOrNull(comparator: Comparator<in R>, selector: (T) -> R): R? =
        content.maxOfWithOrNull(comparator, selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxWith(comparator)"),
        DeprecationLevel.ERROR
    )
    public fun maxWith(comparator: Comparator<in T>): T =
        content.maxWith(comparator)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxWithOrNull(comparator)"),
        DeprecationLevel.ERROR
    )
    public fun maxWithOrNull(comparator: Comparator<in T>): T? =
        content.maxWithOrNull(comparator)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minBy(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> minBy(selector: (T) -> R): T =
        content.minBy(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minByOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> minByOrNull(selector: (T) -> R): T? =
        content.minByOrNull(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> minOf(selector: (T) -> R): R =
        content.minOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun minOf(selector: (T) -> Float): Float =
        content.minOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun minOf(selector: (T) -> Double): Double =
        content.minOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> minOfOrNull(selector: (T) -> R): R? =
        content.minOfOrNull(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun minOfOrNull(selector: (T) -> Float): Float? =
        content.minOfOrNull(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOfOrNull(selector)"),
        DeprecationLevel.ERROR
    )
    public fun minOfOrNull(selector: (T) -> Double): Double? =
        content.minOfOrNull(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOfWith(comparator, selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R> minOfWith(comparator: Comparator<in R>, selector: (T) -> R): R =
        content.minOfWith(comparator, selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOfWithOrNull(comparator, selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R> minOfWithOrNull(comparator: Comparator<in R>, selector: (T) -> R): R? =
        content.minOfWithOrNull(comparator, selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minWith(comparator)"),
        DeprecationLevel.ERROR
    )
    public fun minWith(comparator: Comparator<in T>): T =
        content.minWith(comparator)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minWithOrNull(comparator)"),
        DeprecationLevel.ERROR
    )
    public fun minWithOrNull(comparator: Comparator<in T>): T? =
        content.minWithOrNull(comparator)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minus(element)"),
        DeprecationLevel.ERROR
    )
    public operator fun minus(element: @UnsafeVariance T): List<T> =
        content.minus(element)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minus(elements)"),
        DeprecationLevel.ERROR
    )
    public operator fun minus(elements: Iterable<@UnsafeVariance T>): List<T> =
        content.minus(elements)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minus(elements)"),
        DeprecationLevel.ERROR
    )
    public operator fun minus(elements: Sequence<@UnsafeVariance T>): List<T> =
        content.minus(elements)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minus(elements)"),
        DeprecationLevel.ERROR
    )
    public operator fun minus(elements: Array<out @UnsafeVariance T>): List<T> =
        content.minus(elements)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minusElement(element)"),
        DeprecationLevel.ERROR
    )
    public fun minusElement(element: @UnsafeVariance T): List<T> =
        content.minusElement(element)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.none()"),
        DeprecationLevel.ERROR
    )
    public fun none(): Boolean =
        content.none()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.none(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun none(predicate: (T) -> Boolean): Boolean =
        content.none(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.orEmpty()"),
        DeprecationLevel.ERROR
    )
    public fun orEmpty(): List<T> =
        content.orEmpty()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.partition(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun partition(predicate: (T) -> Boolean): Pair<List<T>, List<T>> =
        content.partition(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.plus(element)"),
        DeprecationLevel.ERROR
    )
    public operator fun plus(element: @UnsafeVariance T): List<T> =
        content.plus(element)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.plus(elements)"),
        DeprecationLevel.ERROR
    )
    public operator fun plus(elements: Iterable<Int>): List<Any?> =
        content.plus(elements)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.plus(elements)"),
        DeprecationLevel.ERROR
    )
    public operator fun plus(elements: Sequence<Int>): List<Any?> =
        content.plus(elements)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.plus(elements)"),
        DeprecationLevel.ERROR
    )
    public operator fun plus(elements: Array<out @UnsafeVariance T>): List<T> =
        content.plus(elements)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.plusElement(element)"),
        DeprecationLevel.ERROR
    )
    public fun plusElement(element: @UnsafeVariance T): List<T> =
        content.plusElement(element)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.random()"),
        DeprecationLevel.ERROR
    )
    public fun random(): T =
        content.random()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.random(random)"),
        DeprecationLevel.ERROR
    )
    public fun random(random: Random): T =
        content.random(random)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.randomOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun randomOrNull(): T? =
        content.randomOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.randomOrNull(random)"),
        DeprecationLevel.ERROR
    )
    public fun randomOrNull(random: Random): T? =
        content.randomOrNull(random)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.reduceIndexed(operation)"),
        DeprecationLevel.ERROR
    )
    public fun reduceIndexed(operation: (Int, T, T) -> @UnsafeVariance T): T =
        content.reduceIndexed(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.reduceIndexedOrNull(operation)"),
        DeprecationLevel.ERROR
    )
    public fun reduceIndexedOrNull(operation: (Int, T, T) -> @UnsafeVariance T): T? =
        content.reduceIndexedOrNull(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.reduceOrNull(operation)"),
        DeprecationLevel.ERROR
    )
    public fun reduceOrNull(operation: (T, T) -> @UnsafeVariance T): T? =
        content.reduceOrNull(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.requireNoNulls()"),
        DeprecationLevel.ERROR
    )
    public fun requireNoNulls(): List<T & Any> =
        content.requireNoNulls()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.reversed()"),
        DeprecationLevel.ERROR
    )
    public fun reversed(): List<T> =
        content.reversed()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.runningFold(initial, operation)"),
        DeprecationLevel.ERROR
    )
    public fun <R> runningFold(initial: R, operation: (R, T) -> R): List<R> =
        content.runningFold(initial, operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.runningFoldIndexed(initial, operation)"),
        DeprecationLevel.ERROR
    )
    public fun <R> runningFoldIndexed(initial: R, operation: (Int, R, T) -> R): List<R> =
        content.runningFoldIndexed(initial, operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.runningReduce(operation)"),
        DeprecationLevel.ERROR
    )
    public fun <R> runningReduce(operation: (T, T) -> @UnsafeVariance T): List<T> =
        content.runningReduce(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.runningReduceIndexed(operation)"),
        DeprecationLevel.ERROR
    )
    public fun runningReduceIndexed(operation: (Int, T, T) -> @UnsafeVariance T): List<T> =
        content.runningReduceIndexed(operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.scan(initial, operation)"),
        DeprecationLevel.ERROR
    )
    public fun <R> scan(initial: R, operation: (R, T) -> R): List<R> =
        content.scan(initial, operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.scanIndexed(initial, operation)"),
        DeprecationLevel.ERROR
    )
    public fun <R> scanIndexed(initial: R, operation: (Int, R, T) -> R): List<R> =
        content.scanIndexed(initial, operation)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.shuffled()"),
        DeprecationLevel.ERROR
    )
    public fun shuffled(): List<T> =
        content.shuffled()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.shuffled(random)"),
        DeprecationLevel.ERROR
    )
    public fun shuffled(random: Random): List<T> =
        content.shuffled(random)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.singleOrNull(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun singleOrNull(predicate: (T) -> Boolean): T? =
        content.singleOrNull(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sortedBy(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> sortedBy(selector: (T) -> R?): List<T> =
        content.sortedBy(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sortedByDescending(selector)"),
        DeprecationLevel.ERROR
    )
    public fun <R : Comparable<R>> sortedByDescending(selector: (T) -> R?): List<T> =
        content.sortedByDescending(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sortedWith(comparator)"),
        DeprecationLevel.ERROR
    )
    public fun sortedWith(comparator: Comparator<in T>): List<T> =
        content.sortedWith(comparator)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.subtract(other)"),
        DeprecationLevel.ERROR
    )
    public fun subtract(other: Iterable<@UnsafeVariance T>): Set<T> =
        content.subtract(other)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sumBy(selector)"),
        DeprecationLevel.ERROR
    )
    @Suppress("DEPRECATION")
    public fun sumBy(selector: (T) -> Int): Int =
        content.sumBy(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sumByDouble(selector)"),
        DeprecationLevel.ERROR
    )
    @Suppress("DEPRECATION")
    public fun sumByDouble(selector: (T) -> Double): Double =
        content.sumByDouble(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sumOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun sumOf(selector: (T) -> Int): Int =
        content.sumOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sumOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun sumOf(selector: (T) -> Long): Long =
        content.sumOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sumOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun sumOf(selector: (T) -> UInt): UInt =
        content.sumOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sumOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun sumOf(selector: (T) -> ULong): ULong =
        content.sumOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sumOf(selector)"),
        DeprecationLevel.ERROR
    )
    public fun sumOf(selector: (T) -> Double): Double =
        content.sumOf(selector)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.takeWhile(predicate)"),
        DeprecationLevel.ERROR
    )
    public fun takeWhile(predicate: (T) -> Boolean): List<T> =
        content.takeWhile(predicate)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toCollection(destination)"),
        DeprecationLevel.ERROR
    )
    public fun <C : MutableCollection<in T>> toCollection(destination: C): C =
        content.toCollection(destination)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toHashSet()"),
        DeprecationLevel.ERROR
    )
    public fun toHashSet(): HashSet<@UnsafeVariance T> =
        content.toHashSet()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toMutableList()"),
        DeprecationLevel.ERROR
    )
    public fun toMutableList(): MutableList<@UnsafeVariance T> =
        content.toMutableList()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toMutableSet()"),
        DeprecationLevel.ERROR
    )
    public fun toMutableSet(): MutableSet<@UnsafeVariance T> =
        content.toMutableSet()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toSet()"),
        DeprecationLevel.ERROR
    )
    public fun toSet(): Set<T> =
        content.toSet()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toTypedArray()"),
        DeprecationLevel.ERROR
    )
    public fun toTypedArray(): Array<@UnsafeVariance T> =
        error("List API moved to `content`") //content.toTypedArray()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.union(other)"),
        DeprecationLevel.ERROR
    )
    public fun union(other: Iterable<@UnsafeVariance T>): Set<T> =
        content.union(other)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.windowed(size, step)"),
        DeprecationLevel.ERROR
    )
    public fun windowed(size: Int, step: Int = 1, partialWindows: Boolean = false): List<List<T>> =
        content.windowed(size, step)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.windowed(size, step, partialWindows, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> windowed(
        size: Int,
        step: Int = 1,
        partialWindows: Boolean = false,
        transform: (List<T>) -> R
    ): List<R> =
        content.windowed(size, step, partialWindows, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.withIndex()"),
        DeprecationLevel.ERROR
    )
    public fun withIndex(): Iterable<IndexedValue<T>> =
        content.withIndex()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.zip(other)"),
        DeprecationLevel.ERROR
    )
    public fun <R> zip(other: Iterable<R>): List<Pair<T, R>> =
        content.zip(other)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.zip(other)"),
        DeprecationLevel.ERROR
    )
    public fun <R> zip(other: Array<out R>): List<Pair<T, R>> =
        content.zip(other)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.zip(other, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R, V> zip(other: Iterable<R>, transform: (T, R) -> V): List<V> =
        content.zip(other, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.zip(other, transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R, V> zip(other: Array<out R>, transform: (T, R) -> V): List<V> =
        content.zip(other, transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.zipWithNext()"),
        DeprecationLevel.ERROR
    )
    public fun zipWithNext(): List<Pair<T, T>> =
        content.zipWithNext()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.zipWithNext(transform)"),
        DeprecationLevel.ERROR
    )
    public fun <R> zipWithNext(transform: (T, T) -> R): List<R> =
        content.zipWithNext(transform)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.ifEmpty<List<T>, List<T>>(defaultValue)"),
        DeprecationLevel.ERROR
    )
    public fun ifEmpty(defaultValue: () -> List<@UnsafeVariance T>): List<T> =
        content.ifEmpty(defaultValue)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.onEach<T, List<T>>(action)"),
        DeprecationLevel.ERROR
    )
    public fun onEach(action: (T) -> Unit): List<T> =
        content.onEach(action)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.onEachIndexed<T, List<T>>(action)"),
        DeprecationLevel.ERROR
    )
    public fun onEachIndexed(action: (Int, T) -> Unit): List<T> =
        content.onEachIndexed(action)
}

@Deprecated("For organizing deprecations")
public sealed class NbtByteArrayDeprecations : @Suppress("DEPRECATION") NbtListLikeDeprecations<Byte>() {
    private val content: List<Byte>
        get() = throw UnsupportedOperationException("List API moved to `content`")

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.average()"),
        DeprecationLevel.ERROR
    )
    public fun average(): Double =
        content.average()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.binarySearch(element, fromIndex, toIndex)"),
        DeprecationLevel.ERROR
    )
    public fun binarySearch(element: Byte?, fromIndex: Int = 0, toIndex: Int = content.size): Int =
        content.binarySearch(element, fromIndex, toIndex)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.max()"),
        DeprecationLevel.ERROR
    )
    public fun max(): Byte =
        content.max()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun maxOrNull(): Byte? =
        content.maxOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.min()"),
        DeprecationLevel.ERROR
    )
    public fun min(): Byte =
        content.min()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun minOrNull(): Byte? =
        content.minOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sorted()"),
        DeprecationLevel.ERROR
    )
    public fun sorted(): List<Byte> =
        content.sorted()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sortedDescending()"),
        DeprecationLevel.ERROR
    )
    public fun sortedDescending(): List<Byte> =
        content.sortedDescending()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sum()"),
        DeprecationLevel.ERROR
    )
    public fun sum(): Int =
        content.sum()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toByteArray()"),
        DeprecationLevel.ERROR
    )
    public fun toByteArray(): ByteArray =
        content.toByteArray()
}

@Deprecated("For organizing deprecations")
public sealed class NbtIntArrayDeprecations : @Suppress("DEPRECATION") NbtListLikeDeprecations<Int>() {
    private val content: List<Int>
        get() = throw UnsupportedOperationException("List API moved to `content`")

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.average()"),
        DeprecationLevel.ERROR
    )
    public fun average(): Double =
        content.average()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.binarySearch(element, fromIndex, toIndex)"),
        DeprecationLevel.ERROR
    )
    public fun binarySearch(element: Int?, fromIndex: Int = 0, toIndex: Int = content.size): Int =
        content.binarySearch(element, fromIndex, toIndex)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.max()"),
        DeprecationLevel.ERROR
    )
    public fun max(): Int =
        content.max()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun maxOrNull(): Int? =
        content.maxOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.min()"),
        DeprecationLevel.ERROR
    )
    public fun min(): Int =
        content.min()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun minOrNull(): Int? =
        content.minOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sorted()"),
        DeprecationLevel.ERROR
    )
    public fun sorted(): List<Int> =
        content.sorted()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sortedDescending()"),
        DeprecationLevel.ERROR
    )
    public fun sortedDescending(): List<Int> =
        content.sortedDescending()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sum()"),
        DeprecationLevel.ERROR
    )
    public fun sum(): Int =
        content.sum()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toIntArray()"),
        DeprecationLevel.ERROR
    )
    public fun toIntArray(): IntArray =
        content.toIntArray()
}

@Deprecated("For organizing deprecations")
public sealed class NbtLongArrayDeprecations : @Suppress("DEPRECATION") NbtListLikeDeprecations<Long>() {
    private val content: List<Long>
        get() = throw UnsupportedOperationException("List API moved to `content`")

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.average()"),
        DeprecationLevel.ERROR
    )
    public fun average(): Double =
        content.average()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.binarySearch(element, fromIndex, toIndex)"),
        DeprecationLevel.ERROR
    )
    public fun binarySearch(element: Long?, fromIndex: Int = 0, toIndex: Int = content.size): Int =
        content.binarySearch(element, fromIndex, toIndex)

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.max()"),
        DeprecationLevel.ERROR
    )
    public fun max(): Long =
        content.max()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.maxOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun maxOrNull(): Long? =
        content.maxOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.min()"),
        DeprecationLevel.ERROR
    )
    public fun min(): Long =
        content.min()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.minOrNull()"),
        DeprecationLevel.ERROR
    )
    public fun minOrNull(): Long? =
        content.minOrNull()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sorted()"),
        DeprecationLevel.ERROR
    )
    public fun sorted(): List<Long> =
        content.sorted()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sortedDescending()"),
        DeprecationLevel.ERROR
    )
    public fun sortedDescending(): List<Long> =
        content.sortedDescending()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.sum()"),
        DeprecationLevel.ERROR
    )
    public fun sum(): Long =
        content.sum()

    @Deprecated(
        "List API moved to `content`",
        ReplaceWith("this.content.toLongArray()"),
        DeprecationLevel.ERROR
    )
    public fun toLongArray(): LongArray =
        content.toLongArray()
}
