package net.benwoodworth.knbt.test.verify

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.NbtWriter
import kotlin.contracts.contract

internal class VerifyingNbtWriter(
    private val tag: NbtTag,
) : NbtWriter {
    private val stateHistory = mutableListOf<State>(State.InRoot)

    fun assertComplete() {
        val state = stateHistory.last()
        check(state is State.Complete)
    }

    override fun beginRootTag(type: NbtTagType) {
        val state = stateHistory.last()
        check(state is State.InRoot)
        check(type == tag.type)

        stateHistory += State.AwaitingValue(tag, State.Complete)
    }

    override fun beginCompound() {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtCompound)

        stateHistory += State.InCompound(state.tag, state.tag.entries.toList(), 0, state.nextState)
    }

    override fun beginCompoundEntry(type: NbtTagType, name: String) {
        val state = stateHistory.last()
        check(state is State.InCompound)

        val entry = state.entries.getOrNull(state.index)
        check(entry != null)
        check(type == entry.value.type)
        check(name == entry.key)

        stateHistory += State.AwaitingValue(entry.value, state.copy(index = state.index + 1))
    }

    override fun endCompound() {
        val state = stateHistory.last()
        check(state is State.InCompound)
        check(state.index == state.entries.lastIndex + 1)

        stateHistory += state.nextState
    }

    override fun beginList(type: NbtTagType, size: Int) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtList<*>)
        check(state.tag.elementType == type)
        check(size >= 0)

        stateHistory += State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginListEntry() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtList<*>)

        stateHistory += State.AwaitingValue(state.tag[state.index], state.copy(index = state.index + 1))
    }

    override fun endList() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtList<*>)

        stateHistory += state.nextState
    }

    override fun beginByteArray(size: Int) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtByteArray)
        check(size >= 0)

        stateHistory += State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginByteArrayEntry() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtByteArray)

        stateHistory += State.AwaitingValue(NbtByte(state.tag[state.index]), state.copy(index = state.index + 1))
    }

    override fun endByteArray() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtByteArray)

        stateHistory += state.nextState
    }

    override fun beginIntArray(size: Int) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtIntArray)
        check(size >= 0)

        stateHistory += State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginIntArrayEntry() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtIntArray)

        stateHistory += State.AwaitingValue(NbtInt(state.tag[state.index]), state.copy(index = state.index + 1))
    }

    override fun endIntArray() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtIntArray)

        stateHistory += state.nextState
    }

    override fun beginLongArray(size: Int) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtLongArray)
        check(size >= 0)

        stateHistory += State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginLongArrayEntry() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtLongArray)

        stateHistory += State.AwaitingValue(NbtLong(state.tag[state.index]), state.copy(index = state.index + 1))
    }

    override fun endLongArray() {
        val state = stateHistory.last()
        check(state is State.InListOrArray)
        check(state.tag is NbtLongArray)

        stateHistory += state.nextState
    }

    override fun writeByte(value: Byte) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtByte)
        check(value == state.tag.value)

        stateHistory += state.nextState
    }

    override fun writeShort(value: Short) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtShort)
        check(value == state.tag.value)

        stateHistory += state.nextState
    }

    override fun writeInt(value: Int) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtInt)
        check(value == state.tag.value)

        stateHistory += state.nextState
    }

    override fun writeLong(value: Long) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtLong)
        check(value == state.tag.value)

        stateHistory += state.nextState
    }

    override fun writeFloat(value: Float) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtFloat)
        check(value.toRawBits() == state.tag.value.toRawBits())

        stateHistory += state.nextState
    }

    override fun writeDouble(value: Double) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtDouble)
        check(value.toRawBits() == state.tag.value.toRawBits())

        stateHistory += state.nextState
    }

    override fun writeString(value: String) {
        val state = stateHistory.last()
        check(state is State.AwaitingValue)
        check(state.tag is NbtString)
        check(value == state.tag.value)

        stateHistory += state.nextState
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
            val nextState: State,
        ) : State

        data class InListOrArray(
            val tag: NbtTag,
            val index: Int,
            val nextState: State,
        ) : State
    }
}
