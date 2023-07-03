package net.benwoodworth.knbt.test.parameterize.arguments

import net.benwoodworth.knbt.test.parameterize.ParameterScope

fun ParameterScope.doubleEdgeCases(): List<Double> =
    listOf(
        0.0,
        -0.0,
        1.0,
        -1.0,
        Double.MAX_VALUE,
        Double.MIN_VALUE,
        -Double.MAX_VALUE,
        -Double.MIN_VALUE,
        Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY,
        Double.NaN,
    )

/**
 * Returns a list of different values adjacent to the given [double].
 */
fun ParameterScope.doubleEdgeCases(double: Double): List<Double> =
    when (double) {
        Double.NEGATIVE_INFINITY -> listOf(-Double.MAX_VALUE)
        Double.POSITIVE_INFINITY -> listOf(Double.MAX_VALUE)
        Double.MIN_VALUE -> listOf(-0.0, 0.0, Double.MIN_VALUE.nextUpRaw())
        -Double.MIN_VALUE -> listOf(-0.0, 0.0, -Double.MIN_VALUE.nextUpRaw())
        Double.MAX_VALUE -> listOf(Double.POSITIVE_INFINITY, Double.MAX_VALUE.nextDownRaw())
        -Double.MAX_VALUE -> listOf(Double.NEGATIVE_INFINITY, -Double.MAX_VALUE.nextDownRaw())
        else -> when (double.toBits()) {
            0.0.toBits() -> listOf(-Double.MIN_VALUE, -0.0, Double.MIN_VALUE)
            (-0.0).toBits() -> listOf(-Double.MIN_VALUE, 0.0, Double.MIN_VALUE)
            Double.NaN.toBits() -> listOf(double.nextNaN())
            else -> listOf(double.nextDownRaw(), double.nextUpRaw())
        }
    }

// https://stackoverflow.com/a/3658280
private fun Double.nextUpRaw(): Double =
    Double.fromBits(this.toRawBits() + 1)

// https://stackoverflow.com/a/3658280
private fun Double.nextDownRaw(): Double =
    Double.fromBits(this.toRawBits() - 1)

// https://www.ibm.com/docs/en/xcafbg/9.0.0?topic=SS3KZ4_9.0.0/com.ibm.xlf111.bg.doc/xlfopg/fpieee.html
private fun Double.nextNaN(): Double {
    require(this.isNaN()) { "Value must be NaN" }

    // Double NaN Ranges:
    // 0x7FF0000000000001uL..0x7FF7FFFFFFFFFFFFuL
    // 0xFFF0000000000001uL..0xFFF7FFFFFFFFFFFFuL
    // 0x7FF8000000000000uL..0x7FFFFFFFFFFFFFFFuL
    // 0xFFF8000000000000uL..0xFFFFFFFFFFFFFFFFuL

    val nextNaN = when (val rawBits = this.toRawBits().toULong()) {
        // if at the end of a NaN range -> start of next range
        0x7FF7FFFFFFFFFFFFuL -> 0xFFF0000000000001uL
        0xFFF7FFFFFFFFFFFFuL -> 0x7FF8000000000000uL
        0x7FFFFFFFFFFFFFFFuL -> 0xFFF8000000000000uL
        0xFFFFFFFFFFFFFFFFuL -> 0x7FF0000000000001uL

        else -> rawBits + 1u
    }

    return Double.fromBits(nextNaN.toLong())
}
