package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import net.benwoodworth.knbt.*

@OptIn(UnsafeNbtApi::class)
private fun <T : NbtTag> Arb.Companion.nbtList(e: Arb<T>, size: IntRange): Arb<NbtList<T>> = Arb
    .list(e, size)
    .map { NbtList(it) }


fun Arb.Companion.nbtList(maxNesting: Int = 2, size: IntRange = 0..4): Arb<NbtList<*>> =
    if (maxNesting <= 0) {
        Arb.nbtListOfNothing()
    } else {
        Arb.choice(
            Arb.nbtListOfNothing(),
            Arb.nbtListOfBytes(size),
            Arb.nbtListOfShorts(size),
            Arb.nbtListOfInts(size),
            Arb.nbtListOfLongs(size),
            Arb.nbtListOfFloats(size),
            Arb.nbtListOfDoubles(size),
            Arb.nbtListOfStrings(size),
            Arb.nbtListOfByteArrays(size),
            Arb.nbtListOfIntArrays(size),
            Arb.nbtListOfLongArrays(size),
            Arb.nbtListOfLists(maxNesting, size),
            Arb.nbtListOfCompounds(maxNesting, size),
        )
    }


fun Arb.Companion.nbtListOfNothing(): Arb<NbtList<Nothing>> =
    Arb.of(NbtList(emptyList()))

fun Arb.Companion.nbtListOfBytes(size: IntRange = 0..4): Arb<NbtList<NbtByte>> =
    Arb.nbtList(Arb.nbtByte(), size)

fun Arb.Companion.nbtListOfShorts(size: IntRange = 0..4): Arb<NbtList<NbtShort>> =
    Arb.nbtList(Arb.nbtShort(), size)

fun Arb.Companion.nbtListOfInts(size: IntRange = 0..4): Arb<NbtList<NbtInt>> =
    Arb.nbtList(Arb.nbtInt(), size)

fun Arb.Companion.nbtListOfLongs(size: IntRange = 0..4): Arb<NbtList<NbtLong>> =
    Arb.nbtList(Arb.nbtLong(), size)

fun Arb.Companion.nbtListOfFloats(size: IntRange = 0..4): Arb<NbtList<NbtFloat>> =
    Arb.nbtList(Arb.nbtFloat(), size)

fun Arb.Companion.nbtListOfDoubles(size: IntRange = 0..4): Arb<NbtList<NbtDouble>> =
    Arb.nbtList(Arb.nbtDouble(), size)

fun Arb.Companion.nbtListOfStrings(size: IntRange = 0..4): Arb<NbtList<NbtString>> =
    Arb.nbtList(Arb.nbtString(), size)


fun Arb.Companion.nbtListOfByteArrays(size: IntRange = 0..4): Arb<NbtList<NbtByteArray>> =
    Arb.nbtList(Arb.nbtByteArray(), size)

fun Arb.Companion.nbtListOfIntArrays(size: IntRange = 0..4): Arb<NbtList<NbtIntArray>> =
    Arb.nbtList(Arb.nbtIntArray(), size)

fun Arb.Companion.nbtListOfLongArrays(size: IntRange = 0..4): Arb<NbtList<NbtLongArray>> =
    Arb.nbtList(Arb.nbtLongArray(), size)


fun Arb.Companion.nbtListOfLists(maxNesting: Int = 2, size: IntRange = 0..4): Arb<NbtList<NbtList<*>>> =
    Arb.nbtList(Arb.nbtList(maxNesting - 1), size)


private fun Arb.Companion.nbtListOfCompounds(maxNesting: Int = 2, size: IntRange = 0..4): Arb<NbtList<NbtCompound>> =
    Arb.nbtList(Arb.nbtCompound(maxNesting - 1), size)
