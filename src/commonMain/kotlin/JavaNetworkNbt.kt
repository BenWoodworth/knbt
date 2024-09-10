package net.benwoodworth.knbt

import kotlinx.serialization.modules.SerializersModule

public class JavaNetworkNbt internal constructor(
    override val configuration: JavaNetworkNbtConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryNbtFormat() {
    override val variant: NbtVariant = NbtVariant.JavaNetwork(configuration.protocolVersion)
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
