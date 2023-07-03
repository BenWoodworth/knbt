package net.benwoodworth.knbt.test.parameterize.arguments

import net.benwoodworth.knbt.test.parameterize.ParameterScope

fun ParameterScope.shorts(): List<Short> =
    CompleteList(Short.MAX_VALUE - Short.MIN_VALUE) { index ->
        (Short.MIN_VALUE + index).toShort()
    }

fun ParameterScope.shortEdgeCases(): List<Short> =
    listOf(0, 1, -1, Short.MIN_VALUE, Short.MAX_VALUE)

fun ParameterScope.shortEdgeCases(short: Short): List<Short> =
    when (short) {
        Short.MIN_VALUE -> listOf((Short.MIN_VALUE + 1).toShort())
        Short.MAX_VALUE -> listOf((Short.MAX_VALUE - 1).toShort())
        else -> listOf((short - 1).toShort(), (short + 1).toShort())
    }


fun ParameterScope.ushorts(): List<UShort> =
    CompleteList((UShort.MAX_VALUE - UShort.MIN_VALUE).toInt()) { index ->
        (UShort.MIN_VALUE + index.toUInt()).toUShort()
    }

fun ParameterScope.ushortEdgeCases(): List<UShort> =
    listOf(0u, 1u, UShort.MAX_VALUE)

fun ParameterScope.ushortEdgeCases(ushort: UShort): List<UShort> =
    when (ushort) {
        UShort.MIN_VALUE -> listOf((UShort.MIN_VALUE + 1u).toUShort())
        UShort.MAX_VALUE -> listOf((UShort.MAX_VALUE - 1u).toUShort())
        else -> listOf((ushort - 1u).toUShort(), (ushort + 1u).toUShort())
    }
