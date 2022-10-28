package net.benwoodworth.knbt.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

private interface MyInterface {
    fun functionWithReturn(arg: String): String
    fun function(n: Int)
}

private object VerifyingMyInterfaceMock :
    VerifyingMockFactory<MyInterface, VerifyingMyInterfaceMock.Builder>(::Builder) {
    class Mock : MyInterface, VerifyingMockFactory.Mock() {
        override fun functionWithReturn(arg: String): String = ::functionWithReturn.called(arg)
        override fun function(n: Int): Unit = ::function.called(n)
    }

    class Builder : VerifyingMockFactory.Builder<MyInterface>(::Mock) {
        fun functionWithReturn(arg: String): Call<String> = ::functionWithReturn.called(arg)
        fun function(n: Int): Unit = ::function.called(n)
    }
}

class VerifyingMockFactorySpec {
    private class PassEarly : Throwable()

    private fun <T> VerifyingMockFactory.Verifier<T>.verifyWithPassEarly(block: (callee: T) -> Unit) {
        try {
            verify(block)
        } catch (_: PassEarly) {
        }
    }

    private val verifier = VerifyingMyInterfaceMock.create {
        functionWithReturn("arg") returns "result"
        function(2)
    }

    @Test
    fun builder_should_error_on_call_without_returns_at_the_end() {
        shouldThrow<IllegalStateException> {
            VerifyingMyInterfaceMock.create {
                functionWithReturn("string") // missing `returns`
            }
        }
    }

    @Test
    fun builder_should_error_when_calling_returns_twice_in_a_row() {
        shouldThrow<IllegalStateException> {
            VerifyingMyInterfaceMock.create {
                val call = functionWithReturn("string")
                call returns "normal return"
                call returns "extra return"
            }
        }
    }

    @Test
    fun builder_should_error_when_calling_returns_on_the_wrong_call() {
        shouldThrow<IllegalStateException> {
            VerifyingMyInterfaceMock.create {
                val wrongCall = functionWithReturn("string")
                wrongCall returns "its own return value"

                functionWithReturn("expected call")
                wrongCall returns "normal return"
            }
        }
    }

    @Test
    fun builder_should_error_on_call_without_returns_followed_by_another_call() {
        shouldThrow<IllegalStateException> {
            VerifyingMyInterfaceMock.create {
                functionWithReturn("string")
                function(7)
            }
        }
    }

    @Test
    fun verifier_should_pass_when_calls_are_correct() {
        verifier.verify { myInterface ->
            myInterface.functionWithReturn("arg")
            myInterface.function(2)
        }
    }

    @Test
    fun verifier_should_return_the_block_return_value() {
        val returned = verifier.verify { myInterface ->
            myInterface.functionWithReturn("arg")
            myInterface.function(2)

            7
        }

        returned shouldBe 7
    }

    @Test
    fun verifier_should_return_correct_values_from_mocked_results() {
        var firstResult: String? = null

        verifier.verify { myInterface ->
            firstResult = myInterface.functionWithReturn("arg")
            myInterface.function(2)
        }

        firstResult.shouldBe("result")
    }

    @Test
    fun verifier_should_fail_when_calling_a_function_a_second_time() {
        verifier.verifyWithPassEarly { myInterface ->
            myInterface.functionWithReturn("arg")
            shouldThrow<AssertionError> {
                myInterface.functionWithReturn("arg")
            }
            throw PassEarly()
        }
    }

    @Test
    fun verifier_should_fail_when_first_call_has_incorrect_argument() {
        verifier.verifyWithPassEarly { myInterface ->
            shouldThrow<AssertionError> {
                myInterface.functionWithReturn("incorrect arg")
            }
            throw PassEarly()
        }
    }

    @Test
    fun verifier_should_fail_when_first_call_is_incorrect_function() {
        verifier.verifyWithPassEarly { myInterface ->
            shouldThrow<AssertionError> {
                myInterface.function(2)
            }
            throw PassEarly()
        }
    }

    @Test
    fun verifier_should_fail_when_second_call_has_incorrect_argument() {
        verifier.verifyWithPassEarly { myInterface ->
            myInterface.functionWithReturn("arg")
            shouldThrow<AssertionError> {
                myInterface.function(22222)
            }
            throw PassEarly()
        }
    }

    @Test
    fun verifier_should_fail_when_second_call_is_incorrect_function() {
        verifier.verifyWithPassEarly { myInterface ->
            myInterface.functionWithReturn("arg")
            shouldThrow<AssertionError> {
                myInterface.functionWithReturn("arg")
            }
            throw PassEarly()
        }
    }

    @Test
    fun verifier_should_fail_when_call_is_missing_from_end() {
        shouldThrow<AssertionError> {
            verifier.verify { myInterface ->
                myInterface.functionWithReturn("arg")
            }
        }
    }

    @Test
    fun verifier_should_fail_when_there_are_extra_calls_at_end() {
        verifier.verifyWithPassEarly { myInterface ->
            myInterface.functionWithReturn("arg")
            myInterface.function(2)

            shouldThrow<AssertionError> {
                myInterface.function(3)
            }
            throw PassEarly()
        }
    }
}
