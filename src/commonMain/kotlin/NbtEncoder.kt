package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder

/**
 * Encoder used by [Nbt] during serialization.
 * This interface can be used to inject desired behaviour into a serialization process of [Nbt].
 *
 * Typical example of the usage:
 * ```
 * // Class representing Either<Left|Right>
 * sealed class Either {
 *     data class Left(val errorMsg: String) : Either()
 *     data class Right(val data: Payload) : Either()
 * }
 *
 * // Serializer injects custom behaviour by inspecting object content and writing
 * object EitherSerializer : KSerializer<Either> {
 *     override val descriptor: SerialDescriptor = buildSerialDescriptor("package.Either", PolymorphicKind.SEALED) {
 *         // ..
 *     }
 *
 *     override fun deserialize(decoder: Decoder): Either {
 *         if (decoder !is NbtDecoder) throw SerializationException("This class can be decoded only by Nbt format")
 *         val nbtTag = decoder.decodeNbtTag() as? NbtCompound ?: throw SerializationException("Expected NbtCompound")
 *         return when {
 *             "error" in nbtTag -> Either.Left(nbtTag["error"]!!.string)
 *             else -> Either.Right(input.nbt.decodeFromNbtTag(Payload.serializer(), nbtTag))
 *         }
 *     }
 *
 *     override fun serialize(encoder: Encoder, value: Either) {
 *         val output = encoder as? NbtEncoder ?: throw SerializationException("This class can be encoded only by Nbt format")
 *         val nbtTag = when (value) {
 *           is Either.Left -> buildNbtCompound { put("error", value.errorMsg) }
 *           is Either.Right -> output.nbt.encodeToNbtTag(Payload.serializer(), value.data)
 *         }
 *         output.encodeNbtTag(nbtTag)
 *     }
 * }
 * ```
 */
@Suppress("DEPRECATION")
public sealed interface NbtEncoder : Encoder, CompositeEncoder, NbtEncoderDeprecations {
    /**
     * An instance of the current [Nbt].
     */
    public val nbt: NbtFormat

    /**
     * Appends the given NBT [tag] to the current output.
     * This method is allowed to invoke only as the part of the whole serialization process of the class,
     * calling this method after invoking [beginStructure] or any `encode*` method will lead to unspecified behaviour
     * and may produce an invalid NBT result.
     * For example:
     * ```
     * class Holder(val value: Int, val list: List<Int>())
     *
     * // Holder serialize method
     * fun serialize(encoder: Encoder, value: Holder) {
     *     // Completely okay, the whole Holder object is read
     *     val nbtCompound = NbtCompound(...) // build an NbtCompound from Holder
     *     (encoder as NbtEncoder).encodeNbtTag(nbtCompound) // Write it
     * }
     *
     * // Incorrect Holder serialize method
     * fun serialize(encoder: Encoder, value: Holder) {
     *     val composite = encoder.beginStructure(descriptor)
     *     composite.encodeSerializableElement(descriptor, 0, Int.serializer(), value.value)
     *     val array = NbtIntArray(value.list)
     *     // Incorrect, encoder is already in an intermediate state after encodeSerializableElement
     *     (composite as NbtEncoder).encodeNbtTag(array)
     *     composite.endStructure(descriptor)
     *     // ...
     * }
     * ```
     */
    public fun encodeNbtTag(tag: NbtTag)

    // TODO Description
    /**
     * Encodes the name of the
     *
     * must be called before value is encoded
     *
     * first call for the serializable value, so name before delegating is used
     * if not called then outermost [NbtName].
     *
     * requires [NbtName.Dynamic]
     *
     * takes priority over static [NbtName] annotation
     *
     * applies to root
     *
     * ignored for unnamed values (formats with unnamed root, list/array entries
     * ignored when name is set by parent (i.e. [SerialDescriptor.getElementName] takes precedence)
     *
     */
    @ExperimentalNbtApi
    public fun encodeNbtName(name: String)
}

@ExperimentalSerializationApi
internal abstract class AbstractNbtEncoder : AbstractEncoder(), NbtEncoder
