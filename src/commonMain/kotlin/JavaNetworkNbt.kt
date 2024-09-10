package net.benwoodworth.knbt

import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

private val javaNetworkNbtCapabilities = NbtCapabilities(
    namedRoot = false,
    definiteLengthEncoding = true,
)

public class JavaNetworkNbt internal constructor(
    override val configuration: JavaNetworkNbtConfiguration,
    override val serializersModule: SerializersModule,
    private val protocolType: ProtocolType,
) : BinaryNbtFormat() {
    override val name: String get() = "JavaNetwork(v${configuration.protocolVersion})"
    override val capabilities: NbtCapabilities get() = javaNetworkNbtCapabilities

    internal enum class ProtocolType { EmptyNamedRoot, UnnamedRoot }

    override fun getNbtReader(context: NbtContext, source: BufferedSource): BinaryNbtReader =
        when (protocolType) {
            ProtocolType.EmptyNamedRoot -> JavaNetworkNbtReader.EmptyNamedRoot(context, source)
            ProtocolType.UnnamedRoot -> JavaNetworkNbtReader.UnnamedRoot(context, source)
        }

    override fun getNbtWriter(context: NbtContext, sink: BufferedSink): BinaryNbtWriter =
        when (protocolType) {
            ProtocolType.EmptyNamedRoot -> JavaNetworkNbtWriter.EmptyNamedRoot(context, sink)
            ProtocolType.UnnamedRoot -> JavaNetworkNbtWriter.UnnamedRoot(context, sink)
        }
}

/**
 * Creates an instance of [JavaNetworkNbt] configured from the optionally given [JavaNetworkNbt instance][from]
 * and adjusted with [builderAction].
 *
 * [protocolVersion][JavaNetworkNbtBuilder.protocolVersion] and [compression][JavaNetworkNbtBuilder.compression] are
 * required.
 */
public fun JavaNetworkNbt(
    from: JavaNetworkNbt? = null,
    builderAction: JavaNetworkNbtBuilder.() -> Unit
): JavaNetworkNbt {
    val builder = JavaNetworkNbtBuilder(from)
    builder.builderAction()
    return builder.build()
}
