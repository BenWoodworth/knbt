package net.benwoodworth.knbt.test.mocks

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

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

class VerifyingMockFactoryTest {
    //region Builder tests
    @Test
    fun builder_call_with_missing_return_at_the_end_should_error() {
        assertFailsWith<IllegalStateException> {
            VerifyingMyInterfaceMock.create {
                functionWithReturn("string") // missing `returns`
            }
        }
    }

    @Test
    fun builder_call_with_repeated_return_should_error() {
        assertFailsWith<IllegalStateException> {
            VerifyingMyInterfaceMock.create {
                val call = functionWithReturn("string")
                call returns "normal return"
                call returns "extra return"
            }
        }
    }

    @Test
    fun builder_call_with_return_from_another_call_should_error() {
        assertFailsWith<IllegalStateException> {
            VerifyingMyInterfaceMock.create {
                val wrongCall = functionWithReturn("string")
                wrongCall returns "its own return value"

                functionWithReturn("expected call")
                wrongCall returns "normal return"
            }
        }
    }

    @Test
    fun builder_with_call_that_is_missing_return_before_another_call_should_error() {
        assertFailsWith<IllegalStateException> {
            VerifyingMyInterfaceMock.create {
                functionWithReturn("string")
                function(7)
            }
        }
    }
    //endregion

    //region Verifier tests
    private val verifier = VerifyingMyInterfaceMock.create {
        functionWithReturn("arg") returns "result"
        function(2)
    }

    @Test
    fun verify_with_correct_calls_should_pass() {
        verifier.verify { myInterface ->
            myInterface.functionWithReturn("arg")
            myInterface.function(2)
        }
    }

    @Test
    fun verify_with_correct_calls_should_return_what_the_block_returns() {
        val returned = verifier.verify { myInterface ->
            myInterface.functionWithReturn("arg")
            myInterface.function(2)

            7
        }

        assertEquals(7, returned)
    }

    @Test
    fun verify_calls_that_are_correct_return_correct_values() {
        var firstResult: String? = null

        verifier.verify { myInterface ->
            firstResult = myInterface.functionWithReturn("arg")
            myInterface.function(2)
        }

        assertEquals("result", firstResult)
    }

    @Test
    fun verify_with_incorrectly_repeated_call_should_fail() {
        var executedPastBadCall = false

        assertFailsWith<AssertionError> {
            verifier.verify { myInterface ->
                myInterface.functionWithReturn("arg")

                myInterface.functionWithReturn("arg")
                executedPastBadCall = true
            }
        }

        assertFalse(executedPastBadCall, "Executed past bad call")
    }

    @Test
    fun verify_with_incorrect_args_in_first_call_should_fail() {
        var executedPastBadCall = false

        assertFailsWith<AssertionError> {
            verifier.verify { myInterface ->
                myInterface.functionWithReturn("incorrect arg")
                executedPastBadCall = true
            }
        }

        assertFalse(executedPastBadCall, "Executed past bad call")
    }

    @Test
    fun verify_with_incorrect_function_for_first_call_should_fail() {
        var executedPastBadCall = false

        assertFailsWith<AssertionError> {
            verifier.verify { myInterface ->
                myInterface.function(2)
                executedPastBadCall = true
            }
        }

        assertFalse(executedPastBadCall, "Executed past bad call")
    }

    @Test
    fun verify_with_incorrect_arg_for_second_call_should_fail() {
        var executedPastBadCall = false

        assertFailsWith<AssertionError> {
            verifier.verify { myInterface ->
                myInterface.functionWithReturn("arg")
                myInterface.function(22222)
                executedPastBadCall = true
            }
        }

        assertFalse(executedPastBadCall, "Executed past bad call")
    }

    @Test
    fun verify_with_incorrect_function_for_second_call_should_fail() {
        var executedPastBadCall = false

        assertFailsWith<AssertionError> {
            verifier.verify { myInterface ->
                myInterface.functionWithReturn("arg")
                myInterface.functionWithReturn("arg")
                executedPastBadCall = true
            }
        }

        assertFalse(executedPastBadCall, "Executed past bad call")
    }

    @Test
    fun verify_with_call_is_missing_from_end_should_fail() {
        assertFailsWith<AssertionError> {
            verifier.verify { myInterface ->
                myInterface.functionWithReturn("arg")
            }
        }
    }

    @Test
    fun verify_with_extra_calls_at_end_should_fail() {
        var executedPastBadCall = false

        assertFailsWith<AssertionError> {
            verifier.verify { myInterface ->
                myInterface.functionWithReturn("arg")
                myInterface.function(2)

                myInterface.function(3)
                executedPastBadCall = true
            }
        }

        assertFalse(executedPastBadCall, "Executed past bad call")
    }
    //endregion
}
