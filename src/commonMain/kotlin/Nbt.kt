package net.benwoodworth.knbt

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.Buffer

public open class Nbt internal constructor(
    override val configuration: NbtConfiguration,
    serializersModule: SerializersModule,
) : NbtFormat(
    configuration.variant.toString(),
    configuration,
    serializersModule,
    configuration.variant.capabilities
), BinaryFormat, @Suppress("DEPRECATION") NbtDeprecations {
    @OptIn(OkioApi::class)
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        Buffer().apply { encodeToBufferedSink(serializer, value, this) }.readByteArray()

    @OptIn(OkioApi::class)
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T =
        decodeFromBufferedSource(deserializer, Buffer().apply { write(bytes) })
}

private object DefaultNbt : Nbt(
    configuration = NbtConfiguration(
        variant = NbtVariant.Java, // Will be ignored by NbtBuilder
        compression = NbtCompression.None, // Will be ignored by NbtBuilder
        compressionLevel = null,
        encodeDefaults = NbtFormat.configuration.encodeDefaults,
        ignoreUnknownKeys = NbtFormat.configuration.ignoreUnknownKeys,
    ),
    serializersModule = EmptySerializersModule(),
)

/**
 * Creates an instance of [Nbt] configured from the optionally given [Nbt instance][from]
 * and adjusted with [builderAction].
 *
 * [variant][NbtBuilder.variant] and [compression][NbtBuilder.compression] are required.
 */
public fun Nbt(from: Nbt = DefaultNbt, builderAction: NbtBuilder.() -> Unit): Nbt {
    val builder = NbtBuilder(from)
    builder.builderAction()
    return builder.build()
}

/**
 * Builder of the [Nbt] instance provided by `Nbt { ... }` factory function.
 */
@NbtDslMarker
public class NbtBuilder internal constructor(nbt: Nbt) : NbtFormatBuilder(nbt) {
    /**
     * The variant of NBT binary format to use. Required.
     */
    public var variant: NbtVariant? =
        if (nbt === DefaultNbt) null else nbt.configuration.variant

    /**
     * The compression method to use when writing NBT binary. Required.
     */
    public var compression: NbtCompression? =
        if (nbt === DefaultNbt) null else nbt.configuration.compression

    /**
     * The compression level, in `0..9` or `null`.
     * `null` by default.
     *
     * - `0` gives no compression at all
     * - `1` gives the best speed
     * - `9` gives the best compression.
     * - `null` requests a compromise between speed and compression.
     */
    public var compressionLevel: Int? = nbt.configuration.compressionLevel
        set(value) {
            require(value == null || value in 0..9) { "Compression level must be in 0..9 or null." }
            field = value
        }

    override fun build(): Nbt {
        val variant = variant
        val compression = compression

        require(variant != null && compression != null) {
            when {
                variant == null && compression == null -> "Variant and compression are required but are null"
                variant == null -> "Variant is required but is null"
                else -> "Compression is required but is null"
            }
        }

        return Nbt(
            configuration = NbtConfiguration(
                variant = variant,
                compression = compression,
                compressionLevel = compressionLevel,
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
            ),
            serializersModule = serializersModule,
        )
    }
}
