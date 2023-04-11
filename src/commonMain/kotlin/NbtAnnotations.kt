package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialInfo
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind


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
 * Instructs the NBT serializer to serialize a [StructureKind.LIST] as an array tag.
 * Can be used on properties, or in a [SerialDescriptor]'s
 * [annotations][SerialDescriptor.annotations]/[getElementAnnotations][SerialDescriptor.getElementAnnotations].
 *
 * NBT array tag type is determined by the first element in the serial descriptor.
 * Applicable to lists of [PrimitiveKind.BYTE], [INT][PrimitiveKind.INT], and [LONG][PrimitiveKind.LONG], for
 * `TAG_Byte_Array`, `TAG_Int_Array`, and `TAG_Long_Array`, respectively.
 */
@SerialInfo
@Target(AnnotationTarget.PROPERTY)
@OptIn(ExperimentalSerializationApi::class)
public annotation class NbtArray

/**
 * Instructs the NBT serializer to serialize a [NbtCompound] or [NbtList] as a named tag. (A tag nested in a
 * single-element compound)
 *
 * This is convenient for NBT binary/files, which require the root tag to be named.
 *
 * Example:
 * ```
 * @Serializable
 * @NbtNamed("name")
 * class MyClass(
 *     val entry1: String,
 *     entry2: Int
 * )
 * ```
 *
 * `MyClass("hello", 42)` serializes to:
 * ```nbt
 * {
 *   name: {
 *     entry1: "hello",
 *     entry2: 42
 *   }
 * }
 * ```
 */
@SerialInfo
@Target(AnnotationTarget.CLASS)
@OptIn(ExperimentalSerializationApi::class)
public annotation class NbtNamed(val name: String)
