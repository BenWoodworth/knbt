package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtLongArray

fun Arb.Companion.nbtByteArray(): Arb<NbtByteArray> = Arb
    .list(Arb.nbtByte())
    .map { list ->
        NbtByteArray(list.map { it.value })
    }

fun Arb.Companion.nbtIntArray(): Arb<NbtIntArray> = Arb
    .list(Arb.nbtInt())
    .map { list ->
        NbtIntArray(list.map { it.value })
    }

fun Arb.Companion.nbtLongArray(): Arb<NbtLongArray> = Arb
    .list(Arb.nbtLong())
    .map { list ->
        NbtLongArray(list.map { it.value })
    }
