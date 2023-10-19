package net.benwoodworth.knbt.test

import okio.Buffer
import okio.Source
import okio.Timeout
import kotlin.test.assertEquals
import kotlin.test.fail

infix fun Int.pow(x: Int): Int {
    var result = 1
    repeat(x) { result *= this }
    return result
}

infix fun Long.pow(x: Int): Long {
    var result = 1L
    repeat(x) { result *= this }
    return result
}

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
            fun Any?.className() = if (this == null) "null" else this::class.simpleName

            stringBuilder
                .append("\n$propertyName: ")
                .append("Expected <${expectedProperty}> (${expectedProperty.className()}), ")
                .append("actual <${actualProperty}> (${actualProperty.className()}).")
        }
    }
}

fun Float.toBinary(): String = toRawBits().toUInt().toString(2).padStart(32, '0')
fun Double.toBinary(): String = toRawBits().toULong().toString(2).padStart(64, '0')

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

inline fun <T> parameterize(
    parameters: List<T>,
    description: T.() -> Any? = { this },
    assert: T.() -> Unit,
) {
    val errors = parameters.map { parameter ->
        val error = try {
            parameter.assert()
            null
        } catch (t: Throwable) {
            t
        }

        parameter to error
    }

    errors.forEach { (parameter, error) ->
        val passOrFail = if (error == null) "PASS" else "FAIL"

        println("[$passOrFail] ${description(parameter)}")
        if (error != null) {
            if (error is AssertionError) {
                println("- ${error.message}")
            } else {
                error.printStackTrace()
            }
        }
        if (error != null) println()
    }

    val failCount = errors.count { (_, error) -> error != null }
    println("Passed: ${parameters.size - failCount}/${parameters.size}")

    if (failCount > 0) fail()
}

/**
 * Work around Kotlin/JS representing Float values slightly differently.
 * TODO https://github.com/BenWoodworth/knbt/issues/3
 */
fun Float.fix(): Float =
    Float.fromBits(this.toRawBits())

/**
 * Work around Kotlin/JS representing Double values slightly differently.
 * TODO https://github.com/BenWoodworth/knbt/issues/3
 */
fun Double.fix(): Double =
    Double.fromBits(this.toRawBits())
