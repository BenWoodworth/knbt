package net.benwoodworth.knbt

import io.kotest.core.spec.style.FunSpec
import kotlin.test.fail

class KotestSpec : FunSpec({
    test("Fail test") {
        fail("failed :(")
    }

    test("Pass Test") {
        println("Pass!")
    }

    context("context") {
        test("context test fail") {
            fail("failed :(")
        }

        test("Pass Test") {
            println("Pass!")
        }
    }
})
