/*
 * NOTICE: Modified from `JsonContentPolymorphicSerializer` in kotlinx.serialization v1.5.0.
 *
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.*
import kotlin.reflect.*

/**
 * Base class for custom serializers that allows selecting polymorphic serializer
 * without a dedicated class discriminator, on a content basis.
 *
 * Usually, polymorphic serialization (represented by [PolymorphicSerializer] and [SealedClassSerializer])
 * requires a dedicated `"type"` property in the NBT to
 * determine actual serializer that is used to deserialize Kotlin class.
 *
 * However, sometimes (e.g. when reading NBT data from Minecraft) type property is not present in the input
 * and it is expected to guess the actual type by the shape of NBT, for example by the presence of specific key.
 * [NbtContentPolymorphicSerializer] provides a skeleton implementation for such strategy. Please note that
 * since NBT content is represented by [NbtTag] class and could be read only with [NbtDecoder] decoder,
 * this class works only with [NBT format][NbtFormat].
 *
 * Deserialization happens in two stages: first, a value from the input NBT is read
 * to as a [NbtTag]. Second, [selectDeserializer] function is called to determine which serializer should be used.
 * The returned serializer is used to deserialize [NbtTag] back to Kotlin object.
 *
 * It is possible to serialize values this serializer. In that case, class discriminator property won't
 * be added to NBT stream, i.e., deserializing a class from the string and serializing it back yields the original string.
 * However, to determine a serializer, a standard polymorphic mechanism represented by [SerializersModule] is used.
 * For convenience, [serialize] method can lookup default serializer, but it is recommended to follow
 * standard procedure with [registering][SerializersModuleBuilder.polymorphic].
 *
 * Usage example:
 * ```
 * interface Payment {
 *     val amount: String
 * }
 *
 * @Serializable
 * data class SuccessfulPayment(override val amount: String, val date: String) : Payment
 *
 * @Serializable
 * data class RefundedPayment(override val amount: String, val date: String, val reason: String) : Payment
 *
 * object PaymentSerializer : NbtContentPolymorphicSerializer<Payment>(Payment::class) {
 *     override fun selectDeserializer(tag: NbtTag) = when {
 *         "reason" in tag.nbtCompound -> RefundedPayment.serializer()
 *         else -> SuccessfulPayment.serializer()
 *     }
 * }
 *
 * // Now both statements will yield different subclasses of Payment:
 *
 * StringifiedNbt.decodeFromString(PaymentSerializer, """{"amount":"1.0","date":"03.02.2020"}""")
 * StringifiedNbt.decodeFromString(PaymentSerializer, """{"amount":"2.0","date":"03.02.2020","reason":"complaint"}""")
 * ```
 *
 * @param T A root class for all classes that could be possibly encountered during serialization and deserialization.
 * @param baseClass A class token for [T].
 */
@OptIn(ExperimentalSerializationApi::class)
public abstract class NbtContentPolymorphicSerializer<T : Any>(private val baseClass: KClass<T>) : KSerializer<T> {
    /**
     * A descriptor for this set of content-based serializers.
     * By default, it uses the name composed of [baseClass] simple name,
     * kind is set to [PolymorphicKind.SEALED] and contains 0 elements.
     *
     * However, this descriptor can be overridden to achieve better representation of custom transformed NBT shape
     * for schema generating/introspection purposes.
     */
    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("NbtContentPolymorphicSerializer<${baseClass.simpleName}>", PolymorphicKind.SEALED)

    @OptIn(InternalSerializationApi::class)
    final override fun serialize(encoder: Encoder, value: T) {
        val actualSerializer =
            encoder.serializersModule.getPolymorphic(baseClass, value)
                ?: value::class.serializerOrNull()
                ?: throwSubtypeNotRegistered(value::class, baseClass)
        @Suppress("UNCHECKED_CAST")
        (actualSerializer as KSerializer<T>).serialize(encoder, value)
    }

    final override fun deserialize(decoder: Decoder): T {
        val input = decoder.asNbtDecoder()
        val tree = input.decodeNbtTag()

        val actualSerializer = selectDeserializer(tree) as KSerializer<T>
        return input.nbt.decodeFromNbtTag(actualSerializer, tree)
    }

    /**
     * Determines a particular strategy for deserialization by looking on a parsed NBT [tag].
     */
    protected abstract fun selectDeserializer(tag: NbtTag): DeserializationStrategy<T>

    private fun throwSubtypeNotRegistered(subClass: KClass<*>, baseClass: KClass<*>): Nothing {
        val subClassName = subClass.simpleName ?: "$subClass"
        val scope = "in the scope of '${baseClass.simpleName}'"
        throw SerializationException(
            "Class '${subClassName}' is not registered for polymorphic serialization $scope.\n" +
                    "Mark the base class as 'sealed' or register the serializer explicitly.")
    }

}
