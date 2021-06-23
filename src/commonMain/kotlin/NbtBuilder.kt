package net.benwoodworth.knbt

import net.benwoodworth.knbt.tag.*

/**
 * Build an [NbtTag] suitable for being written to an NBT file.
 *
 * @return a [name]d [NbtCompound] built using the [builderAction].
 */
public inline fun buildNbt(
    name: String,
    builderAction: NbtCompoundBuilder.() -> Unit,
): NbtCompound =
    buildNbtCompound {
        putNbtCompound(name, builderAction)
    }
