package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import net.benwoodworth.knbt.NbtTag

fun Arb.Companion.nbtTag(maxNesting: Int = 2): Arb<NbtTag> = Arb
    .choice(
        Arb.nbtByte(),
        Arb.nbtShort(),
        Arb.nbtInt(),
        Arb.nbtLong(),
        Arb.nbtFloat(),
        Arb.nbtDouble(),
        Arb.nbtList(maxNesting),
        Arb.nbtCompound(maxNesting),
        Arb.nbtByteArray(),
        Arb.nbtIntArray(),
        Arb.nbtLongArray(),
    )
