package net.benwoodworth.knbt.test.parameterize.arguments

import net.benwoodworth.knbt.test.parameterize.ParameterScope


fun ParameterScope.longEdgeCases(): List<Long> =
    listOf(0, 1, -1, Long.MIN_VALUE, Long.MAX_VALUE)

fun ParameterScope.longEdgeCases(long: Long): List<Long> =
    when (long) {
        Long.MIN_VALUE -> listOf(Long.MIN_VALUE + 1)
        Long.MAX_VALUE -> listOf(Long.MAX_VALUE - 1)
        else -> listOf(long - 1, long + 1)
    }


fun ParameterScope.ulongEdgeCases(): List<ULong> =
    listOf(0u, 1u, ULong.MAX_VALUE)

fun ParameterScope.ulongEdgeCases(ulong: ULong): List<ULong> =
    when (ulong) {
        ULong.MIN_VALUE -> listOf(ULong.MIN_VALUE + 1u)
        ULong.MAX_VALUE -> listOf(ULong.MAX_VALUE - 1u)
        else -> listOf(ulong - 1u, ulong + 1u)
    }
