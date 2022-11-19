package net.benwoodworth.knbt.test

import net.benwoodworth.knbt.*

fun Float.binaryEquals(other: Float): Boolean =
    this.toRawBits() == other.toRawBits()

fun Double.binaryEquals(other: Double): Boolean =
    this.toRawBits() == other.toRawBits()

fun NbtTag.binaryEquals(other: NbtTag): Boolean = when (this) {
    is NbtByte -> other is NbtByte && value == other.value
    is NbtShort -> other is NbtShort && value == other.value
    is NbtInt -> other is NbtInt && value == other.value
    is NbtLong -> other is NbtLong && value == other.value
    is NbtFloat -> other is NbtFloat && value.binaryEquals(other.value)
    is NbtDouble -> other is NbtDouble && value.binaryEquals(other.value)
    is NbtString -> other is NbtString && value == other.value

    is NbtByteArray -> other is NbtByteArray && content.contentEquals(other.content)
    is NbtIntArray -> other is NbtIntArray && content.contentEquals(other.content)
    is NbtLongArray -> other is NbtLongArray && content.contentEquals(other.content)

    is NbtList<*> -> other is NbtList<*> &&
            elementType == other.elementType &&
            size == other.size &&
            this.zip(other).all { (element, otherElement) ->
                element.binaryEquals(otherElement)
            }

    is NbtCompound -> other is NbtCompound &&
            size == other.size &&
            this.entries.zip(other.entries).all { (entry, otherEntry) ->
                entry.key == otherEntry.key && entry.value.binaryEquals(otherEntry.value)
            }
}
