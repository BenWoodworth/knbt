package net.benwoodworth.knbt

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterize
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

inline fun parameterizeTest(block: ParameterizeScope.() -> Unit): Unit =
    parameterize(
        onFailure = { recordFailure = true },
        block = block
    )

/**
 * Work around Kotlin/JS representing Float values slightly differently.
 * TODO https://github.com/BenWoodworth/knbt/issues/3
 */
fun Float.fix(): Float =
    Float.fromBits(this.toRawBits())

class NbtFormatForParameter(
    private val name: String,
    private val build: ((NbtFormatBuilder.() -> Unit)) -> NbtFormat
) {
    override fun toString() = name
    operator fun invoke(builderAction: NbtFormatBuilder.() -> Unit = {}) = build(builderAction)
}

val nbtFormats = listOf(
    NbtFormatForParameter("Nbt { ... }") { builderAction ->
        Nbt {
            variant = NbtVariant.Java
            compression = NbtCompression.None
            builderAction()
        }
    },
    NbtFormatForParameter("StringifiedNbt { ... }") { builderAction ->
        StringifiedNbt {
            builderAction()
        }
    },
)

/**
 * Declares a "NbtFormat" parameter declared with [Nbt] and [StringifiedNbt] arguments,
 * and returns its current argument.
 */
fun ParameterizeScope.parameterizedNbtFormat(
    builderAction: NbtFormatBuilder.() -> Unit = {}
): NbtFormat {
    @Suppress("LocalVariableName") // Parameter name
    val NbtFormat by parameter(nbtFormats)

    return NbtFormat(builderAction)
}

fun NbtFormat(from: NbtFormat, builderAction: NbtFormatBuilder.() -> Unit): NbtFormat =
    when (from) {
        is Nbt -> Nbt(from, builderAction)
        is StringifiedNbt -> StringifiedNbt(from, builderAction)
    }
