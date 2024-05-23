package net.benwoodworth.knbt.test

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterize
import kotlin.contracts.contract

inline fun parameterizeTest(
    recordFailures: Long = 10,
    maxFailures: Long = Long.MAX_VALUE,
    block: ParameterizeScope.() -> Unit
) {
    var hasTestCases = false

    parameterize(
        onFailure = {
            recordFailure = failureCount <= recordFailures
            breakEarly = failureCount >= maxFailures
        }
    ) {
        block()
        hasTestCases = true
    }

    // If all iterations have a parameter with 0 arguments, then no iterations will actually (fully) execute.
    // This sanity check prevents bad tests from silently passing if they accidentally run like that.
    check(hasTestCases) { "Bad parameterized test: All test cases skipped" }
}

/**
 * Useful for stating assumptions about the [condition]s in which a test is meaningful.
 * A failed assumption does not mean the code is broken, but that the test provides no useful information.
 * Assume basically means "don't run this test if these [condition]s don't apply".
 */
fun ParameterizeScope.assume(condition: Boolean) {
    contract {
        returns() implies condition
    }

    if (!condition) {
        @Suppress("UnusedVariable", "unused") // Has a side effect, continuing the parameterize loop
        val skip: Nothing by parameter(emptyList())

        error("Should have skipped when declaring empty parameter")
    }
}
