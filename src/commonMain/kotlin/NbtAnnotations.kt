package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlin.reflect.KClass


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

/**
 * Signal to the NBT encoder what type a value should be encoded as.
 * Valid for class properties, and within a serial descriptor's annotations.
 *
 * [SerialKind]s, and their recognized [NbtType]s:
 * - [StructureKind.LIST]: [NbtList], [NbtByteArray], [NbtIntArray], [NbtLongArray]
 *
 * Is [ExperimentalSerializationApi] since [SerialInfo] is experimental.
 */
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
@ExperimentalSerializationApi
public annotation class NbtType(val type: KClass<out NbtTag>)
