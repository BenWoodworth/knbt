package net.benwoodworth.knbt

import kotlinx.serialization.modules.SerializersModule

public class JavaNbt internal constructor(
    override val configuration: JavaNbtConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryNbtFormat() {
    override val variant: NbtVariant get() = NbtVariant.Java
}

/**
 * Creates an instance of [JavaNbt] configured from the optionally given [JavaNbt instance][from]
 * and adjusted with [builderAction].
 *
 * [compression][JavaNbtBuilder.compression] is required.
 */
public fun JavaNbt(
    from: JavaNbt? = null,
    builderAction: JavaNbtBuilder.() -> Unit
): JavaNbt {
    val builder = JavaNbtBuilder(from)
    builder.builderAction()
    return builder.build()
}
