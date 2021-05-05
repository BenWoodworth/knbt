package net.benwoodworth.knbt

@DslMarker
internal annotation class NbtDslMarker

@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION, AnnotationTarget.TYPEALIAS)
@RequiresOptIn(level = RequiresOptIn.Level.ERROR)
internal annotation class InternalNbtApi
