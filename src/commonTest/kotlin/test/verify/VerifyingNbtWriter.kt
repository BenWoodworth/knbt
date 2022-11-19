package net.benwoodworth.knbt.test.verify

import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.internal.NbtWriter

/**
 * Delegates to [writer] and verifies that the all the calls are correct.
 */
internal class VerifyingNbtWriter(
    private val writer: NbtWriter
) : NbtWriter {
    private val stateHistory = mutableListOf<Pair<State, Call>>()
    private var state: State = State.InRoot

    fun assertComplete(): Unit =
        check(state is State.Complete) { "Expected writer calls to be complete, but is in state $state" }

    private inline fun call(call: Call, delegate: NbtWriter.() -> Unit) {
        stateHistory += state to call

        val newState = state.transition(call)
        state = newState

        writer.delegate()
    }

    //region NbtWriter calls, delegating to `call()`
    override fun beginRootTag(type: NbtTagType): Unit =
        call(Call.BeginRootTag(type)) { beginRootTag(type) }

    override fun beginCompound(): Unit =
        call(Call.BeginCompound) { beginCompound() }

    override fun beginCompoundEntry(type: NbtTagType, name: String): Unit =
        call(Call.BeginCompoundEntry(type, name)) { beginCompoundEntry(type, name) }

    override fun endCompound(): Unit =
        call(Call.EndCompound) { endCompound() }

    override fun beginList(type: NbtTagType, size: Int): Unit =
        call(Call.BeginList(type, size)) { beginList(type, size) }

    override fun beginListEntry(): Unit =
        call(Call.BeginListEntry) { beginListEntry() }

    override fun endList(): Unit =
        call(Call.EndList) { endList() }

    override fun beginByteArray(size: Int): Unit =
        call(Call.BeginByteArray(size)) { beginByteArray(size) }

    override fun beginByteArrayEntry(): Unit =
        call(Call.BeginByteArrayEntry) { beginByteArrayEntry() }

    override fun endByteArray(): Unit =
        call(Call.EndByteArray) { endByteArray() }

    override fun beginIntArray(size: Int): Unit =
        call(Call.BeginIntArray(size)) { beginIntArray(size) }

    override fun beginIntArrayEntry(): Unit =
        call(Call.BeginIntArrayEntry) { beginIntArrayEntry() }

    override fun endIntArray(): Unit =
        call(Call.EndIntArray) { endIntArray() }

    override fun beginLongArray(size: Int): Unit =
        call(Call.BeginLongArray(size)) { beginLongArray(size) }

    override fun beginLongArrayEntry(): Unit =
        call(Call.BeginLongArrayEntry) { beginLongArrayEntry() }

    override fun endLongArray(): Unit =
        call(Call.EndLongArray) { endLongArray() }

    override fun writeByte(value: Byte): Unit =
        call(Call.WriteByte(value)) { writeByte(value) }

    override fun writeShort(value: Short): Unit =
        call(Call.WriteShort(value)) { writeShort(value) }

    override fun writeInt(value: Int): Unit =
        call(Call.WriteInt(value)) { writeInt(value) }

    override fun writeLong(value: Long): Unit =
        call(Call.WriteLong(value)) { writeLong(value) }

    override fun writeFloat(value: Float): Unit =
        call(Call.WriteFloat(value)) { writeFloat(value) }

    override fun writeDouble(value: Double): Unit =
        call(Call.WriteDouble(value)) { writeDouble(value) }

    override fun writeString(value: String): Unit =
        call(Call.WriteString(value)) { writeString(value) }
    //endregion

    private fun State.transition(call: Call): State = when (this) {
        is State.Complete -> error("Expected no more calls, but got $call")

        is State.AwaitingValue -> when {
            call is Call.BeginCompound && type == TAG_Compound -> State.InCompound(nextState)

            call is Call.BeginList && type == TAG_List -> when {
                call.type == TAG_End && call.size > 0 -> error("Expected ${Call.BeginList::class.simpleName} with $TAG_End to have size 0, but got $call")
                call.size >= 0 -> State.InList(call.type, call.size, 0, nextState)
                else -> error("Expected ${Call.BeginList::class.simpleName} with non-negative size, but got $call")
            }

            call is Call.BeginByteArray && type == TAG_Byte_Array -> when {
                call.size >= 0 -> State.InByteArray(call.size, 0, nextState)
                else -> error("Expected ${Call.BeginByteArray::class.simpleName} with non-negative size, but got $call")
            }

            call is Call.BeginIntArray && type == TAG_Int_Array -> when {
                call.size >= 0 -> State.InIntArray(call.size, 0, nextState)
                else -> error("Expected ${Call.BeginIntArray::class.simpleName} with non-negative size, but got $call")
            }

            call is Call.BeginLongArray && type == TAG_Long_Array -> when {
                call.size >= 0 -> State.InLongArray(call.size, 0, nextState)
                else -> error("Expected ${Call.BeginLongArray::class.simpleName} with non-negative size, but got $call")
            }

            call is Call.WriteByte && type == TAG_Byte -> nextState
            call is Call.WriteShort && type == TAG_Short -> nextState
            call is Call.WriteInt && type == TAG_Int -> nextState
            call is Call.WriteLong && type == TAG_Long -> nextState
            call is Call.WriteFloat && type == TAG_Float -> nextState
            call is Call.WriteDouble && type == TAG_Double -> nextState
            call is Call.WriteString && type == TAG_String -> nextState

            else -> error("Expected value call for $type, but got $this")
        }

        is State.InRoot -> when (call) {
            is Call.BeginRootTag -> when {
                call.type != TAG_End -> State.AwaitingValue(call.type, State.Complete)
                else -> error("${NbtWriter::beginRootTag.name} must not be called with $TAG_End")

            }

            else -> error("Expected ${Call.BeginRootTag::class.simpleName} for first call, but got $call")
        }

        is State.InCompound -> when (call) {
            is Call.EndCompound -> nextState

            is Call.BeginCompoundEntry -> when (call.type) {
                TAG_End -> error("Expected ${Call.BeginCompoundEntry::class.simpleName} with type other than $TAG_End, but got $call")
                else -> State.AwaitingValue(call.type, this)
            }

            else -> error("Expected ${Call.EndCompound::class.simpleName} or ${Call.BeginCompoundEntry::class.simpleName}, but got $call")
        }

        is State.InList -> when {
            count < size -> when (call) {
                Call.BeginListEntry -> State.AwaitingValue(entryType, copy(count = count + 1))
                else -> error("Expected ${Call.BeginListEntry::class.simpleName}, but got $call")
            }

            else -> when (call) {
                Call.EndList -> nextState
                else -> error("Expected ${Call.EndList::class.simpleName}, but got $call")
            }
        }

        is State.InByteArray -> when {
            count < size -> when (call) {
                is Call.BeginByteArrayEntry -> State.AwaitingValue(TAG_Byte, copy(count = count + 1))
                else -> error("Expected ${Call.BeginByteArrayEntry::class.simpleName}, but got $call")
            }

            else -> when (call) {
                Call.EndByteArray -> nextState
                else -> error("Expected ${Call.EndByteArray::class.simpleName}, but got $call")
            }
        }

        is State.InIntArray -> when {
            count < size -> when (call) {
                is Call.BeginIntArrayEntry -> State.AwaitingValue(TAG_Int, copy(count = count + 1))
                else -> error("Expected ${Call.BeginIntArrayEntry::class.simpleName}, but got $call")
            }

            else -> when (call) {
                Call.EndIntArray -> nextState
                else -> error("Expected ${Call.EndIntArray::class.simpleName}, but got $call")
            }
        }

        is State.InLongArray -> when {
            count < size -> when (call) {
                is Call.BeginLongArrayEntry -> State.AwaitingValue(TAG_Long, copy(count = count + 1))
                else -> error("Expected ${Call.BeginLongArrayEntry::class.simpleName}, but got $call")
            }

            else -> when (call) {
                Call.EndLongArray -> nextState
                else -> error("Expected ${Call.EndLongArray::class.simpleName}, but got $call")
            }
        }
    }

    private sealed interface State {
        data object Complete : State

        data class AwaitingValue(val type: NbtTagType, val nextState: State) : State

        data object InRoot : State
        data class InCompound(val nextState: State) : State
        data class InList(val entryType: NbtTagType, val size: Int, val count: Int, val nextState: State) : State
        data class InByteArray(val size: Int, val count: Int, val nextState: State) : State
        data class InIntArray(val size: Int, val count: Int, val nextState: State) : State
        data class InLongArray(val size: Int, val count: Int, val nextState: State) : State
    }

    private sealed interface Call {
        data class BeginRootTag(val type: NbtTagType) : Call

        data object BeginCompound : Call
        data class BeginCompoundEntry(val type: NbtTagType, val name: String) : Call
        data object EndCompound : Call

        data class BeginList(val type: NbtTagType, val size: Int) : Call
        data object BeginListEntry : Call
        data object EndList : Call

        data class BeginByteArray(val size: Int) : Call
        data object BeginByteArrayEntry : Call
        data object EndByteArray : Call

        data class BeginIntArray(val size: Int) : Call
        data object BeginIntArrayEntry : Call
        data object EndIntArray : Call

        data class BeginLongArray(val size: Int) : Call
        data object BeginLongArrayEntry : Call
        data object EndLongArray : Call

        data class WriteByte(val value: Byte) : Call
        data class WriteShort(val value: Short) : Call
        data class WriteInt(val value: Int) : Call
        data class WriteLong(val value: Long) : Call
        data class WriteFloat(val value: Float) : Call
        data class WriteDouble(val value: Double) : Call
        data class WriteString(val value: String) : Call
    }
}
