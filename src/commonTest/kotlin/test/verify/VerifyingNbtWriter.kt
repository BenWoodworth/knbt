package net.benwoodworth.knbt.test.verify

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.NbtWriter
import kotlin.contracts.contract
import kotlin.reflect.KFunction


internal class VerifyingNbtWriter(
    private val tag: NbtTag,
) : NbtWriter {
    private val stateHistory = mutableListOf<State>(State.InRoot)

    fun assertComplete(): Unit =
        verifyAndGetNextState(::assertComplete) {
            val state = stateHistory.last()
            checkStateType<State.Complete>(state)

            state
        }

    override fun beginRootTag(type: NbtTagType): Unit =
        verifyAndGetNextState(::beginRootTag) {
            checkStateType<State.InRoot>(state)
            check(type == tag.type)

            State.AwaitingValue(tag, State.Complete)
        }

    override fun beginCompound(): Unit =
        verifyAndGetNextState(::beginCompound) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenType<NbtCompound>(state.tag)

            State.InCompound(state.tag, state.tag.content.entries.toList(), 0, state.nextState)
        }

    override fun beginCompoundEntry(type: NbtTagType, name: String): Unit =
        verifyAndGetNextState(::beginCompoundEntry) {
            checkStateType<State.InCompound>(state)

            val entry = state.entries.getOrNull(state.index)
            check(entry != null)
            check(type == entry.value.type)
            check(name == entry.key)

            State.AwaitingValue(entry.value, state.copy(index = state.index + 1))
        }

    override fun endCompound(): Unit =
        verifyAndGetNextState(::endCompound) {
            checkStateType<State.InCompound>(state)
            check(state.index == state.entries.lastIndex + 1)

            state.nextState
        }

    override fun beginList(type: NbtTagType, size: Int): Unit =
        verifyAndGetNextState(::beginList) {
            checkStateType<State.AwaitingValue>(state)
            check(state.tag is NbtList<*>)
            check(state.tag.elementType == type)
            check(size == state.tag.size)

            State.InListOrArray(state.tag, 0, state.nextState)
        }

    override fun beginListEntry(): Unit =
        verifyAndGetNextState(::beginListEntry) {
            checkStateType<State.InListOrArray>(state)
            check(state.tag is NbtList<*>)

            State.AwaitingValue(state.tag[state.index], state.copy(index = state.index + 1))
        }

    override fun endList(): Unit =
        verifyAndGetNextState(::endList) {
            checkStateType<State.InListOrArray>(state)
            check(state.tag is NbtList<*>)

            state.nextState
        }

    override fun beginByteArray(size: Int): Unit =
        verifyAndGetNextState(::beginByteArray) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenType<NbtByteArray>(state.tag)
            check(size == state.tag.size)

            State.InListOrArray(state.tag, 0, state.nextState)
        }

    override fun beginByteArrayEntry(): Unit =
        verifyAndGetNextState(::beginByteArrayEntry) {
            checkStateType<State.InListOrArray>(state)
            checkWrittenType<NbtByteArray>(state.tag)

            State.AwaitingValue(NbtByte(state.tag[state.index]), state.copy(index = state.index + 1))
        }

    override fun endByteArray(): Unit =
        verifyAndGetNextState(::endByteArray) {
            checkStateType<State.InListOrArray>(state)
            checkWrittenType<NbtByteArray>(state.tag)

            state.nextState
        }

    override fun beginIntArray(size: Int): Unit =
        verifyAndGetNextState(::beginIntArray) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenType<NbtIntArray>(state.tag)
            check(size == state.tag.size)

            State.InListOrArray(state.tag, 0, state.nextState)
        }

    override fun beginIntArrayEntry(): Unit =
        verifyAndGetNextState(::beginIntArrayEntry) {
            checkStateType<State.InListOrArray>(state)
            checkWrittenType<NbtIntArray>(state.tag)

            State.AwaitingValue(NbtInt(state.tag[state.index]), state.copy(index = state.index + 1))
        }

    override fun endIntArray(): Unit =
        verifyAndGetNextState(::endIntArray) {
            checkStateType<State.InListOrArray>(state)
            checkWrittenType<NbtIntArray>(state.tag)

            state.nextState
        }

    override fun beginLongArray(size: Int): Unit =
        verifyAndGetNextState(::beginLongArray) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenType<NbtLongArray>(state.tag)
            check(size == state.tag.size)

            State.InListOrArray(state.tag, 0, state.nextState)
        }

    override fun beginLongArrayEntry(): Unit =
        verifyAndGetNextState(::beginLongArrayEntry) {
            checkStateType<State.InListOrArray>(state)
            checkWrittenType<NbtLongArray>(state.tag)

            State.AwaitingValue(NbtLong(state.tag[state.index]), state.copy(index = state.index + 1))
        }

    override fun endLongArray(): Unit =
        verifyAndGetNextState(::endLongArray) {
            checkStateType<State.InListOrArray>(state)
            checkWrittenType<NbtLongArray>(state.tag)

            state.nextState
        }

    override fun writeByte(value: Byte): Unit =
        verifyAndGetNextState(::writeByte) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenValue(state.tag, NbtByte(value))

            state.nextState
        }

    override fun writeShort(value: Short): Unit =
        verifyAndGetNextState(::writeShort) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenValue(state.tag, NbtShort(value))

            state.nextState
        }

    override fun writeInt(value: Int): Unit =
        verifyAndGetNextState(::writeInt) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenValue(state.tag, NbtInt(value))

            state.nextState
        }

    override fun writeLong(value: Long): Unit =
        verifyAndGetNextState(::writeLong) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenValue(state.tag, NbtLong(value))

            state.nextState
        }

    override fun writeFloat(value: Float): Unit =
        verifyAndGetNextState(::writeFloat) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenValue(state.tag, NbtFloat(value))

            state.nextState
        }

    override fun writeDouble(value: Double): Unit =
        verifyAndGetNextState(::writeDouble) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenValue(state.tag, NbtDouble(value))

            state.nextState
        }

    override fun writeString(value: String): Unit =
        verifyAndGetNextState(::writeString) {
            checkStateType<State.AwaitingValue>(state)
            checkWrittenValue(state.tag, NbtString(value))

            state.nextState
        }

    private sealed interface State {
        data object Complete : State

        data class AwaitingValue(
            val tag: NbtTag,
            val nextState: State,
        ) : State

        data object InRoot : State

        data class InCompound(
            val tag: NbtCompound,
            val entries: List<Map.Entry<String, NbtTag>>,
            val index: Int,
            val nextState: State,
        ) : State

        data class InListOrArray(
            val tag: NbtTag,
            val index: Int,
            val nextState: State,
        ) : State
    }

    private fun verifyAndGetNextState(
        function: KFunction<*>,
        block: VerifyScope.() -> State
    ) {
//        contract { // TODO Needed?
//            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//        }

        val currentState = stateHistory.last()
        val nextState = VerifyScope(function, currentState).block()
        stateHistory += nextState
    }

    private class VerifyScope(
        function: KFunction<*>,
        val state: State
    ) {
        private val checkMessagePrefix = "${function.name}(): "

        inline fun <reified TExpected : State> checkStateType(state: State) {
            contract { returns() implies (state is TExpected) }

            check(state is TExpected) {
                "$checkMessagePrefix Should only be called in state ${TExpected::class.simpleName}, " +
                        "but current state is ${state::class.simpleName}"
            }
        }

        inline fun <reified TWritten : NbtTag> checkWrittenType(stateTag: NbtTag) {
            contract { returns() implies (stateTag is TWritten) }

            check(stateTag is TWritten) {
                "$checkMessagePrefix Expected ${stateTag::class.simpleName} to be written, " +
                        "but was ${TWritten::class.simpleName}"
            }
        }

        inline fun <reified TWritten : NbtTag> checkWrittenValue(expected: NbtTag, actual: TWritten) {
//            contract { returns() implies (expected is TWritten) } // TODO Needed?

//            checkWrittenType<TWritten>(expected)
            check(actual == expected) {
                "$checkMessagePrefix Expected $expected to be written, but was $actual"
            }
        }
    }
}
