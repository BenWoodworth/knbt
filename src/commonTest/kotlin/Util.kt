package net.benwoodworth.knbt

import kotlin.test.assertEquals

infix fun <T> T.shouldReturn(expected: T): Unit =
    assertEquals(expected, this, "Incorrect return value.")
