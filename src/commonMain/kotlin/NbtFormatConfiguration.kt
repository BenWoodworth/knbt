package net.benwoodworth.knbt

import kotlinx.serialization.SerializationException
import kotlinx.serialization.modules.SerializersModule

public open class NbtFormatConfiguration internal constructor(
    public val encodeDefaults: Boolean,
    public val ignoreUnknownKeys: Boolean
) {
    override fun toString(): String =
        "NbtFormatConfiguration(" +
                "encodeDefaults=$encodeDefaults" +
                ", ignoreUnknownKeys=$ignoreUnknownKeys" +
                ")"
}

public open class NbtFormatBuilder internal constructor(nbt: NbtFormat) {
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
     * Module with contextual and polymorphic serializers to be used in the resulting [NbtFormat] instance.
     */
    public var serializersModule: SerializersModule = nbt.serializersModule

    internal open fun build(): NbtFormat {
        return NbtFormat(
            NbtFormat.name,
            configuration = NbtFormatConfiguration(
                encodeDefaults = encodeDefaults,
                ignoreUnknownKeys = ignoreUnknownKeys,
            ),
            serializersModule = serializersModule,
            capabilities = NbtFormat.capabilities,
        )
    }
}
