package net.benwoodworth.knbt.test.parameterize.arguments

import net.benwoodworth.knbt.test.parameterize.ParameterScope

fun ParameterScope.intEdgeCases(): List<Int> =
    listOf(0, 1, -1, Int.MIN_VALUE, Int.MAX_VALUE)

fun ParameterScope.intEdgeCases(int: Int): List<Int> =
    when (int) {
        Int.MIN_VALUE -> listOf(Int.MIN_VALUE + 1)
        Int.MAX_VALUE -> listOf(Int.MAX_VALUE - 1)
        else -> listOf(int - 1, int + 1)
    }


fun ParameterScope.uintEdgeCases(): List<UInt> =
    listOf(0u, 1u, UInt.MAX_VALUE)

fun ParameterScope.uintEdgeCases(uint: UInt): List<UInt> =
    when (uint) {
        UInt.MIN_VALUE -> listOf(UInt.MIN_VALUE + 1u)
        UInt.MAX_VALUE -> listOf(UInt.MAX_VALUE - 1u)
        else -> listOf(uint - 1u, uint + 1u)
    }
