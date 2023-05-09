package net.benwoodworth.knbt.test.generators

import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.map


// Just for symmetry between similar tests with `cartesian`
fun <A, B> Exhaustive.Companion.cartesian(a: Exhaustive<A>, f: (A) -> B): Exhaustive<B> =
    a.map(f)
