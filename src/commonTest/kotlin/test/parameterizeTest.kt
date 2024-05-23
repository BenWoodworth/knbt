package net.benwoodworth.knbt.test

import com.benwoodworth.parameterize.ExperimentalParameterizeApi
import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameterize

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

@OptIn(ExperimentalParameterizeApi::class)
inline fun <T> ParameterizeScope.Parameter<T>.filter(
    crossinline predicate: (argument: T) -> Boolean
): ParameterizeScope.Parameter<T> =
    ParameterizeScope.Parameter(arguments.filter { predicate(it) })
