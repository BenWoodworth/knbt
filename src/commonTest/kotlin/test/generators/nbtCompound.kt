package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import net.benwoodworth.knbt.NbtCompound

fun Arb.Companion.nbtCompound(maxNesting: Int = 2): Arb<NbtCompound> =
    if (maxNesting <= 0) {
        Arb.of(NbtCompound(emptyMap()))
    } else {
        Arb
            .map(
                Arb.nbtString().map { it.value },
                Arb.nbtTag(maxNesting - 1)
            )
            .map { NbtCompound(it) }
    }
