package net.benwoodworth.knbt.test.verify

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.NbtWriter
import net.benwoodworth.knbt.internal.toNbtString
import net.benwoodworth.knbt.internal.toNbtTagType
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

internal class VerifyingNbtWriter(
    private val tag: NbtTag,
) : NbtWriter {
    private val stateHistory = mutableListOf<State>(State.InRoot)

    fun assertComplete(): Unit = transitionState(::assertComplete) {
        assertStateIs<State.Complete>(state)

        state
    }

    override fun beginRootTag(type: NbtTagType): Unit = transitionState(::beginRootTag) {
        assertStateIs<State.InRoot>(state)
        assertWrittenRootTypeEquals(tag.type, type)

        State.AwaitingValue(tag, State.Complete)
    }

    override fun beginCompound(): Unit = transitionState(::beginCompound) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtCompound::class)

        State.InCompound(state.tag, state.tag.content.entries.toList(), 0, state.nextState)
    }

    override fun beginCompoundEntry(type: NbtTagType, name: String): Unit = transitionState(::beginCompoundEntry) {
        assertStateIs<State.InCompound>(state)

        val entry = state.entries.getOrNull(state.index)
        assertCompoundHasNextEntry(entry)
        assertWrittenCompoundEntryInfoEquals(entry, type to name)

        State.AwaitingValue(entry.value, state.copy(index = state.index + 1))
    }

    override fun endCompound(): Unit = transitionState(::endCompound) {
        assertStateIs<State.InCompound>(state)

        val entry = state.entries.getOrNull(state.index)
        assertNextCompoundEntryIsNull(entry)

        state.nextState
    }

    override fun beginList(type: NbtTagType, size: Int): Unit = transitionState(::beginList) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtList::class)
        assertWrittenElementTypeEquals(state.tag.elementType, type)
        assertWrittenSizeEquals(state.tag.size, size)

        State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginListEntry(): Unit = transitionState(::beginListEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtList::class)

        State.AwaitingValue(state.tag[state.index], state.copy(index = state.index + 1))
    }

    override fun endList(): Unit = transitionState(::endList) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtList::class)

        state.nextState
    }

    override fun beginByteArray(size: Int): Unit = transitionState(::beginByteArray) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtByteArray::class)
        assertWrittenSizeEquals(size, state.tag.size)

        State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginByteArrayEntry(): Unit = transitionState(::beginByteArrayEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtByteArray::class)

        State.AwaitingValue(NbtByte(state.tag[state.index]), state.copy(index = state.index + 1))
    }

    override fun endByteArray(): Unit = transitionState(::endByteArray) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtByteArray::class)

        state.nextState
    }

    override fun beginIntArray(size: Int): Unit = transitionState(::beginIntArray) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtIntArray::class)
        assertWrittenSizeEquals(size, state.tag.size)

        State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginIntArrayEntry(): Unit = transitionState(::beginIntArrayEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtIntArray::class)

        State.AwaitingValue(NbtInt(state.tag[state.index]), state.copy(index = state.index + 1))
    }

    override fun endIntArray(): Unit = transitionState(::endIntArray) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtIntArray::class)

        state.nextState
    }

    override fun beginLongArray(size: Int): Unit = transitionState(::beginLongArray) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtLongArray::class)
        assertWrittenSizeEquals(size, state.tag.size)

        State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginLongArrayEntry(): Unit = transitionState(::beginLongArrayEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtLongArray::class)

        State.AwaitingValue(NbtLong(state.tag[state.index]), state.copy(index = state.index + 1))
    }

    override fun endLongArray(): Unit = transitionState(::endLongArray) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtLongArray::class)

        state.nextState
    }

    override fun writeByte(value: Byte): Unit = transitionState(::writeByte) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtByte(value))

        state.nextState
    }

    override fun writeShort(value: Short): Unit = transitionState(::writeShort) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtShort(value))

        state.nextState
    }

    override fun writeInt(value: Int): Unit = transitionState(::writeInt) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtInt(value))

        state.nextState
    }

    override fun writeLong(value: Long): Unit = transitionState(::writeLong) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtLong(value))

        state.nextState
    }

    override fun writeFloat(value: Float): Unit = transitionState(::writeFloat) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtFloat(value))

        state.nextState
    }

    override fun writeDouble(value: Double): Unit = transitionState(::writeDouble) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtDouble(value))

        state.nextState
    }

    override fun writeString(value: String): Unit = transitionState(::writeString) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtString(value))

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

    // KT-49904: May be possible to simplify in the future with decorators
    private fun transitionState(
        function: KFunction<*>,
        transition: VerifyScope.() -> State
    ) {
        val currentState = stateHistory.last()
        val nextState = VerifyScope(function, currentState).transition()

        if (nextState != currentState) {
            stateHistory += nextState
        }
    }

    private class VerifyScope(
        function: KFunction<*>,
        val state: State
    ) {
        private val messagePrefix = "${function.name}(): "

        private data class CompoundEntryInfo(val type: NbtTagType, val name: String) {
            override fun toString() = "$type(${name.toNbtString(forceQuote = true)})"
        }

        private fun Map.Entry<String, NbtTag>.toEntryInfo() =
            this.let { (name, tag) -> CompoundEntryInfo(tag.type, name) }

        private fun Pair<NbtTagType, String>.toEntryInfo() =
            this.let { (type, name) -> CompoundEntryInfo(type, name) }


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

        fun assertCompoundHasNextEntry(expected: Map.Entry<String, NbtTag>?) {
            contract { returns() implies (expected != null) }

            val message = messagePrefix +
                    "Expected compound to be ended, but began new compound entry: <${expected?.toEntryInfo()}>."

            assertTrue(expected != null, message)
        }

        fun assertWrittenCompoundEntryInfoEquals(
            expected: Map.Entry<String, NbtTag>,
            actual: Pair<NbtTagType, String>
        ) {
            val message = messagePrefix + "Incorrect compound entry info was written"

            assertEquals(expected.toEntryInfo(), actual.toEntryInfo(), message)
        }

        fun assertNextCompoundEntryIsNull(expected: Map.Entry<String, NbtTag>?) {
            val message = messagePrefix +
                    "Expected to begin new compound entry, but compound was ended. " +
                    "Expected: <${expected?.toEntryInfo()}>."

            assertTrue(expected == null, message)
        }

        fun assertWrittenTagEquals(expected: NbtTag, actual: NbtTag) {
            assertEquals(expected, actual, messagePrefix + "Incorrect tag was written")
        }

        fun assertWrittenElementTypeEquals(expected: NbtTagType, actual: NbtTagType) {
            assertEquals(expected, actual, messagePrefix + "Incorrect element type was written")
        }

        fun assertWrittenSizeEquals(expected: Int, actual: Int) {
            assertEquals(expected, actual, messagePrefix + "Incorrect size was written")
        }
    }
}
