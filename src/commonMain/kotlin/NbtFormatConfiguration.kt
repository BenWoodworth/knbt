package net.benwoodworth.knbt

import kotlinx.serialization.SerializationException
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

public abstract class NbtFormatConfiguration internal constructor() {
    public abstract val encodeDefaults: Boolean
    public abstract val ignoreUnknownKeys: Boolean
    public abstract val lenientNbtNames: Boolean

    abstract override fun toString(): String
}

@Suppress("ConstPropertyName")
internal object NbtFormatDefaults {
    const val encodeDefaults: Boolean = false
    const val ignoreUnknownKeys: Boolean = false
    const val lenientNbtNames: Boolean = false
    val serializersModule: SerializersModule = EmptySerializersModule()
}

public abstract class NbtFormatBuilder internal constructor(nbt: NbtFormat?) {
    /**
     * Specifies whether default values of Kotlin properties should be encoded.
     * `false` by default.
     */
    public var encodeDefaults: Boolean =
        nbt?.configuration?.encodeDefaults ?: NbtFormatDefaults.encodeDefaults

    /**
     * Specifies whether encounters of unknown properties in the input NBT
     * should be ignored instead of throwing [SerializationException].
     * `false` by default.
     */
    public var ignoreUnknownKeys: Boolean =
        nbt?.configuration?.ignoreUnknownKeys ?: NbtFormatDefaults.ignoreUnknownKeys

    /**
     * Specifies whether a root [NbtName] mismatch should be ignored when deserializing named [NbtFormat]s
     * instead of throwing [SerializationException].
     * `false` by default.
     */
    public var lenientNbtNames: Boolean =
        nbt?.configuration?.lenientNbtNames ?: NbtFormatDefaults.lenientNbtNames

    /**
     * Module with contextual and polymorphic serializers to be used in the resulting [NbtFormat] instance.
     */
    public var serializersModule: SerializersModule =
        nbt?.serializersModule ?: NbtFormatDefaults.serializersModule

    internal abstract fun build(): NbtFormat
}
