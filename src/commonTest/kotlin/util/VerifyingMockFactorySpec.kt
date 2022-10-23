package net.benwoodworth.knbt.util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe

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

class VerifyingMockFactorySpec : FlatSpec({
    class PassEarly : Throwable()

    fun <T> VerifyingMockFactory.Verifier<T>.verifyWithPassEarly(block: (callee: T) -> Unit) {
        try {
            verify(block)
        } catch (_: PassEarly) {
        }
    }

    context("builder") {
        test("should error on call without `returns` at the end") {
            shouldThrow<IllegalStateException> {
                VerifyingMyInterfaceMock.create {
                    functionWithReturn("string")
                }
            }
        }

        test("should error when calling `returns` twice in a row") {
            shouldThrow<IllegalStateException> {
                VerifyingMyInterfaceMock.create {
                    val call = functionWithReturn("string")
                    call returns "normal return"
                    call returns "extra return"
                }
            }
        }

        test("should error when calling `returns` on the wrong call") {
            shouldThrow<IllegalStateException> {
                VerifyingMyInterfaceMock.create {
                    val wrongCall = functionWithReturn("string")
                    wrongCall returns "its own return value"

                    functionWithReturn("expected call")
                    wrongCall returns "normal return"
                }
            }
        }

        test("should error on call without `returns` followed by another call") {
            shouldThrow<IllegalStateException> {
                VerifyingMyInterfaceMock.create {
                    functionWithReturn("string")
                    function(7)
                }
            }
        }
    }

    context("verifier") {
        val verifier = VerifyingMyInterfaceMock.create {
            functionWithReturn("arg") returns "result"
            function(2)
        }

        test("should pass when calls are correct") {
            verifier.verify { myInterface ->
                myInterface.functionWithReturn("arg")
                myInterface.function(2)
            }
        }

        test("should return the block return value") {
            val returned = verifier.verify { myInterface ->
                myInterface.functionWithReturn("arg")
                myInterface.function(2)

                7
            }

            returned shouldBe 7
        }

        test("should return correct values from mocked results") {
            var firstResult: String? = null

            verifier.verify { myInterface ->
                firstResult = myInterface.functionWithReturn("arg")
                myInterface.function(2)
            }

            firstResult.shouldBe("result")
        }

        test("should fail when calling a function a second time") {
            verifier.verifyWithPassEarly { myInterface ->
                myInterface.functionWithReturn("arg")
                shouldThrow<AssertionError> {
                    myInterface.functionWithReturn("arg")
                }
                throw PassEarly()
            }
        }

        test("should fail when first call has incorrect argument") {
            verifier.verifyWithPassEarly { myInterface ->
                shouldThrow<AssertionError> {
                    myInterface.functionWithReturn("incorrect arg")
                }
                throw PassEarly()
            }
        }

        test("should fail when first call is incorrect function") {
            verifier.verifyWithPassEarly { myInterface ->
                shouldThrow<AssertionError> {
                    myInterface.function(2)
                }
                throw PassEarly()
            }
        }

        test("should fail when second call has incorrect argument") {
            verifier.verifyWithPassEarly { myInterface ->
                myInterface.functionWithReturn("arg")
                shouldThrow<AssertionError> {
                    myInterface.function(22222)
                }
                throw PassEarly()
            }
        }

        test("should fail when second call is incorrect function") {
            verifier.verifyWithPassEarly { myInterface ->
                myInterface.functionWithReturn("arg")
                shouldThrow<AssertionError> {
                    myInterface.functionWithReturn("arg")
                }
                throw PassEarly()
            }
        }

        test("should fail when call is missing from end") {
            shouldThrow<AssertionError> {
                verifier.verify { myInterface ->
                    myInterface.functionWithReturn("arg")
                }
            }
        }

        test("should fail when there are extra calls at end") {
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
})
