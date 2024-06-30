package net.benwoodworth.knbt

/** TODO wording
 * The serial representation of a [value], except its [NbtName] is replaced with [name].
 *
 * Named NBT formats
 *
 * when [encodeToNamedNbtTag], captures the [value] and its name
 * when [decodeFromNamedNbtTag],
 *
 * when encoding (name replaces) // TODO When serializer is implemented
 * when decoding (name captures) // TODO When serializer is implemented
 */
public class NbtNamed<out T>(
    public val name: String,
    public val value: T
) {
    override fun equals(other: Any?): Boolean =
        other is NbtNamed<*> && name == other.name && value == other.value

    override fun hashCode(): Int =
        name.hashCode() * 31 + value.hashCode()

    override fun toString(): String =
        "NbtNamed(name=$name, value=$value)"
}
