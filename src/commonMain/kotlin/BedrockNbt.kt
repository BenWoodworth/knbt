package net.benwoodworth.knbt

import kotlinx.serialization.modules.SerializersModule

public class BedrockNbt internal constructor(
    override val configuration: BedrockNbtConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryNbtFormat() {
    override val variant: NbtVariant get() = NbtVariant.Bedrock
}

/**
 * Creates an instance of [BedrockNbt] configured from the optionally given [BedrockNbt instance][from]
 * and adjusted with [builderAction].
 *
 * [compression][BedrockNbtBuilder.compression] is required.
 */
public fun BedrockNbt(
    from: BedrockNbt? = null,
    builderAction: BedrockNbtBuilder.() -> Unit
): BedrockNbt {
    val builder = BedrockNbtBuilder(from)
    builder.builderAction()
    return builder.build()
}
