package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtLongArray

fun Arb.Companion.nbtByteArray(size: IntRange = 0..4): Arb<NbtByteArray> = Arb
    .list(Arb.nbtByte(), size)
    .map { list ->
        NbtByteArray(list.map { it.value })
    }

fun Arb.Companion.nbtIntArray(size: IntRange = 0..4): Arb<NbtIntArray> = Arb
    .list(Arb.nbtInt(), size)
    .map { list ->
        NbtIntArray(list.map { it.value })
    }

fun Arb.Companion.nbtLongArray(size: IntRange = 0..4): Arb<NbtLongArray> = Arb
    .list(Arb.nbtLong(), size)
    .map { list ->
        NbtLongArray(list.map { it.value })
    }
