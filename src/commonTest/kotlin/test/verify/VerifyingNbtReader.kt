package net.benwoodworth.knbt.test.verify

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtCapabilities
import net.benwoodworth.knbt.internal.NbtReader
import net.benwoodworth.knbt.internal.NbtType
import net.benwoodworth.knbt.internal.NbtType.TAG_End
import net.benwoodworth.knbt.toNbtType
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class VerifyingNbtReader(
    private val tag: NbtNamed<NbtTag>?,
    private val capabilities: NbtCapabilities,
) : NbtReader {
    private val stateHistory = mutableListOf<State>(State.InRoot)

    fun assertComplete() = transitionState(::beginRootTag) {
        assertStateIs<State.Complete>(state)

        Unit to state
    }

    override fun beginRootTag(): NbtReader.NamedTagInfo = transitionState(::beginRootTag) {
        assertStateIs<State.InRoot>(state)

        if (tag == null) {
            NbtReader.NamedTagInfo(TAG_End, "") to State.Complete
        } else {
            NbtReader.NamedTagInfo(tag.value.type, tag.name) to State.AwaitingValue(tag.value, State.Complete)
        }
    }

    override fun beginCompound(): Unit = transitionState(::beginCompound) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtCompound::class)

        Unit to State.InCompound(state.tag, state.tag.content.entries.toList(), 0, false, state.nextState)
    }

    override fun beginCompoundEntry(): NbtReader.NamedTagInfo = transitionState(::beginCompoundEntry) {
        assertStateIs<State.InCompound>(state)
        assertBeginningEntryWithAnotherEntryToRead(state.ended)

        val entry = state.entries.getOrNull(state.index)
        if (entry != null) {
            val awaitEntryState = State.AwaitingValue(entry.value, state.copy(index = state.index + 1))

            NbtReader.NamedTagInfo(entry.value.type, entry.key) to awaitEntryState
        } else {
            NbtReader.NamedTagInfo.End to state.copy(ended = true)
        }
    }

    override fun endCompound(): Unit = transitionState(::endCompound) {
        assertStateIs<State.InCompound>(state)
        assertEndingWithNoMoreEntries(state.ended)

        Unit to state.nextState
    }

    override fun beginList(): NbtReader.ListInfo = transitionState(::beginList) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtList::class)

        if (capabilities.definiteLengthEncoding) {
            val endState = State.InListOrArray(state.tag, state.tag.content.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.content.foldRight(endState, State::AwaitingValue)

            NbtReader.ListInfo(state.tag.elementType, state.tag.size) to consecutiveAwaitValueStates
        } else {
            val inListState = State.InListOrArray(state.tag, 0, false, state.nextState)

            NbtReader.ListInfo(state.tag.elementType, NbtReader.UNKNOWN_SIZE) to inListState
        }
    }

    override fun beginListEntry(): Boolean = transitionState(::beginListEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertReadTagTypeEquals(state.tag, NbtList::class)
        assertBeginningEntryWithUnknownSizes(capabilities.definiteLengthEncoding)
        assertBeginningEntryWithAnotherEntryToRead(state.ended)

        val entry = state.tag.getOrNull(state.index)
        if (entry != null) {
            val awaitEntryState = State.AwaitingValue(entry, state.copy(index = state.index + 1))

            true to awaitEntryState
        } else {
            false to state.copy(ended = true)
        }
    }

    override fun endList(): Unit = transitionState(::endList) {
        assertStateIs<State.InListOrArray>(state)
        assertReadTagTypeEquals(state.tag, NbtList::class)
        assertEndingWithNoMoreEntries(state.ended)

        Unit to state.nextState
    }

    override fun beginByteArray(): NbtReader.ArrayInfo = transitionState(::beginByteArray) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtByteArray::class)

        if (capabilities.definiteLengthEncoding) {
            val endState = State.InListOrArray(state.tag, state.tag.content.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.content.map(::NbtByte).foldRight(endState, State::AwaitingValue)

            NbtReader.ArrayInfo(state.tag.size) to consecutiveAwaitValueStates
        } else {
            val inByteArrayState = State.InListOrArray(state.tag, 0, false, state.nextState)

            NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE) to inByteArrayState
        }
    }

    override fun beginByteArrayEntry(): Boolean = transitionState(::beginByteArrayEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertReadTagTypeEquals(state.tag, NbtByteArray::class)
        assertBeginningEntryWithUnknownSizes(capabilities.definiteLengthEncoding)
        assertBeginningEntryWithAnotherEntryToRead(state.ended)

        val entry = state.tag.getOrNull(state.index)
        if (entry != null) {
            val awaitEntryState = State.AwaitingValue(NbtByte(entry), state.copy(index = state.index + 1))

            true to awaitEntryState
        } else {
            false to state.copy(ended = true)
        }
    }

    override fun endByteArray(): Unit = transitionState(::endByteArray) {
        assertStateIs<State.InListOrArray>(state)
        assertReadTagTypeEquals(state.tag, NbtByteArray::class)
        assertEndingWithNoMoreEntries(state.ended)

        Unit to state.nextState
    }

    override fun beginIntArray(): NbtReader.ArrayInfo = transitionState(::beginIntArray) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtIntArray::class)

        if (capabilities.definiteLengthEncoding) {
            val endState = State.InListOrArray(state.tag, state.tag.content.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.content.map(::NbtInt).foldRight(endState, State::AwaitingValue)

            NbtReader.ArrayInfo(state.tag.size) to consecutiveAwaitValueStates
        } else {
            val inIntArrayState = State.InListOrArray(state.tag, 0, false, state.nextState)

            NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE) to inIntArrayState
        }
    }

    override fun beginIntArrayEntry(): Boolean = transitionState(::beginIntArrayEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertReadTagTypeEquals(state.tag, NbtIntArray::class)
        assertBeginningEntryWithUnknownSizes(capabilities.definiteLengthEncoding)
        assertBeginningEntryWithAnotherEntryToRead(state.ended)

        val entry = state.tag.getOrNull(state.index)
        if (entry != null) {
            true to State.AwaitingValue(NbtInt(entry), state.copy(index = state.index + 1))
        } else {
            false to state.copy(ended = true)
        }
    }

    override fun endIntArray(): Unit = transitionState(::endIntArray) {
        assertStateIs<State.InListOrArray>(state)
        assertReadTagTypeEquals(state.tag, NbtIntArray::class)
        assertEndingWithNoMoreEntries(state.ended)

        Unit to state.nextState
    }

    override fun beginLongArray(): NbtReader.ArrayInfo = transitionState(::beginLongArray) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtLongArray::class)

        if (capabilities.definiteLengthEncoding) {
            val endState = State.InListOrArray(state.tag, state.tag.content.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.content.map(::NbtLong).foldRight(endState, State::AwaitingValue)

            NbtReader.ArrayInfo(state.tag.size) to consecutiveAwaitValueStates
        } else {
            val inLongArrayState = State.InListOrArray(state.tag, 0, false, state.nextState)

            NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE) to inLongArrayState
        }
    }

    override fun beginLongArrayEntry(): Boolean = transitionState(::beginLongArrayEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertReadTagTypeEquals(state.tag, NbtLongArray::class)
        assertBeginningEntryWithUnknownSizes(capabilities.definiteLengthEncoding)
        assertBeginningEntryWithAnotherEntryToRead(state.ended)

        val entry = state.tag.getOrNull(state.index)
        if (entry != null) {
            true to State.AwaitingValue(NbtLong(entry), state.copy(index = state.index + 1))
        } else {
            false to state.copy(ended = true)
        }
    }

    override fun endLongArray(): Unit = transitionState(::endLongArray) {
        assertStateIs<State.InListOrArray>(state)
        assertReadTagTypeEquals(state.tag, NbtLongArray::class)
        assertEndingWithNoMoreEntries(state.ended)

        Unit to state.nextState
    }

    override fun readByte(): Byte = transitionState(::readByte) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtByte::class)

        state.tag.value to state.nextState
    }

    override fun readShort(): Short = transitionState(::readShort) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtShort::class)

        state.tag.value to state.nextState
    }

    override fun readInt(): Int = transitionState(::readInt) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtInt::class)

        state.tag.value to state.nextState
    }

    override fun readLong(): Long = transitionState(::readLong) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtLong::class)

        state.tag.value to state.nextState
    }

    override fun readFloat(): Float = transitionState(::readFloat) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtFloat::class)

        state.tag.value to state.nextState
    }

    override fun readDouble(): Double = transitionState(::readDouble) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtDouble::class)

        state.tag.value to state.nextState
    }

    override fun readString(): String = transitionState(::readString) {
        assertStateIs<State.AwaitingValue>(state)
        assertReadTagTypeEquals(state.tag, NbtString::class)

        state.tag.value to state.nextState
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
            val ended: Boolean,
            val nextState: State,
        ) : State

        data class InListOrArray(
            val tag: NbtTag,
            val index: Int,
            val ended: Boolean,
            val nextState: State,
        ) : State
    }

    private fun State.getPath(): String = when (this) {
        is State.AwaitingValue -> nextState.getPath()
        State.Complete -> "[Complete]"
        State.InRoot -> "$"
        is State.InCompound -> "${nextState.getPath()}.${entries[index].key}"
        is State.InListOrArray -> "${nextState.getPath()}.[$index]}"
    }


    // KT-49904: May be possible to simplify in the future with decorators
    private fun <R> transitionState(
        function: KFunction<R>,
        transition: VerifyScope.() -> Pair<R, State>
    ): R {
        val currentState = stateHistory.last()
        val (returnValue, nextState) = VerifyScope(function, currentState).transition()

        if (nextState != currentState) {
            stateHistory += nextState
        }

        return returnValue
    }

    private class VerifyScope(
        function: KFunction<*>,
        val state: State
    ) {
        private val messagePrefix = "${function.name}(): "

        inline fun <reified TExpected : State> assertStateIs(state: State) {
            contract { returns() implies (state is TExpected) }

            val message = messagePrefix +
                    "Should only be called in state ${TExpected::class.simpleName}, " +
                    "but current state is ${state::class.simpleName}."

            assertTrue(TExpected::class == state::class, message)
        }

        fun assertBeginningEntryWithUnknownSizes(knownSizes: Boolean) {
            assertFalse(knownSizes, messagePrefix + "Should not explicitly begin entries unless the size is unknown.")
        }

        fun assertBeginningEntryWithAnotherEntryToRead(ended: Boolean) {
            assertFalse(ended, messagePrefix + "Should not begin entry when there are no more to read.")
        }

        fun assertEndingWithNoMoreEntries(ended: Boolean) {
            assertTrue(ended, messagePrefix + "Should not be ended when there are more entries to read.")
        }

        inline fun <reified T : NbtTag> assertReadTagTypeEquals(expected: NbtTag, actual: KClass<T>) {
            contract { returns() implies (expected is T) }

            assertEquals(expected.type, actual.toNbtType(), messagePrefix + "Incorrect type was read")
        }
    }
}
