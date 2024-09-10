package net.benwoodworth.knbt

import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.NbtCapabilities

private val nbtCapabilities = NbtCapabilities(
    namedRoot = true,
    definiteLengthEncoding = true,
)

public open class Nbt internal constructor(
    override val configuration: NbtConfiguration,
    override val serializersModule: SerializersModule,
) : NbtFormat() {
    override val name: String get() = "NbtTag"
    override val capabilities: NbtCapabilities get() = nbtCapabilities

    public companion object Default : Nbt(
        configuration = NbtConfiguration(
            encodeDefaults = NbtFormatDefaults.encodeDefaults,
            ignoreUnknownKeys = NbtFormatDefaults.ignoreUnknownKeys,
        ),
        serializersModule = NbtFormatDefaults.serializersModule,
    )
}

/**
 * Creates an instance of [Nbt] configured from the optionally given [Nbt instance][from]
 * and adjusted with [builderAction].
 */
public fun Nbt(
    from: Nbt? = null,
    builderAction: NbtBuilder.() -> Unit,
): Nbt {
    val builder = NbtBuilder(from)
    builder.builderAction()
    return builder.build()
}
