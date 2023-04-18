package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import net.benwoodworth.knbt.NbtString

private val specialCodepoints = Arb.choose(
    10 to Codepoint.ascii(),
    1 to Arb.codepoints(),
)

fun Arb.Companion.nbtString(): Arb<NbtString> = Arb
    .string(0..20, specialCodepoints)
    .map { NbtString(it) }
