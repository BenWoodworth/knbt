package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import net.benwoodworth.knbt.*

fun Arb.Companion.nbtByte(): Arb<NbtByte> =
    byte().map(::NbtByte)

fun Arb.Companion.nbtShort(): Arb<NbtShort> =
    short().map(::NbtShort)

fun Arb.Companion.nbtInt(): Arb<NbtInt> =
    int().map(::NbtInt)

fun Arb.Companion.nbtLong(): Arb<NbtLong> =
    long().map(::NbtLong)


private val floatNaNs = Arb
    .choice(
        Arb.uInt(0x7F80_0001u..0x7FBF_FFFFu), // Signaling NaNs
        Arb.uInt(0xFF80_0001u..0xFFBF_FFFFu), // Signaling NaNs
        Arb.uInt(0x7FC0_0000u..0x7FFF_FFFFu), // Quiet NaNs
        Arb.uInt(0xFFC0_0000u..0xFFFF_FFFFu), // Quiet NaNs
    )
    .map { Float.fromBits(it.toInt()) }

fun Arb.Companion.nbtFloat(): Arb<NbtFloat> = Arb
    .choose(
        9 to float(),
        1 to floatNaNs
    )
    .map { NbtFloat(it) }


private val doubleNaNs = Arb
    .choice(
        Arb.uLong(0x7FF0_0000_0000_0001uL..0x7FF7_FFFF_FFFF_FFFFuL), // Signaling NaNs
        Arb.uLong(0xFFF0_0000_0000_0001uL..0xFFF7_FFFF_FFFF_FFFFuL), // Signaling NaNs
        Arb.uLong(0x7FF8_0000_0000_0000uL..0x7FFF_FFFF_FFFF_FFFFuL), // Quiet NaNs
        Arb.uLong(0xFFF8_0000_0000_0000uL..0xFFFF_FFFF_FFFF_FFFFuL), // Quiet NaNs
    )
    .map { Double.fromBits(it.toLong()) }

fun Arb.Companion.nbtDouble(): Arb<NbtDouble> = Arb
    .choose(
        9 to double(),
        1 to doubleNaNs
    )
    .map { NbtDouble(it) }
