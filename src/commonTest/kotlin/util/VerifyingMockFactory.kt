package net.benwoodworth.knbt.util

import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldStartWith
import kotlin.jvm.JvmName
import kotlin.reflect.KFunction
import kotlin.test.assertContentEquals

abstract class VerifyingMockFactory<T : Any, TBuilder : VerifyingMockFactory.Builder<T>>(
    private val builderConstructor: () -> TBuilder,
) {
    fun create(builderAction: TBuilder.() -> Unit): Verifier<T> {
        val builder = builderConstructor()
        builder.builderAction()

        return builder.run {
            build()
        }
    }

    class Verifier<T> private constructor(
        private val mockConstructor: () -> T,
    ) {
        fun <R> verify(block: (callee: T) -> R): R {
            val mock = mockConstructor()
            check(mock is Mock) { "Expected mockConstructor() to return a Mock" }

            return block(mock).also {
                mock.run { assertAllCallsMade() }
            }
        }

        companion object {
            @Suppress("UnusedReceiverParameter", "TestFunctionName") // Limit visibility to only the Builder
            fun <T : Any> Builder<T>.Verifier(mockConstructor: () -> T): Verifier<T> =
                VerifyingMockFactory.Verifier(mockConstructor)
        }
    }

    protected abstract class Mock {
        private lateinit var expectedCallReturns: List<CallReturn<*>>

        @Suppress("UnusedReceiverParameter") // Limit visibility to only the Builder
        fun Builder<*>.setExpectedCallReturns(callReturns: List<CallReturn<*>>) {
            expectedCallReturns = callReturns
        }

        private val actualCalls = mutableListOf<Call<*>>()

        @Suppress("UnusedReceiverParameter") // Limit visibility to only the CallVerifier
        fun Verifier<*>.assertAllCallsMade() {
            assertContentEquals(expectedCallReturns.map { it.call }, actualCalls)
        }

        protected fun <R> KFunction<R>.called(vararg args: Any?): R {
            val actualCall = Call<R>(this.name, args.asList())
            actualCalls += actualCall

            val expectedCallReturn = expectedCallReturns.getOrNull(actualCalls.lastIndex)
            val expectedCall = expectedCallReturn?.call

            val assertionError = when {
                expectedCall == null -> "Expected calls to be concluded, but was $actualCall"
                expectedCall != actualCall -> "Expected to be called with $expectedCall, but was $actualCall"
                else -> null
            }

            if (assertionError != null) {
                withClue(assertionError) {
                    val expectedCalls = expectedCallReturns.map { it.call }
                    expectedCalls.shouldStartWith(actualCalls)
                }
            }

            // expectedCall == actualCall, so expectedCallReturn != null, and the return types are the same
            @Suppress("UNCHECKED_CAST")
            return expectedCallReturn!!.returns as R
        }
    }

    data class Call<R>(val function: String, val args: List<Any?>) {
        override fun toString(): String =
            args.joinToString(prefix = "${function}(", postfix = ")")
    }

    data class CallReturn<R>(val call: Call<R>, val returns: R) {
        override fun toString(): String =
            if (returns == Unit) "$call" else "$call -> $returns"
    }

    abstract class Builder<T : Any>(
        private val mockConstructor: () -> T,
    ) {
        private val callReturns = mutableListOf<CallReturn<*>>()
        private var currentCall: Call<*>? = null

        private fun <R> handleCall(name: String, args: Array<out Any?>): Call<R> {
            val call = Call<R>(name, args.asList())

            check(currentCall == null) { "Expected `returns ...` after $currentCall, but got $call" }

            currentCall = call
            return call
        }

        infix fun <R> Call<R>.returns(value: R) {
            val call = currentCall

            check(call != null) { "Can only use `returns` once, but was called again on $this" }
            check(call == this) { "Expected `$call returns ...`, but got `$this returns ...`" }

            callReturns += CallReturn(this, value)
            currentCall = null
        }

        protected fun <R> KFunction<Call<R>>.called(vararg args: Any?): Call<R> =
            handleCall(name, args)

        @JvmName("called\$Unit")
        protected fun KFunction<Unit>.called(vararg args: Any?): Unit =
            handleCall<Unit>(name, args) returns Unit

        @Suppress("UnusedReceiverParameter") // Limit visibility to only the CallVerifierFactory
        fun VerifyingMockFactory<*, *>.build(): Verifier<T> {
            check(currentCall == null) { "Expected `returns ...` after $currentCall" }

            val expectedCallReturns = callReturns.toList()

            Verifier.run {
                return Verifier {
                    val mock = mockConstructor()
                    check(mock is Mock)

                    mock.apply {
                        setExpectedCallReturns(expectedCallReturns)
                    }
                }
            }
        }
    }
}
