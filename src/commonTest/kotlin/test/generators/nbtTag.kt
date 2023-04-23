package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import net.benwoodworth.knbt.NbtTag

fun Arb.Companion.nbtTag(maxNesting: Int = 2, size: IntRange = 0..4): Arb<NbtTag> =
    choice(
        Arb.nbtByte(),
        Arb.nbtShort(),
        Arb.nbtInt(),
        Arb.nbtLong(),
        Arb.nbtFloat(),
        Arb.nbtDouble(),
        Arb.nbtList(maxNesting, size),
        Arb.nbtCompound(maxNesting, size),
        Arb.nbtByteArray(size),
        Arb.nbtIntArray(size),
        Arb.nbtLongArray(size),
    )
