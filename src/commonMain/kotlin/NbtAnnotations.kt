package net.benwoodworth.knbt

@DslMarker
internal annotation class NbtDslMarker

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
internal annotation class InternalNbtApi

@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
internal annotation class UnsafeNbtApi

/**
 * @suppress
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
public annotation class ExperimentalNbtApi

/**
 * This library is only using Okio temporarily, and will eventually use kotlinx-io instead once it is stable enough.
 * Otherwise, the APIs that use Okio are stable and okay to use.
 * @suppress
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
public annotation class OkioApi
