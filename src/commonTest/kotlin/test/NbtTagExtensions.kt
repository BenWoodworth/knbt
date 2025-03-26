package net.benwoodworth.knbt.test

import net.benwoodworth.knbt.NbtNamed
import net.benwoodworth.knbt.NbtTag

fun NbtTag.named(name: String): NbtNamed<NbtTag>? =
    NbtNamed(name, this)
