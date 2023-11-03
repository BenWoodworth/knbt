package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.okio.encodeToBufferedSink
import okio.*

public sealed class Nbt(
    override val configuration: NbtConfiguration,
    override val serializersModule: SerializersModule,
) : NbtFormat, BinaryFormat, @Suppress("DEPRECATION") NbtDeprecations {
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
        encodeDefaults = false,
        ignoreUnknownKeys = false,
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
public class NbtBuilder internal constructor(nbt: Nbt) {
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

    @Deprecated("Replaced with NbtCompression.Gzip(level = ...) and .Zlib(level = ...)", level = DeprecationLevel.ERROR)
    public var compressionLevel: Int?
        get() = error("Deprecated. Use NbtCompression.Gzip(level = ...) or .Zlib(level = ...) instead.")
        set(_) = error("Deprecated. Use NbtCompression.Gzip(level = ...) or .Zlib(level = ...) instead.")

    /**
     * Specifies whether default values of Kotlin properties should be encoded.
     * `false` by default.
     */
    public var encodeDefaults: Boolean = nbt.configuration.encodeDefaults

    /**
     * Specifies whether encounters of unknown properties in the input NBT
     * should be ignored instead of throwing [SerializationException].
     * `false` by default.
     */
    public var ignoreUnknownKeys: Boolean = nbt.configuration.ignoreUnknownKeys

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [Nbt] instance.
     */
    public var serializersModule: SerializersModule = nbt.serializersModule

    internal fun build(): Nbt {
        val variant = variant
        val compression = compression

        require(variant != null && compression != null) {
            when {
                variant == null && compression == null -> "Variant and compression are required but are null"
                variant == null -> "Variant is required but is null"
                else -> "Compression is required but is null"
            }
        }

        return NbtImpl(
            configuration = NbtConfiguration(
                variant = variant,
                compression = compression,
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
            ),
            serializersModule = serializersModule,
        )
    }
}

private class NbtImpl(
    configuration: NbtConfiguration,
    serializersModule: SerializersModule,
) : Nbt(configuration, serializersModule)
