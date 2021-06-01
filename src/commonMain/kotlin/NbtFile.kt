package net.benwoodworth.knbt

/**
 * Override the [NbtConfiguration]'s [variant] and [compression], and
 * nest elements in a compound tag with the given [tagName].
 *
 * Example usage:
 * ```
 * @Serializable
 * @NbtFile(variant = Java, compression = None, tagName = "root")
 * data class ExampleFile(val string: String, val int: Int)
 *
 * // Encodes to {root:{string:"Hello, world!",int:42}}
 * val encoded = Nbt.encodeToNbtTag(ExampleFile("Hello, world!", 42))
 * ```
 *
 * *Note*: There are issues with Kotlin 1.5.0's serialization plugin:
 * [KT-46739](https://youtrack.jetbrains.com/issue/KT-46739)
 * [KT-46740](https://youtrack.jetbrains.com/issue/KT-46740)
 */
@Deprecated(
    "Use @NbtRoot(name) instead.",
    ReplaceWith("NbtRoot(tagName)", "net.benwoodworth.knbt.NbtRoot"),
    DeprecationLevel.ERROR,
)
public class NbtFile(
    public val variant: NbtVariant,
    public val compression: NbtCompression,
    public val tagName: String = "",
)
