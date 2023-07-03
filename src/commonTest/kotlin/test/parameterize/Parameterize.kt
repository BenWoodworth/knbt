package net.benwoodworth.knbt.test.parameterize

import kotlin.reflect.KProperty

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
private annotation class ParameterizedTestDsl

fun parameterize(block: @ParameterizedTestDsl ParameterizeScope.() -> Unit) {
    val testScope = ParameterizeScopeImpl()

    do {
        try {
            testScope.block()
        } catch (failure: Throwable) {
            println(
                "Test failed with arguments:\n" +
                        testScope.getReadIterationValues().joinToString("\n")
            )

            throw failure
        }
    } while (testScope.finishIteration())
}

//private class ParameterizedTestError(
//    val arguments: List<Argument>,
//    override val cause: Throwable
//) : AssertionError() {
//    override val message: String =
//        "Parameterized test failed for arguments:\n" + arguments.joinToString("\n")
//}

@Suppress("unused") // For debugging
class Argument(
    val variable: KProperty<*>,
    val name: String,
    val value: Any?
) {
    override fun toString(): String =
        "$name = $value"
}

data class TestParameter<T>(
    val name: String?,
    val arguments: Lazy<List<T>>
) {
    private var initializedForVariable: KProperty<*>? = null
    private var initializedValue: T? = null

    private val initialized: Boolean
        get() = initializedForVariable != null

    val value: T
        get() {
            check(initialized) { "Test parameter has not been initialized" }

            @Suppress("UNCHECKED_CAST")
            return initializedValue as T
        }

    override fun toString(): String =
        if (initialized) value.toString() else "[uninitialized]"

    fun initialize(variable: KProperty<*>, value: T) {
        if (initializedForVariable != null) {
            check(variable === initializedForVariable) {
                "Cannot initialize for variable $variable. Already initialized with $initializedForVariable"
            }
            check(value === initializedValue) {
                "Cannot initialize with value $value. Already initialized with $initializedValue"
            }
            return
        }

        initializedForVariable = variable
        initializedValue = value
    }
}

@ParameterizedTestDsl
interface ParameterizeScope {
    fun <T> parameter(
        name: String? = null,
        arguments: @ParameterizedTestDsl ParameterScope.() -> List<T>
    ): TestParameter<T> =
        TestParameter(name, lazy { ParameterScope.arguments() })

    operator fun <T> TestParameter<T>.getValue(thisRef: Any?, variable: KProperty<*>): T
}

@ParameterizedTestDsl
object ParameterScope

private class ParameterizeScopeImpl : ParameterizeScope {
    private data class ParameterState<out T>(
        val variable: KProperty<*>,
        val name: String,
        val values: List<T>,
        val permutationPeriod: Int,
        var wasReadThisIteration: Boolean
    )

    private val parameters = mutableListOf<ParameterState<*>>()
    private var permutationIndex = 0
    private var permutationCount = 1

    private val <T> ParameterState<T>.iterationValue: T
        get() = values[permutationIndex / permutationPeriod % values.size]

    fun getReadIterationValues(): List<Argument> =
        parameters
            .filter { it.wasReadThisIteration }
            .map { Argument(it.variable, it.name, it.iterationValue) }

    fun finishIteration(): Boolean {
        parameters.forEach { it.wasReadThisIteration = false }

        permutationIndex++
        return permutationIndex < permutationCount
    }

    override operator fun <T> TestParameter<T>.getValue(thisRef: Any?, variable: KProperty<*>): T {
        @Suppress("UNCHECKED_CAST")
        var parameter = parameters
            .firstOrNull { it.variable == variable } as ParameterState<T>?

        if (parameter == null) {
            parameter = ParameterState(
                variable,
                name ?: variable.name,
                arguments.value,
                permutationPeriod = permutationCount,
                wasReadThisIteration = true
            )

            parameters += parameter
            permutationCount *= parameter.values.size
        }

        parameter.wasReadThisIteration = true
        val value = parameter.iterationValue

        initialize(variable, value)

        return value
    }
}
