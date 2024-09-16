package net.benwoodworth.knbt.test

import com.benwoodworth.parameterize.ParameterizeFailedError
import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterize
import kotlin.contracts.contract
import kotlin.reflect.KProperty

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

// The mapped parameters could be done more seamlessly with context parameters, with the `provideDelegate` and
// `getValue` operators taking the ParameterizeScope from the calling context instead of explicitly passing the scope
// and holding onto it. Maybe refactor later once context parameters are available in Kotlin.

/**
 * Returns a parameter that is reported with the given [name] and [argument] when listed in a [ParameterizeFailedError].
 */
inline fun <T> ParameterizeScope.Parameter<T>.reportedAs(
    parameterizeScope: ParameterizeScope,
    name: String,
    crossinline argument: (T) -> Any?
): MappedParameter<T> =
    MappedParameter(
        parameterizeScope,
        name,
        arguments.map { actualArgument ->
            MappedParameterArgument(actualArgument, argument(actualArgument))
        }
    )


class MappedParameterArgument<out T>(
    val argument: T,
    private val mappedArgument: Any?
) {
    override fun toString(): String = mappedArgument.toString()
}

private class MappedKProperty<T>(
    val property: KProperty<T>,
    override val name: String
) : KProperty<T> by property {
    override fun equals(other: Any?): Boolean =
        other is MappedKProperty<*> && property == other.property && name == other.name

    override fun hashCode(): Int = property.hashCode() * 31 + name.hashCode()
}

class MappedParameter<out T>(
    private val parameterizeScope: ParameterizeScope,
    private val name: String?,
    private val arguments: Sequence<MappedParameterArgument<T>>
) {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): MappedParameterDelegate<T> {
        val mappedProperty = if (name == null) {
            property
        } else {
            MappedKProperty(property, name)
        }

        return with(parameterizeScope) {
            MappedParameterDelegate(
                parameterizeScope,
                parameter(arguments).provideDelegate(thisRef, mappedProperty)
            )
        }
    }
}

class MappedParameterDelegate<out T>(
    private val parameterizeScope: ParameterizeScope,
    private val parameterDelegate: ParameterizeScope.ParameterDelegate<MappedParameterArgument<T>>
) {
    override fun toString(): String = parameterDelegate.toString()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
        with(parameterizeScope) {
            parameterDelegate.getValue(thisRef, property).argument
        }
}
