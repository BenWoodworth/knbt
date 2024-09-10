package net.benwoodworth.knbt

import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.*
import net.benwoodworth.knbt.internal.BedrockNbtReader
import net.benwoodworth.knbt.internal.NbtCapabilities
import net.benwoodworth.knbt.internal.NbtContext
import net.benwoodworth.knbt.internal.NbtReader
import net.benwoodworth.knbt.internal.NbtWriter
import okio.BufferedSink
import okio.BufferedSource
import okio.Sink
import okio.Source

private val bedrockNbtCapabilities = NbtCapabilities(
    namedRoot = true,
    definiteLengthEncoding = true,
)

public class BedrockNbt internal constructor(
    override val configuration: BedrockNbtConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryNbtFormat() {
    override val name: String get() = "Bedrock"
    override val capabilities: NbtCapabilities get() = bedrockNbtCapabilities

    override fun getNbtReader(context: NbtContext, source: BufferedSource): BinaryNbtReader =
        BedrockNbtReader(context, source)

    override fun getNbtWriter(context: NbtContext, sink: BufferedSink): BinaryNbtWriter =
        BedrockNbtWriter(context, sink)
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
