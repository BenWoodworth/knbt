package net.benwoodworth.knbt

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder

/**
 * Decoder used by [Nbt] during deserialization.
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
public sealed interface NbtDecoder : Decoder, CompositeDecoder, NbtDecoderDeprecations {
    /**
     * An instance of the current [NbtFormat].
     */
    public val nbt: NbtFormat

    /**
     * Decodes the next element in the current input as [NbtTag].
     * The type of the decoded element depends on the current state of the input and, when received
     * by [serializer][KSerializer] in its [KSerializer.serialize] method, the type of the token directly matches
     * the [kind][SerialDescriptor.kind].
     *
     * This method is allowed to invoke only as the part of the whole deserialization process of the class,
     * calling this method after invoking [beginStructure] or any `decode*` method will lead to unspecified behaviour.
     * For example:
     * ```
     * class Holder(val value: Int, val list: List<Int>())
     *
     * // Holder deserialize method
     * fun deserialize(decoder: Decoder): Holder {
     *     // Completely okay, the whole Holder object is read
     *     val nbtCompound = (decoder as NbtDecoder).decodeNbtTag()
     *     // ...
     * }
     *
     * // Incorrect Holder deserialize method
     * fun deserialize(decoder: Decoder): Holder {
     *     // decode "value" key unconditionally
     *     decoder.decodeElementIndex(descriptor)
     *     val value = decode.decodeInt()
     *     // Incorrect, decoder is already in an intermediate state after decodeInt
     *     val nbtTag = (decoder as NbtDecoder).decodeNbtTag()
     *     // ...
     * }
     * ```
     */
    public fun decodeNbtTag(): NbtTag

    /**
     * Returns the name of the current value, or null if it's unnamed (root of an unnamed [NbtFormat], entry in an NBT list/array).
     *
     * Must be called before decoding value
     *
     * requires [NbtName.Dynamic]
     */
    @ExperimentalNbtApi
    public fun decodeNbtName(): String?
}

@ExperimentalSerializationApi
internal abstract class AbstractNbtDecoder : AbstractDecoder(), NbtDecoder
