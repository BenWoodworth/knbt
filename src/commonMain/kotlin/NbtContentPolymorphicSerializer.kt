package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.modules.*
import kotlin.reflect.*

// Derived from JsonContentPolymorphicSerializer in kotlinx-serialization v1.5.0

/**
 * Base class for custom serializers that allows selecting polymorphic serializer
 * without a dedicated class discriminator, on a content basis.
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
 *         "reason" in content.nbtCompound -> RefundedPayment.serializer()
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
public abstract class NbtContentPolymorphicSerializer<T : Any>(
    private val baseClass: KClass<T>
) : KSerializer<T> {
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

    final override fun serialize(encoder: Encoder, value: T) {
        @OptIn(InternalSerializationApi::class)
        val actualSerializer =
            encoder.serializersModule.getPolymorphic(baseClass, value)
                    ?: value::class.serializerOrNull()
                    ?: throwSubtypeNotRegistered(value::class, baseClass)

        // required until root logic is moved into beginStructure
        @Suppress("UNCHECKED_CAST")
        encoder.encodeSerializableValue(actualSerializer as KSerializer<T>, value)

//        @Suppress("UNCHECKED_CAST")
//        (actualSerializer as KSerializer<T>).serialize(encoder, value)
    }

    final override fun deserialize(decoder: Decoder): T {
        val input = decoder.asNbtDecoder()
        val tag = input.decodeNbtTag()

        @Suppress("UNCHECKED_CAST")
        val actualSerializer = selectDeserializer(tag) as KSerializer<T>
        return input.nbt.decodeFromNbtTag(actualSerializer, tag)
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
