package net.benwoodworth.knbt

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
