package net.benwoodworth.knbt.test.parameterize.arguments

import net.benwoodworth.knbt.test.parameterize.ParameterScope

fun ParameterScope.bytes(): List<Byte> =
    CompleteList((Byte.MAX_VALUE - Byte.MIN_VALUE)) { index ->
        (Byte.MIN_VALUE + index).toByte()
    }

fun ParameterScope.byteEdgeCases(): List<Byte> =
    listOf(0, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE)

fun ParameterScope.byteEdgeCases(byte: Byte): List<Byte> =
    when (byte) {
        Byte.MIN_VALUE -> listOf((Byte.MIN_VALUE + 1).toByte())
        Byte.MAX_VALUE -> listOf((Byte.MAX_VALUE - 1).toByte())
        else -> listOf((byte - 1).toByte(), (byte + 1).toByte())
    }


fun ParameterScope.ubytes(): List<UByte> =
    CompleteList((UByte.MAX_VALUE - UByte.MIN_VALUE).toInt()) { index ->
        (UByte.MIN_VALUE + index.toUInt()).toUByte()
    }

fun ParameterScope.ubyteEdgeCases(): List<UByte> =
    listOf(0u, 1u, UByte.MAX_VALUE)

fun ParameterScope.ubyteEdgeCases(ubyte: UByte): List<UByte> =
    when (ubyte) {
        UByte.MIN_VALUE -> listOf((UByte.MIN_VALUE + 1u).toUByte())
        UByte.MAX_VALUE -> listOf((UByte.MAX_VALUE - 1u).toUByte())
        else -> listOf((ubyte - 1u).toUByte(), (ubyte + 1u).toUByte())
    }
