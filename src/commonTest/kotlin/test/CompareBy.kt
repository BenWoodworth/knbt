package net.benwoodworth.knbt.test

import net.benwoodworth.knbt.*
import kotlin.test.assertTrue

fun interface CompareBy<in T> {
    fun get(value: T): Any?

    companion object {
        val Self: CompareBy<Any?> = CompareBy { it }
    }
}

fun <T> CompareBy<T>.assertEquals(expected: T, actual: T, message: String? = null) {
    val messagePrefix = if (message == null) "" else "$message. "
    assertTrue("${messagePrefix}Expected <$expected>, actual <$actual>.") {
        get(expected) == get(actual)
    }
}

fun Float.Companion.compareByBinary(): CompareBy<Float> = CompareBy { it.toRawBits() }

fun Double.Companion.compareByBinary(): CompareBy<Double> = CompareBy { it.toRawBits() }

fun NbtTag.Companion.compareByBinary(): CompareBy<NbtTag> = CompareBy { nbtTag ->
    fun NbtTag.toBinaryEqualityStructure(): Any = type to when (this) {
        is NbtByte -> this
        is NbtShort -> this
        is NbtInt -> this
        is NbtLong -> this
        is NbtString -> this

        is NbtFloat -> value.toRawBits()
        is NbtDouble -> value.toRawBits()

        is NbtList<*> -> elementType to this.map { it.toBinaryEqualityStructure() }

        is NbtByteArray -> content.asList()
        is NbtIntArray -> content.asList()
        is NbtLongArray -> content.asList()

        is NbtCompound -> mapValues { (_, value) -> value.toBinaryEqualityStructure() }
    }

    nbtTag.toBinaryEqualityStructure()
}
