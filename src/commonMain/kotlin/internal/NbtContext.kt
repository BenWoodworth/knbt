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


    fun onBeginSerializableValue(descriptor: SerialDescriptor) {
        dynamicNameDelegationChecker.checkSerializerDelegation(descriptor)
    }

    fun onBeginValue() {
        dynamicNameDelegationChecker.finishCheckingDelegation()
    }

    @OptIn(ExperimentalSerializationApi::class)
    private class DynamicNameChecker(private val context: NbtContext) {
        private var currentSerializer: SerialDescriptor? = null
        private var currentSerializerNameType: NameType = Uncomputed

        private enum class NameType { Static, Dynamic, Uncomputed }

        /**
         * Checks that a statically named serializer isn't delegating to a dynamically named serializer, since
         * delegating to a dynamically named serializer is only allowed from another dynamically named serializer.
         */
        fun checkSerializerDelegation(newSerializer: SerialDescriptor) {
            val delegatingSerializer = currentSerializer
            val delegatingSerializerNameType = currentSerializerNameType

            currentSerializer = newSerializer
            currentSerializerNameType = Uncomputed // Don't compute until it's actually needed

            val delegatingFromStaticName = when {
                delegatingSerializer === null -> false
                delegatingSerializerNameType === Uncomputed -> !delegatingSerializer.nbtNameIsDynamic
                else -> delegatingSerializerNameType === Static
            }

            if (delegatingFromStaticName) {
                val delegatingToDynamicName = newSerializer.nbtNameIsDynamic
                    .also { isDynamic -> currentSerializerNameType = if (isDynamic) Dynamic else Static }

                if (delegatingToDynamicName) {
                    delegatingSerializer!! // Isn't null, since `delegatingFromStaticName` would've been false
                    val message = "@NbtName.Dynamic is required when delegating to a dynamically named serializer, " +
                            "but '${delegatingSerializer.serialName}' delegates to '${newSerializer.serialName}' " +
                            "without it."

                    throw NbtException(context, message)
                }
            }
        }

        fun finishCheckingDelegation() {
            currentSerializer = null
        }
    }
}
