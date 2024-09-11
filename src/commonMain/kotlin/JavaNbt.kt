package net.benwoodworth.knbt

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
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

    /**
     * Serializes the given [value] into an equivalent named [NbtTag] using the given [serializer].
     *
     * @throws [SerializationException] if the given value cannot be serialized to Java NBT.
     */
    public fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtNamed<NbtCompound> {
        val tag = encodeToNbtTagUnsafe(serializer, value)
        check(tag.value is NbtCompound) {
            "Expected value to encode into an NbtCompound, but was an ${tag.value::class.simpleName} tag"
        }

        @Suppress("UNCHECKED_CAST")
        return tag as NbtNamed<NbtCompound>
    }

    /**
     * Deserializes the given named [tag] into a value of type [T] using the given [deserializer].
     *
     * @throws [SerializationException] if the given NBT tag is not a valid Java NBT input for the type [T].
     * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
     */
    public fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtNamed<NbtCompound>): T =
        decodeFromNbtTagUnsafe(deserializer, tag)
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

/**
 * Serializes the given [value] into an equivalent named [NbtTag] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given value cannot be serialized to Java NBT.
 */
public inline fun <reified T> JavaNbt.encodeToNbtTag(value: T): NbtNamed<NbtCompound> =
    encodeToNbtTag(serializersModule.serializer(), value)

/**
 * Deserializes the given named [tag] into a value of type [T] using a serializer retrieved from the reified type
 * parameter.
 *
 * @throws [SerializationException] if the given NBT tag is not a valid Java NBT input for the type [T].
 * @throws [IllegalArgumentException] if the decoded input cannot be represented as a valid instance of type [T].
 */
public inline fun <reified T> JavaNbt.decodeFromNbtTag(tag: NbtNamed<NbtCompound>): T =
    decodeFromNbtTag(serializersModule.serializer(), tag)
