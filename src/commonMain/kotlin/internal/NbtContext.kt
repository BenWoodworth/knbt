package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.NbtDecoder
import net.benwoodworth.knbt.NbtEncoder

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
    override fun getPath(): NbtPath? = null // TODO
}
