package net.benwoodworth.knbt.internal

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.NbtEncoder
import net.benwoodworth.knbt.NbtFormat
import net.benwoodworth.knbt.NbtType

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
internal class SerializationNbtContext(
    private val nbt: NbtFormat
) : NbtContext {
    private var currentDescriptor: SerialDescriptor? = null
    private var structureNesting = 0
    private var isRootValue = true

    override fun getPath(): NbtPath? = null // TODO

    val isSerializingRootValue: Boolean
        get() = structureNesting == 0

    /**
     * Decorates calls to [SerializationStrategy.serialize] and [DeserializationStrategy.deserialize] so that a
     * serializer's [descriptor] can be tracked throughout all its calls during the serialization process.
     */
    inline fun <T> decorateValueSerialization(descriptor: SerialDescriptor, block: () -> T): T {
        val previousDescriptor = currentDescriptor
        currentDescriptor = descriptor

        try {
            return block()
        } finally {
            currentDescriptor = previousDescriptor
        }
    }

    fun onBeginStructure() {
        structureNesting++
    }

    fun onEndStructure() {
        structureNesting--
    }

    fun checkDynamicallySerializingNbtName() {
        if (currentDescriptor?.nbtNameIsDynamic != true) {
            @OptIn(ExperimentalSerializationApi::class)
            val quotedSerialName = currentDescriptor?.serialName
                ?.let { "'$it'" }
                ?: "the serialization process"

            val message = "@NbtName.Dynamic is required when dynamically serializing NBT names, " +
                    "but $quotedSerialName did so without it."

            throw NbtException(this, message)
        }
    }

    /**
     * Checks the [type] of a value as it's starting to be serialized.
     */
    fun beginSerializingValue(type: NbtType) {
        if (isRootValue) {
            if (type !in nbt.capabilities.rootTagTypes) {
                val message = "The ${nbt.name} format does not support root $type values. " +
                        "Supported types: ${nbt.capabilities.rootTagTypes}"

                throw NbtException(this, message)
            }

            isRootValue = false
        }
    }
}
