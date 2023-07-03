package net.benwoodworth.knbt.test.parameterize.arguments

import net.benwoodworth.knbt.test.parameterize.ParameterScope
import kotlin.reflect.KProperty

fun ParameterScope.chars(): List<Char> =
    CompleteList(Char.MAX_VALUE - Char.MIN_VALUE) { index ->
        (Char.MIN_VALUE + index)
    }

fun ParameterScope.charEdgeCases(): List<Char> =
    listOf(Char(0u))

fun ParameterScope.charEdgeCases(char: Char): List<Char> =
    buildList(4) {
        if (char != Char.MIN_VALUE) add(char - 1)
        if (char != Char.MAX_VALUE) add(char + 1)

        char.lowercaseChar().let { lowercase ->
            if (char != lowercase) add(lowercase)
        }

        char.uppercaseChar().let { uppercase ->
            if (char != uppercase) add(uppercase)
        }
    }
