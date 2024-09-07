/*
 * NOTICE: Modified from `JsonTransformingSerializer` in kotlinx.serialization v1.5.0.
 *
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

@file:Suppress("DeprecatedCallableAddReplaceWith")

package net.benwoodworth.knbt

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Base class for custom serializers that allows manipulating an abstract NBT
 * representation of the class before serialization or deserialization.
 *
 * [NbtTransformingSerializer] provides capabilities to manipulate [NbtTag] representation
 * directly instead of interacting with [Encoder] and [Decoder] in order to apply a custom
 * transformation to the NBT.
 * Please note that this class expects that [Encoder] and [Decoder] are implemented by [NbtDecoder] and [NbtEncoder],
 * i.e. serializers derived from this class work only with [NBT format][NbtFormat].
 *
 * There are two methods in which NBT transformation can be defined: [transformSerialize] and [transformDeserialize].
 * You can override one or both of them. Consult their documentation for details.
 *
 * Usage example:
 *
 * ```
 * @Serializable
 * data class Example(
 *     @Serializable(UnwrappingNbtListSerializer::class) val data: String
 * )
 * // Unwraps a list to a single object
 * object UnwrappingNbtListSerializer :
 *     NbtTransformingSerializer<String>(String.serializer()) {
 *     override fun transformDeserialize(tag: NbtTag): NbtTag {
 *         if (tag !is NbtList<*>) return tag
 *         require(tag.size == 1) { "List size must be equal to 1 to unwrap it" }
 *         return tag[0]
 *     }
 * }
 * // Now these functions both yield correct result:
 * StringifiedNbt.decodeFromString(Example.serializer(), """{"data":["str1"]}""")
 * StringifiedNbt.decodeFromString(Example.serializer(), """{"data":"str1"}""")
 * ```
 *
 * @param T A type for Kotlin property for which this serializer could be applied.
 *        **Not** the type that you may encounter in NBT. (e.g. if you unwrap a list
 *        to a single value `T`, use `T`, not `List<T>`)
 * @param tSerializer A serializer for type [T]. Determines [NbtTag] which is passed to [transformSerialize].
 *        Should be able to parse [NbtTag] from [transformDeserialize] function.
 *        Usually, default [serializer] is sufficient.
 */
public abstract class NbtTransformingSerializer<T : Any>(
    private val tSerializer: KSerializer<T>
) : KSerializer<T> {

    /**
     * A descriptor for this transformation.
     * By default, it delegates to [tSerializer]'s descriptor.
     *
     * However, this descriptor can be overridden to achieve better representation of the resulting NBT shape
     * for schema generating or introspection purposes.
     */
    override val descriptor: SerialDescriptor get() = tSerializer.descriptor

    final override fun serialize(encoder: Encoder, value: T) {
        val output = encoder.asNbtEncoder()
        var tag = output.nbt.encodeToNbtTag(tSerializer, value)
        tag = transformSerialize(tag)
        output.encodeNbtTag(tag)
    }

    final override fun deserialize(decoder: Decoder): T {
        val input = decoder.asNbtDecoder()
        val tag = input.decodeNbtTag()
        return input.nbt.decodeFromNbtTag(tSerializer, transformDeserialize(tag))
    }

    /**
     * Transformation that happens during [deserialize] call.
     * Does nothing by default.
     *
     * During deserialization, a value from NBT is firstly decoded to a [NbtTag],
     * user transformation in [transformDeserialize] is applied,
     * and then resulting [NbtTag] is deserialized to [T] with [tSerializer].
     */
    protected open fun transformDeserialize(tag: NbtTag): NbtTag = tag

    /**
     * Transformation that happens during [serialize] call.
     * Does nothing by default.
     *
     * During serialization, a value of type [T] is serialized with [tSerializer] to a [NbtTag],
     * user transformation in [transformSerialize] is applied, and then resulting [NbtTag] is encoded to NBT.
     */
    protected open fun transformSerialize(tag: NbtTag): NbtTag = tag
}
