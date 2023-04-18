package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import net.benwoodworth.knbt.*

@OptIn(UnsafeNbtApi::class)
private fun <T : NbtTag> Arb.Companion.nbtList(e: Arb<T>): Arb<NbtList<T>> = Arb
    .list(e)
    .map { NbtList(it) }


fun Arb.Companion.nbtList(maxNesting: Int = 2): Arb<NbtList<*>> =
    if (maxNesting <= 0) {
        Arb.nbtListOfNothing()
    } else {
        Arb.choice(
            Arb.nbtListOfNothing(),
            Arb.nbtListOfBytes(),
            Arb.nbtListOfShorts(),
            Arb.nbtListOfInts(),
            Arb.nbtListOfLongs(),
            Arb.nbtListOfFloats(),
            Arb.nbtListOfDoubles(),
            Arb.nbtListOfStrings(),
            Arb.nbtListOfByteArrays(),
            Arb.nbtListOfIntArrays(),
            Arb.nbtListOfLongArrays(),
            Arb.nbtListOfLists(maxNesting),
            Arb.nbtListOfCompounds(maxNesting),
        )
    }


fun Arb.Companion.nbtListOfNothing(): Arb<NbtList<Nothing>> =
    Arb.of(NbtList(emptyList()))

fun Arb.Companion.nbtListOfBytes(): Arb<NbtList<NbtByte>> =
    Arb.nbtList(Arb.nbtByte())

fun Arb.Companion.nbtListOfShorts(): Arb<NbtList<NbtShort>> =
    Arb.nbtList(Arb.nbtShort())

fun Arb.Companion.nbtListOfInts(): Arb<NbtList<NbtInt>> =
    Arb.nbtList(Arb.nbtInt())

fun Arb.Companion.nbtListOfLongs(): Arb<NbtList<NbtLong>> =
    Arb.nbtList(Arb.nbtLong())

fun Arb.Companion.nbtListOfFloats(): Arb<NbtList<NbtFloat>> =
    Arb.nbtList(Arb.nbtFloat())

fun Arb.Companion.nbtListOfDoubles(): Arb<NbtList<NbtDouble>> =
    Arb.nbtList(Arb.nbtDouble())

fun Arb.Companion.nbtListOfStrings(): Arb<NbtList<NbtString>> =
    Arb.nbtList(Arb.nbtString())


fun Arb.Companion.nbtListOfByteArrays(): Arb<NbtList<NbtByteArray>> =
    Arb.nbtList(Arb.nbtByteArray())

fun Arb.Companion.nbtListOfIntArrays(): Arb<NbtList<NbtIntArray>> =
    Arb.nbtList(Arb.nbtIntArray())

fun Arb.Companion.nbtListOfLongArrays(): Arb<NbtList<NbtLongArray>> =
    Arb.nbtList(Arb.nbtLongArray())


fun Arb.Companion.nbtListOfLists(maxNesting: Int = 2): Arb<NbtList<NbtList<*>>> =
    Arb.nbtList(Arb.nbtList(maxNesting - 1))


private fun Arb.Companion.nbtListOfCompounds(maxNesting: Int = 2): Arb<NbtList<NbtCompound>> =
    Arb.nbtList(Arb.nbtCompound(maxNesting - 1))
