package net.benwoodworth.knbt

import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.internal.*
import net.benwoodworth.knbt.internal.BinaryNbtReader
import net.benwoodworth.knbt.internal.BinaryNbtWriter
import net.benwoodworth.knbt.internal.JavaNbtReader
import net.benwoodworth.knbt.internal.JavaNbtWriter
import net.benwoodworth.knbt.internal.NbtCapabilities
import net.benwoodworth.knbt.internal.NbtContext
import okio.BufferedSink
import okio.BufferedSource

private val javaNbtCapabilities = NbtCapabilities(
    namedRoot = true,
    definiteLengthEncoding = true,
)

public class JavaNbt internal constructor(
    override val configuration: JavaNbtConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryNbtFormat() {
    override val name: String get() = "Java"
    override val capabilities: NbtCapabilities get() = javaNbtCapabilities

    override fun getNbtReader(context: NbtContext, source: BufferedSource): BinaryNbtReader =
        JavaNbtReader(context, source)

    override fun getNbtWriter(context: NbtContext, sink: BufferedSink): BinaryNbtWriter =
        JavaNbtWriter(context, sink)
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
