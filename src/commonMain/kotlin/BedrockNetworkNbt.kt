package net.benwoodworth.knbt

import kotlinx.serialization.modules.SerializersModule

public class BedrockNetworkNbt internal constructor(
    override val configuration: BedrockNetworkNbtConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryNbtFormat() {
    override val variant: NbtVariant get() = NbtVariant.BedrockNetwork
}

/**
 * Creates an instance of [BedrockNetworkNbt] configured from the optionally given [BedrockNetworkNbt instance][from]
 * and adjusted with [builderAction].
 *
 * [compression][BedrockNbtBuilder.compression] is required.
 */
public fun BedrockNetworkNbt(
    from: BedrockNetworkNbt? = null,
    builderAction: BedrockNetworkNbtBuilder.() -> Unit
): BedrockNetworkNbt {
    val builder = BedrockNetworkNbtBuilder(from)
    builder.builderAction()
    return builder.build()
}
