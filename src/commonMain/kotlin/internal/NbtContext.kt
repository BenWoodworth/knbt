package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.internal.SerializationNbtContext.DynamicNameChecker.NameType.*

/**
 * The context of the NBT serialization process, providing access to information about its current state.
 *
 * Notably it's used for [NbtException]s for reporting failure details, and for providing common NBT serialization
 * logic.
 */
internal interface NbtContext {
    fun getPath(): NbtPath?
}

/**
 * An [NbtContext] for use outside the main serialization process, such as during initialization when serialization has
 * not yet started.
 */
internal data object EmptyNbtContext : NbtContext {
    override fun getPath(): NbtPath? = null
}

/**
 * An [NbtContext] for use by [NbtEncoder] and [NbtDecoder] implementations, providing common NBT serialization logic
 * that is shared between them.
 *
 * Should only be used by [NbtEncoder] and [NbtDecoder].
 */
internal class SerializationNbtContext : NbtContext {
    private val dynamicNameDelegationChecker = DynamicNameChecker(this)

    override fun getPath(): NbtPath? = null // TODO

    fun checkDynamicallySerializingNbtName(): Unit =
        dynamicNameDelegationChecker.checkDynamicallySerializingNbtName()

    @OptIn(ExperimentalSerializationApi::class)
    private class DynamicNameChecker(private val context: NbtContext) {
        private var currentSerializer: SerialDescriptor? = null
        private var currentSerializerNameType: NameType = Uncomputed

        private enum class NameType { Static, Dynamic, Uncomputed }

        fun checkDynamicallySerializingNbtName() {
            TODO("Tracking needs to be re-implemented. Was implemented in 6020d53b, but removed in 8b137783")

            // TODO check. Can be reached when encoding primitives directly, after encoding value, potentially other ways?
            val currentSerializer = currentSerializer!!

            val isDynamic = when (currentSerializerNameType) {
                Static -> false
                Dynamic -> true
                Uncomputed -> currentSerializer.nbtNameIsDynamic
                    .also { isDynamic -> currentSerializerNameType = if (isDynamic) Dynamic else Static }
            }

            if (!isDynamic) {
                val message = "@NbtName.Dynamic is required when dynamically serializing NBT names, " +
                        "but '${currentSerializer.serialName}' does so without it."

                throw NbtException(context, message)
            }
        }
    }
}
