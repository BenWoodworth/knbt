package net.benwoodworth.knbt

import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

private val bedrockNetworkNbtCapabilities = NbtCapabilities(
    namedRoot = false,
    definiteLengthEncoding = true,
)

public class BedrockNetworkNbt internal constructor(
    override val configuration: BedrockNetworkNbtConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryNbtFormat() {
    override val name: String get() = "BedrockNetwork"
    override val capabilities: NbtCapabilities get() = bedrockNetworkNbtCapabilities

    override fun getNbtReader(context: NbtContext, source: BufferedSource): BinaryNbtReader =
        BedrockNbtReader(context, source)

    override fun getNbtWriter(context: NbtContext, sink: BufferedSink): BinaryNbtWriter =
        BedrockNbtWriter(context, sink)
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
