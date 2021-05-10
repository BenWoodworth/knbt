package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.NbtReader
import kotlin.test.assertEquals

fun Any?.toValueString(): String {
    fun Appendable.appendEscaped(value: Char): Appendable = when (value) {
        '\t' -> append("\\t")
        '\b' -> append("\\b")
        '\n' -> append("\\n")
        '\r' -> append("\\r")
        '\'' -> append("\\'")
        '\"' -> append("\\\"")
        '\\' -> append("\\\\")
        '\$' -> append("\\$")
        else -> if (value.isISOControl()) {
            append("\\u").append(value.code.toString(16).padStart(4, '0'))
        } else {
            append(value)
        }
    }

    fun Appendable.appendEscaped(value: String): Appendable =
        apply { value.forEach { appendEscaped(it) } }

    return when (val value = this) {
        is String -> buildString { append('"').appendEscaped(value).append('"') }
        is Char -> buildString { append('\'').appendEscaped(value).append('\'') }
        is Long -> buildString { append(value.toString()).append('L') }
        is Float -> buildString { append(value.toString()).append('f') }
        is NbtReader.RootTagInfo -> buildString {
            append("RootTagInfo(")
            append("type=")
            append(value.type.toValueString())
            append(')')
        }
        is NbtReader.CompoundEntryInfo -> buildString {
            append("CompoundEntryInfo(")
            append("type=")
            append(value.type.toValueString())
            append(", name=")
            append(value.name.toValueString())
            append(')')
        }
        is NbtReader.ListInfo -> buildString {
            append("ListInfo(")
            append("type=")
            append(value.type.toValueString())
            append(", size=")
            append(value.size.toValueString())
            append(')')
        }
        is NbtReader.ArrayInfo -> buildString {
            append("ArrayInfo(")
            append("size=")
            append(value.size.toValueString())
            append(')')
        }
        else -> value.toString()
    }
}

infix fun <T> T.shouldReturn(expected: T): Unit =
    assertEquals(expected, this, "Incorrect return value.")
