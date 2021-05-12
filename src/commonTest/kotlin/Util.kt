package net.benwoodworth.knbt

import okio.Buffer
import okio.Source
import okio.Timeout
import kotlin.test.assertEquals
import kotlin.test.fail

infix fun <T> T.shouldReturn(expected: T): Unit =
    assertEquals(expected, this, "Incorrect return value.")

inline fun <T> assertStructureEquals(
    expected: T, actual: T, message: String? = null,
    assert: StructureAsserter<T>.() -> Unit,
) {
    val stringBuilder = StringBuilder()
    StructureAsserter(expected, actual, stringBuilder).assert()

    if (stringBuilder.isNotEmpty()) {
        fail(message ?: "Structures are not equal.$stringBuilder")
    }
}

class StructureAsserter<T>(private val expected: T, private val actual: T, private val stringBuilder: StringBuilder) {
    fun <P> property(propertyName: String, property: T.() -> P) {
        val expectedProperty = expected.property()
        val actualProperty = actual.property()

        if (expectedProperty != actualProperty) {
            stringBuilder.append("\n$propertyName: Expected <$expectedProperty>, actual <$actualProperty>.")
        }
    }
}

fun ByteArray.asSource(): Source = object : Source {
    private var offset: Int = 0

    override fun close(): Unit = Unit

    override fun read(sink: Buffer, byteCount: Long): Long {
        val remaining = this@asSource.size - offset
        return if (remaining == 0) {
            -1L
        } else {
            val byteCountInt = minOf(remaining.toLong(), byteCount).toInt()
            sink.write(this@asSource, offset, byteCountInt)
            offset += byteCountInt
            byteCountInt.toLong()
        }
    }

    override fun timeout(): Timeout = Timeout.NONE
}
