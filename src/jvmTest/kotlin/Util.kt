package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.NbtReader
import java.io.IOException
import java.io.InputStream
import java.nio.file.Path
import kotlin.io.path.useLines
import kotlin.test.assertEquals

fun getResourceAsStream(name: String): InputStream =
    (object {})::class.java.getResourceAsStream(name) ?: throw IOException("Resource not found: $name")

fun getResourceAsText(name: String): String =
    getResourceAsStream(name).use { it.reader().readText() }

fun assertHexEquals(expected: Iterable<Byte>, actual: Iterable<Byte>, message: String? = null) {
    fun Iterable<Byte>.toHex(): String = joinToString(" ") { byte ->
        byte.toUByte().toString(16).uppercase().padStart(2, '0')
    }

    fun Iterable<Byte>.toChars(): String = joinToString(" ") { byte ->
        val char = byte.toInt().toChar()
        if (char.isISOControl() || byte < 0) "  " else " $char"
    }

    assertEquals(
        expected = "${expected.toHex()}\n${expected.toChars()}",
        actual = "${actual.toHex()}\n${actual.toChars()}",
        message = message,
    )
}

fun assertTextFileEquals(expected: Path, actual: Path, message: String? = null) {
    expected.useLines { expectedLines ->
        actual.useLines { actualLines ->
            val e = expectedLines.iterator()
            val a = actualLines.iterator()

            var line = 0
            while (e.hasNext() || a.hasNext()) {
                val eLine = e.takeIf { it.hasNext() }?.next()
                val aLine = a.takeIf { it.hasNext() }?.next()

                assert(eLine == aLine) {
                    buildString {
                        if (message != null) append(message).append(". ")
                        append("Files differ at line $line. Expected <$expected>, actual <$actual>.")
                    }
                }
                line++
            }
        }
    }
}

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
