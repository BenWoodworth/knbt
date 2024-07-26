package net.benwoodworth.knbt.test.verify

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.toNbtString
import net.benwoodworth.knbt.internal.toNbtTagType
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class VerifyingNbtReader(
    private val tag: NbtTag,
    private val knownSizes: Boolean = true,
) : NbtReader {
    private val stateHistory = mutableListOf<State>(State.InRoot)

    fun assertComplete() {
        val state = stateHistory.last()
        check(state is State.Complete) { "Expected reader calls to be complete, but is in state $state" }
    }

    override fun beginRootTag(): NbtReader.RootTagInfo = transitionState(::beginRootTag) {
        val state = stateHistory.last()
        check(state is State.InRoot)

        NbtReader.RootTagInfo(tag.type) to State.AwaitingValue(tag, State.Complete)
    }

    override fun beginCompound(): Unit = transitionState(::beginCompound) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtCompound)

        Unit to State.InCompound(state.tag, state.tag.content.entries.toList(), 0, false, state.nextState)
    }

    override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo = transitionState(::beginCompoundEntry) {
        val state = stateHistory.last()
        check(state is State.InCompound)
        check(!state.ended)

        val entry = state.entries.getOrNull(state.index)
        if (entry != null) {
            NbtReader.CompoundEntryInfo(entry.value.type, entry.key) to
                    State.AwaitingValue(entry.value, state.copy(index = state.index + 1))
        } else {
            NbtReader.CompoundEntryInfo.End to state.copy(ended = true)
        }
    }

    override fun endCompound(): Unit = transitionState(::endCompound) {
        val state = stateHistory.last()
        check(state is State.InCompound)
        check(state.ended)

        Unit to state.nextState
    }

    override fun beginList(): NbtReader.ListInfo = transitionState(::beginList) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtList<*>)

        if (knownSizes) {
            val endState = State.InListOrArray(state.tag, state.tag.content.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.content.foldRight(endState, State::AwaitingValue)

            NbtReader.ListInfo(state.tag.elementType, state.tag.size) to consecutiveAwaitValueStates
        } else {
            NbtReader.ListInfo(state.tag.elementType, NbtReader.UNKNOWN_SIZE) to
                    State.InListOrArray(state.tag, 0, false, state.nextState)
        }
    }

    override fun beginListEntry(): Boolean = transitionState(::beginListEntry) {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtList<*>)
        check(!knownSizes) { "beginListEntry() should not be called unless the list's size is unknown" }
        check(!state.ended)

        val entry = state.tag.getOrNull(state.index)
        if (entry != null) {
            true to State.AwaitingValue(entry, state.copy(index = state.index + 1))
        } else {
            false to state.copy(ended = true)
        }
    }

    override fun endList(): Unit = transitionState(::endList) {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtList<*>)
        check(state.ended)

        Unit to state.nextState
    }

    override fun beginByteArray(): NbtReader.ArrayInfo = transitionState(::beginByteArray) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtByteArray)

        if (knownSizes) {
            val endState = State.InListOrArray(state.tag, state.tag.content.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.content.map(::NbtByte).foldRight(endState, State::AwaitingValue)

            NbtReader.ArrayInfo(state.tag.size) to consecutiveAwaitValueStates
        } else {
            NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE) to State.InListOrArray(state.tag, 0, false, state.nextState)
        }
    }

    override fun beginByteArrayEntry(): Boolean = transitionState(::beginByteArrayEntry) {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtByteArray)
        check(!knownSizes) { "Should not be called unless the array's size is unknown" }
        check(!state.ended)

        val entry = state.tag.getOrNull(state.index)
        if (entry != null) {
            true to State.AwaitingValue(NbtByte(entry), state.copy(index = state.index + 1))
        } else {
            false to state.copy(ended = true)
        }
    }

    override fun endByteArray(): Unit = transitionState(::endByteArray) {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtByteArray)
        check(state.ended)

        Unit to state.nextState
    }

    override fun beginIntArray(): NbtReader.ArrayInfo = transitionState(::beginIntArray) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtIntArray)

        if (knownSizes) {
            val endState = State.InListOrArray(state.tag, state.tag.content.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.content.map(::NbtInt).foldRight(endState, State::AwaitingValue)

            NbtReader.ArrayInfo(state.tag.size) to consecutiveAwaitValueStates
        } else {
            NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE) to State.InListOrArray(state.tag, 0, false, state.nextState)
        }
    }

    override fun beginIntArrayEntry(): Boolean = transitionState(::beginIntArrayEntry) {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtIntArray)
        check(!knownSizes) { "Should not be called unless the array's size is unknown" }
        check(!state.ended)

        val entry = state.tag.getOrNull(state.index)
        if (entry != null) {
            true to State.AwaitingValue(NbtInt(entry), state.copy(index = state.index + 1))
        } else {
            false to state.copy(ended = true)
        }
    }

    override fun endIntArray(): Unit = transitionState(::endIntArray) {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtIntArray)
        check(state.ended)

        Unit to state.nextState
    }

    override fun beginLongArray(): NbtReader.ArrayInfo = transitionState(::beginLongArray) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtLongArray)

        if (knownSizes) {
            val endState = State.InListOrArray(state.tag, state.tag.content.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.content.map(::NbtLong).foldRight(endState, State::AwaitingValue)

            NbtReader.ArrayInfo(state.tag.size) to consecutiveAwaitValueStates
        } else {
            NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE) to State.InListOrArray(state.tag, 0, false, state.nextState)
        }
    }

    override fun beginLongArrayEntry(): Boolean = transitionState(::beginLongArrayEntry) {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtLongArray)
        check(!knownSizes) { "Should not be called unless the array's size is unknown" }
        check(!state.ended)

        val entry = state.tag.getOrNull(state.index)
        if (entry != null) {
            true to State.AwaitingValue(NbtLong(entry), state.copy(index = state.index + 1))
        } else {
            false to state.copy(ended = true)
        }
    }

    override fun endLongArray(): Unit = transitionState(::endLongArray) {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtLongArray)
        check(state.ended)

        Unit to state.nextState
    }

    override fun readByte(): Byte = transitionState(::readByte) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtByte)

        state.tag.value to state.nextState
    }

    override fun readShort(): Short = transitionState(::readShort) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtShort)

        state.tag.value to state.nextState
    }

    override fun readInt(): Int = transitionState(::readInt) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtInt)

        state.tag.value to state.nextState
    }

    override fun readLong(): Long = transitionState(::readLong) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtLong)

        state.tag.value to state.nextState
    }

    override fun readFloat(): Float = transitionState(::readFloat) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtFloat)

        state.tag.value to state.nextState
    }

    override fun readDouble(): Double = transitionState(::readDouble) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtDouble)

        state.tag.value to state.nextState
    }

    override fun readString(): String = transitionState(::readString) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtString)

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

        private fun Map.Entry<String, NbtTag>.toTagInfoString() =
            this.let { (name, tag) -> "${tag.type}(${name.toNbtString(forceQuote = true)})" }


        inline fun <reified TExpected : State> assertStateIs(state: State) {
            contract { returns() implies (state is TExpected) }

            val message = messagePrefix +
                    "Should only be called in state ${TExpected::class.simpleName}, " +
                    "but current state is ${state::class.simpleName}."

            assertIs<TExpected>(state, message)
        }

        fun assertWrittenRootTypeEquals(expected: NbtTagType, actual: NbtTagType) {
            assertEquals(expected, actual, messagePrefix + "Incorrect root type was written")
        }

        inline fun <reified T : NbtTag> assertWrittenTagTypeEquals(expected: NbtTag, actual: KClass<T>) {
            contract { returns() implies (expected is T) }

            assertEquals(expected.type, actual.toNbtTagType(), messagePrefix + "Incorrect type was written")
        }

        fun assertCompoundShouldBeginEntry(nextEntry: Map.Entry<String, NbtTag>?) {
            contract { returns() implies (nextEntry != null) }

            val message = messagePrefix +
                    "Should have ended the compound, but began a new compound entry instead: " +
                    "<${nextEntry?.toTagInfoString()}>."

            assertTrue(nextEntry != null, message)
        }

        fun assertWrittenCompoundEntryNameEquals(expected: String, actual: String) {
            assertEquals(expected, actual, messagePrefix + "Incorrect compound entry name was written")
        }

        fun assertWrittenCompoundEntryTypeEquals(expected: NbtTagType, actual: NbtTagType) {
            assertEquals(expected, actual, messagePrefix + "Incorrect compound entry type was written")
        }

        fun assertCompoundShouldBeEnded(nextEntry: Map.Entry<String, NbtTag>?) {
            val message = messagePrefix +
                    "Should have began a new compound entry, but the compound was ended instead. " +
                    "Expected: <${nextEntry?.toTagInfoString()}>."

            assertTrue(nextEntry == null, message)
        }

        fun assertWrittenElementTypeEquals(expected: NbtTagType, actual: NbtTagType) {
            assertEquals(expected, actual, messagePrefix + "Incorrect element type was written")
        }

        fun assertWrittenSizeEquals(expected: Int, actual: Int) {
            assertEquals(expected, actual, messagePrefix + "Incorrect size was written")
        }

        fun assertWrittenTagEquals(expected: NbtTag, actual: NbtTag) {
            assertEquals(expected, actual, messagePrefix + "Incorrect tag was written")
        }
    }
}
