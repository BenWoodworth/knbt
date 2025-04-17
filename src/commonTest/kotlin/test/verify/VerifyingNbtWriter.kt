package net.benwoodworth.knbt.test.verify

import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtType
import net.benwoodworth.knbt.internal.NbtWriter
import net.benwoodworth.knbt.internal.toNbtString
import net.benwoodworth.knbt.toNbtType
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class VerifyingNbtWriter(
    private val tag: NbtNamed<NbtTag>,
) : NbtWriter {
    private val stateHistory = mutableListOf<State>(State.InRoot)

    fun assertComplete(): Unit = transitionState(::assertComplete) {
        assertStateIs<State.Complete>(state)

        Unit to state
    }

    override fun beginRootTag(type: NbtType, name: String): Unit = transitionState(::beginRootTag) {
        assertStateIs<State.InRoot>(state)
        assertWrittenRootTypeEquals(tag.value.type, type)
        assertWrittenRootNameEquals(tag.name, name)

        Unit to State.AwaitingValue(tag.value, State.Complete)
    }

    override fun beginCompound(): Unit = transitionState(::beginCompound) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtCompound::class)

        Unit to State.InCompound(state.tag, state.tag.content.entries.toList(), 0, state.nextState)
    }

    override fun beginCompoundEntry(type: NbtType, name: String): Unit = transitionState(::beginCompoundEntry) {
        assertStateIs<State.InCompound>(state)

        val entry = state.entries.getOrNull(state.index)
        assertCompoundShouldBeginEntry(entry)
        assertWrittenCompoundEntryNameEquals(entry.key, name)
        assertWrittenCompoundEntryTypeEquals(entry.value.type, type)

        Unit to State.AwaitingValue(entry.value, state.copy(index = state.index + 1))
    }

    override fun endCompound(): Unit = transitionState(::endCompound) {
        assertStateIs<State.InCompound>(state)

        val entry = state.entries.getOrNull(state.index)
        assertCompoundShouldBeEnded(entry)

        Unit to state.nextState
    }

    override fun beginList(type: NbtType, size: Int): Unit = transitionState(::beginList) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtList::class)
        assertWrittenElementTypeEquals(state.tag.elementType, type)
        assertWrittenSizeEquals(state.tag.size, size)

        Unit to State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginListEntry(): Unit = transitionState(::beginListEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtList::class)

        Unit to State.AwaitingValue(state.tag[state.index], state.copy(index = state.index + 1))
    }

    override fun endList(): Unit = transitionState(::endList) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtList::class)

        Unit to state.nextState
    }

    override fun beginByteArray(size: Int): Unit = transitionState(::beginByteArray) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtByteArray::class)
        assertWrittenSizeEquals(size, state.tag.size)

        Unit to State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginByteArrayEntry(): Unit = transitionState(::beginByteArrayEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtByteArray::class)

        Unit to State.AwaitingValue(NbtByte(state.tag[state.index]), state.copy(index = state.index + 1))
    }

    override fun endByteArray(): Unit = transitionState(::endByteArray) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtByteArray::class)

        Unit to state.nextState
    }

    override fun beginIntArray(size: Int): Unit = transitionState(::beginIntArray) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtIntArray::class)
        assertWrittenSizeEquals(size, state.tag.size)

        Unit to State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginIntArrayEntry(): Unit = transitionState(::beginIntArrayEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtIntArray::class)

        Unit to State.AwaitingValue(NbtInt(state.tag[state.index]), state.copy(index = state.index + 1))
    }

    override fun endIntArray(): Unit = transitionState(::endIntArray) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtIntArray::class)

        Unit to state.nextState
    }

    override fun beginLongArray(size: Int): Unit = transitionState(::beginLongArray) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagTypeEquals(state.tag, NbtLongArray::class)
        assertWrittenSizeEquals(size, state.tag.size)

        Unit to State.InListOrArray(state.tag, 0, state.nextState)
    }

    override fun beginLongArrayEntry(): Unit = transitionState(::beginLongArrayEntry) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtLongArray::class)

        Unit to State.AwaitingValue(NbtLong(state.tag[state.index]), state.copy(index = state.index + 1))
    }

    override fun endLongArray(): Unit = transitionState(::endLongArray) {
        assertStateIs<State.InListOrArray>(state)
        assertWrittenTagTypeEquals(state.tag, NbtLongArray::class)

        Unit to state.nextState
    }

    override fun writeByte(value: Byte): Unit = transitionState(::writeByte) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtByte(value))

        Unit to state.nextState
    }

    override fun writeShort(value: Short): Unit = transitionState(::writeShort) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtShort(value))

        Unit to state.nextState
    }

    override fun writeInt(value: Int): Unit = transitionState(::writeInt) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtInt(value))

        Unit to state.nextState
    }

    override fun writeLong(value: Long): Unit = transitionState(::writeLong) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtLong(value))

        Unit to state.nextState
    }

    override fun writeFloat(value: Float): Unit = transitionState(::writeFloat) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtFloat(value))

        Unit to state.nextState
    }

    override fun writeDouble(value: Double): Unit = transitionState(::writeDouble) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtDouble(value))

        Unit to state.nextState
    }

    override fun writeString(value: String): Unit = transitionState(::writeString) {
        assertStateIs<State.AwaitingValue>(state)
        assertWrittenTagEquals(state.tag, NbtString(value))

        Unit to state.nextState
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

            assertTrue(TExpected::class == state::class, message)
        }

        fun assertWrittenRootTypeEquals(expected: NbtType, actual: NbtType) {
            assertEquals(expected, actual, messagePrefix + "Incorrect root type was written")
        }

        fun assertWrittenRootNameEquals(expected: String, actual: String) {
            assertEquals(expected, actual, messagePrefix + "Incorrect root name was written")
        }

        inline fun <reified T : NbtTag> assertWrittenTagTypeEquals(expected: NbtTag, actual: KClass<T>) {
            contract { returns() implies (expected is T) }

            assertEquals(expected.type, actual.toNbtType(), messagePrefix + "Incorrect type was written")
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

        fun assertWrittenCompoundEntryTypeEquals(expected: NbtType, actual: NbtType) {
            assertEquals(expected, actual, messagePrefix + "Incorrect compound entry type was written")
        }

        fun assertCompoundShouldBeEnded(nextEntry: Map.Entry<String, NbtTag>?) {
            val message = messagePrefix +
                    "Should have began a new compound entry, but the compound was ended instead. " +
                    "Expected: <${nextEntry?.toTagInfoString()}>."

            assertTrue(nextEntry == null, message)
        }

        fun assertWrittenElementTypeEquals(expected: NbtType, actual: NbtType) {
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
