package net.benwoodworth.knbt.test.parameterize.arguments

import net.benwoodworth.knbt.test.parameterize.ParameterScope

fun ParameterScope.floatEdgeCases(): List<Float> =
    listOf(
        0.0f,
        -0.0f,
        1.0f,
        -1.0f,
        Float.MAX_VALUE,
        Float.MIN_VALUE,
        -Float.MAX_VALUE,
        -Float.MIN_VALUE,
        Float.NEGATIVE_INFINITY,
        Float.POSITIVE_INFINITY,
        Float.NaN,
    )

/**
 * Returns a list of different values adjacent to the given [float].
 */
fun ParameterScope.floatEdgeCases(float: Float): List<Float> =
    when (float) {
        Float.NEGATIVE_INFINITY -> listOf(-Float.MAX_VALUE)
        Float.POSITIVE_INFINITY -> listOf(Float.MAX_VALUE)
        Float.MIN_VALUE -> listOf(-0f, 0f, Float.MIN_VALUE.nextUpRaw())
        -Float.MIN_VALUE -> listOf(-0f, 0f, -Float.MIN_VALUE.nextUpRaw())
        Float.MAX_VALUE -> listOf(Float.POSITIVE_INFINITY, Float.MAX_VALUE.nextDownRaw())
        -Float.MAX_VALUE -> listOf(Float.NEGATIVE_INFINITY, -Float.MAX_VALUE.nextDownRaw())
        else -> when (float.toBits()) {
            0f.toBits() -> listOf(-Float.MIN_VALUE, -0f, Float.MIN_VALUE)
            (-0f).toBits() -> listOf(-Float.MIN_VALUE, 0f, Float.MIN_VALUE)
            Float.NaN.toBits() -> listOf(float.nextNaN())
            else -> listOf(float.nextDownRaw(), float.nextUpRaw())
        }
    }

// https://stackoverflow.com/a/3658280
private fun Float.nextUpRaw(): Float =
    Float.fromBits(this.toRawBits() + 1)

// https://stackoverflow.com/a/3658280
private fun Float.nextDownRaw(): Float =
    Float.fromBits(this.toRawBits() - 1)

// https://www.ibm.com/docs/en/xcafbg/9.0.0?topic=SS3KZ4_9.0.0/com.ibm.xlf111.bg.doc/xlfopg/fpieee.html
private fun Float.nextNaN(): Float {
    require(this.isNaN()) { "Value must be NaN" }

    // Float NaN Ranges:
    // 0x7F800001u..0x7FBFFFFFu
    // 0xFF800001u..0xFFBFFFFFu
    // 0x7FC00000u..0x7FFFFFFFu
    // 0xFFC00000u..0xFFFFFFFFu

    val nextNaN = when (val rawBits = this.toRawBits().toUInt()) {
        // if at the end of a NaN range -> start of next range
        0x7FBFFFFFu -> 0xFF800001u
        0xFFBFFFFFu -> 0x7FC00000u
        0x7FFFFFFFu -> 0xFFC00000u
        0xFFFFFFFFu -> 0x7F800001u

        else -> rawBits + 1u
    }

    return Float.fromBits(nextNaN.toInt())
}
