package net.benwoodworth.knbt

/**
 * Nest elements in a compound tag with the given [name].
 *
 * Example usage:
 * ```kotlin
 * @Serializable
 * @NbtRoot(name = "root")
 * class Example(val string: String, val int: Int)
 *
 * // Serializes to: {root : {string : "Hello, world!", int : 42}}
 * Nbt.encodeToNbtTag(Example(string = "Hello, World!", int = 42))
 * ```
 *
 * *Note*: Using the default value in Kotlin 1.5.0 causes an exception:
 * [KT-46739](https://youtrack.jetbrains.com/issue/KT-46739)
 */
@ExperimentalNbtApi
@Target(AnnotationTarget.CLASS)
@Deprecated(
    "Removed in favor of @SerialName",
    ReplaceWith("SerialName", "kotlinx.serialization.SerialName"),
    DeprecationLevel.ERROR,
)
public annotation class NbtRoot
@Deprecated(
    "Removed in favor of @SerialName",
    ReplaceWith("SerialName(name)", "kotlinx.serialization.SerialName"),
    DeprecationLevel.ERROR,
)
constructor(val name: String = "")
