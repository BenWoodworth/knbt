package net.benwoodworth.knbt.test.verify

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtReader

internal class VerifyingNbtReader(
    private val tag: NbtTag,
    private val knownSizes: Boolean = true,
) : NbtReader {
    private val stateHistory = mutableListOf<State>(State.InRoot)

    fun assertComplete() {
        val state = stateHistory.last()
        check(state is State.Complete) { "Expected reader calls to be complete, but is in state $state" }
    }

    override fun beginRootTag(): NbtReader.RootTagInfo {
        val state = stateHistory.last()
        check(state is State.InRoot)

        stateHistory += State.AwaitingValue(tag, State.Complete)
        return NbtReader.RootTagInfo(tag.type)
    }

    override fun beginCompound() {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtCompound)

        stateHistory += State.InCompound(state.tag, state.tag.entries.toList(), 0, false, state.nextState)
    }

    override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo {
        val state = stateHistory.last()
        check(state is State.InCompound)
        check(!state.ended)

        val entry = state.entries.getOrNull(state.index)
        return if (entry != null) {
            stateHistory += State.AwaitingValue(entry.value, state.copy(index = state.index + 1))
            NbtReader.CompoundEntryInfo(entry.value.type, entry.key)
        } else {
            stateHistory += state.copy(ended = true)
            return NbtReader.CompoundEntryInfo.End
        }
    }

    override fun endCompound() {
        val state = stateHistory.last()
        check(state is State.InCompound)
        check(state.ended)

        stateHistory += state.nextState
    }

    override fun beginList(): NbtReader.ListInfo {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtList<*>)

        return if (knownSizes) {
            val endState = State.InListOrArray(state.tag, state.tag.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.foldRight(endState, State::AwaitingValue)
            stateHistory += consecutiveAwaitValueStates
            NbtReader.ListInfo(state.tag.elementType, state.tag.size)
        } else {
            stateHistory += State.InListOrArray(state.tag, 0, false, state.nextState)
            NbtReader.ListInfo(state.tag.elementType, NbtReader.UNKNOWN_SIZE)
        }
    }

    override fun beginListEntry(): Boolean {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtList<*>)
        check(!state.ended)
        check(!knownSizes)

        val entry = state.tag.getOrNull(state.index)
        return if (entry != null) {
            stateHistory += State.AwaitingValue(entry, state.copy(index = state.index + 1))
            true
        } else {
            stateHistory += state.copy(ended = true)
            false
        }
    }

    override fun endList() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtList<*>)
        check(state.ended)

        stateHistory += state.nextState
    }

    override fun beginByteArray(): NbtReader.ArrayInfo {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtByteArray)

        return if (knownSizes) {
            val endState = State.InListOrArray(state.tag, state.tag.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.map(::NbtByte).foldRight(endState, State::AwaitingValue)
            stateHistory += consecutiveAwaitValueStates
            NbtReader.ArrayInfo(state.tag.size)
        } else {
            stateHistory += State.InListOrArray(state.tag, 0, false, state.nextState)
            NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE)
        }
    }

    override fun beginByteArrayEntry(): Boolean {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtByteArray)
        check(!state.ended)
        check(!knownSizes)

        val entry = state.tag.getOrNull(state.index)
        return if (entry != null) {
            stateHistory += State.AwaitingValue(NbtByte(entry), state.copy(index = state.index + 1))
            true
        } else {
            stateHistory += state.copy(ended = true)
            false
        }
    }

    override fun endByteArray() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtByteArray)
        check(state.ended)

        stateHistory += state.nextState
    }

    override fun beginIntArray(): NbtReader.ArrayInfo {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtIntArray)

        return if (knownSizes) {
            val endState = State.InListOrArray(state.tag, state.tag.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.map(::NbtInt).foldRight(endState, State::AwaitingValue)
            stateHistory += consecutiveAwaitValueStates
            NbtReader.ArrayInfo(state.tag.size)
        } else {
            stateHistory += State.InListOrArray(state.tag, 0, false, state.nextState)
            NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE)
        }
    }

    override fun beginIntArrayEntry(): Boolean {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtIntArray)
        check(!state.ended)
        check(!knownSizes)

        val entry = state.tag.getOrNull(state.index)
        return if (entry != null) {
            stateHistory += State.AwaitingValue(NbtInt(entry), state.copy(index = state.index + 1))
            true
        } else {
            stateHistory += state.copy(ended = true)
            false
        }
    }

    override fun endIntArray() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtIntArray)
        check(state.ended)

        stateHistory += state.nextState
    }

    override fun beginLongArray(): NbtReader.ArrayInfo {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtLongArray)

        return if (knownSizes) {
            val endState = State.InListOrArray(state.tag, state.tag.lastIndex + 1, true, state.nextState)
            val consecutiveAwaitValueStates = state.tag.map(::NbtLong).foldRight(endState, State::AwaitingValue)
            stateHistory += consecutiveAwaitValueStates
            NbtReader.ArrayInfo(state.tag.size)
        } else {
            stateHistory += State.InListOrArray(state.tag, 0, false, state.nextState)
            NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE)
        }
    }

    override fun beginLongArrayEntry(): Boolean {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtLongArray)
        check(!state.ended)
        check(!knownSizes)

        val entry = state.tag.getOrNull(state.index)
        return if (entry != null) {
            stateHistory += State.AwaitingValue(NbtLong(entry), state.copy(index = state.index + 1))
            true
        } else {
            stateHistory += state.copy(ended = true)
            false
        }
    }

    override fun endLongArray() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtLongArray)
        check(state.ended)

        stateHistory += state.nextState
    }

    override fun readByte(): Byte {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtByte)

        stateHistory += state.nextState
        return state.tag.value
    }

    override fun readShort(): Short {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtShort)

        stateHistory += state.nextState
        return state.tag.value
    }

    override fun readInt(): Int {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtInt)

        stateHistory += state.nextState
        return state.tag.value
    }

    override fun readLong(): Long {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtLong)

        stateHistory += state.nextState
        return state.tag.value
    }

    override fun readFloat(): Float {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtFloat)

        stateHistory += state.nextState
        return state.tag.value
    }

    override fun readDouble(): Double {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtDouble)

        stateHistory += state.nextState
        return state.tag.value
    }

    override fun readString(): String {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtString)

        stateHistory += state.nextState
        return state.tag.value
    }

    private sealed interface State {
        object Complete : State {
            override fun toString(): String =
                this::class.simpleName!!
        }

        data class AwaitingValue(
            val tag: NbtTag,
            val nextState: State,
        ) : State

        object InRoot : State {
            override fun toString(): String =
                this::class.simpleName!!
        }

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
}
