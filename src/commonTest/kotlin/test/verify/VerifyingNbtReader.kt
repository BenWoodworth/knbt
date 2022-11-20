package net.benwoodworth.knbt.test.verify

import net.benwoodworth.knbt.internal.NbtReader
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.internal.NbtWriter

/**
 * Delegates to [reader] and verifies that the all the calls are correct.
 */
internal class VerifyingNbtReader(
    private val reader: NbtReader,
    private val knownSizes: Boolean = true,
) : NbtReader {
    private val stateHistory = mutableListOf<Pair<State, Call>>()
    private var state: State = State.InRoot

    fun assertComplete(): Unit =
        check(state is State.Complete) { "Expected reader calls to be complete, but is in state $state" }

    private fun NbtReader.ListInfo.withInterceptedSize(): NbtReader.ListInfo =
        if (knownSizes) this else this.copy(size = NbtReader.UNKNOWN_SIZE)

    private fun NbtReader.ArrayInfo.withInterceptedSize(): NbtReader.ArrayInfo =
        if (knownSizes) this else NbtReader.ArrayInfo(NbtReader.UNKNOWN_SIZE)

    private inline fun <R> call(
        delegate: NbtReader.() -> R,
        call: (result: R) -> Call,
    ): R {
        val result = reader.delegate()
        val resultCall = call(result)

        stateHistory += state to resultCall

        val newState = state.transition(resultCall)
        state = newState

        return result
    }

    //region NbtReader calls, delegating to `call()`
    override fun beginRootTag(): NbtReader.RootTagInfo = call(
        delegate = { beginRootTag() },
        call = { Call.BeginRootTag(it) },
    )

    override fun beginCompound(): Unit = call(
        delegate = { beginCompound() },
        call = { Call.BeginCompound }
    )

    override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo = call(
        delegate = { beginCompoundEntry() },
        call = { Call.BeginCompoundEntry(it) }
    )

    override fun endCompound(): Unit = call(
        delegate = { endCompound() },
        call = { Call.EndCompound }
    )

    override fun beginList(): NbtReader.ListInfo = call(
        delegate = { beginList().withInterceptedSize() },
        call = { Call.BeginList(it) }
    )

    override fun beginListEntry(): Boolean = call(
        delegate = { beginListEntry() },
        call = { Call.BeginListEntry(it) }
    )

    override fun endList(): Unit = call(
        delegate = { endList() },
        call = { Call.EndList }
    )

    override fun beginByteArray(): NbtReader.ArrayInfo = call(
        delegate = { beginByteArray().withInterceptedSize() },
        call = { Call.BeginByteArray(it) }
    )

    override fun beginByteArrayEntry(): Boolean = call(
        delegate = { beginByteArrayEntry() },
        call = { Call.BeginByteArrayEntry(it) }
    )

    override fun endByteArray(): Unit = call(
        delegate = { endByteArray() },
        call = { Call.EndByteArray }
    )

    override fun beginIntArray(): NbtReader.ArrayInfo = call(
        delegate = { beginIntArray().withInterceptedSize() },
        call = { Call.BeginIntArray(it) }
    )

    override fun beginIntArrayEntry(): Boolean = call(
        delegate = { beginIntArrayEntry() },
        call = { Call.BeginIntArrayEntry(it) }
    )

    override fun endIntArray(): Unit = call(
        delegate = { endIntArray() },
        call = { Call.EndIntArray }
    )

    override fun beginLongArray(): NbtReader.ArrayInfo = call(
        delegate = { beginLongArray().withInterceptedSize() },
        call = { Call.BeginLongArray(it) }
    )

    override fun beginLongArrayEntry(): Boolean = call(
        delegate = { beginLongArrayEntry() },
        call = { Call.BeginLongArrayEntry(it) }
    )

    override fun endLongArray(): Unit = call(
        delegate = { endLongArray() },
        call = { Call.EndLongArray }
    )

    override fun readByte(): Byte = call(
        delegate = { readByte() },
        call = { Call.ReadByte(it) }
    )

    override fun readShort(): Short = call(
        delegate = { readShort() },
        call = { Call.ReadShort(it) }
    )

    override fun readInt(): Int = call(
        delegate = { readInt() },
        call = { Call.ReadInt(it) }
    )

    override fun readLong(): Long = call(
        delegate = { readLong() },
        call = { Call.ReadLong(it) }
    )

    override fun readFloat(): Float = call(
        delegate = { readFloat() },
        call = { Call.ReadFloat(it) }
    )

    override fun readDouble(): Double = call(
        delegate = { readDouble() },
        call = { Call.ReadDouble(it) }
    )

    override fun readString(): String = call(
        delegate = { readString() },
        call = { Call.ReadString(it) }
    )
    //endregion

    private fun State.transition(call: Call): State = when (this) {
        is State.Complete -> error("Expected no more calls, but got $call")

        is State.AwaitingValue -> when {
            call is Call.BeginCompound && type == TAG_Compound -> State.InCompound(false, nextState)

            call is Call.BeginList && type == TAG_List -> when {
                call.result.size == NbtReader.UNKNOWN_SIZE -> State.InList(call.result.type, null, 0, nextState)
                call.result.type == TAG_End && call.result.size > 0 -> error("Expected ${Call.BeginList::class.simpleName} with $TAG_End to have size 0, but got $call")
                call.result.size >= 0 -> State.InList(call.result.type, call.result.size, 0, nextState)
                else -> error("Expected ${Call.BeginList::class.simpleName} with non-negative size, but got $call")
            }

            call is Call.BeginByteArray && type == TAG_Byte_Array -> when {
                call.result.size == NbtReader.UNKNOWN_SIZE -> State.InByteArray(null, 0, nextState)
                call.result.size >= 0 -> State.InByteArray(call.result.size, 0, nextState)
                else -> error("Expected ${Call.BeginByteArray::class.simpleName} with non-negative size, but got $call")
            }

            call is Call.BeginIntArray && type == TAG_Int_Array -> when {
                call.result.size == NbtReader.UNKNOWN_SIZE -> State.InIntArray(null, 0, nextState)
                call.result.size >= 0 -> State.InIntArray(call.result.size, 0, nextState)
                else -> error("Expected ${Call.BeginIntArray::class.simpleName} with non-negative size, but got $call")
            }

            call is Call.BeginLongArray && type == TAG_Long_Array -> when {
                call.result.size == NbtReader.UNKNOWN_SIZE -> State.InLongArray(null, 0, nextState)
                call.result.size >= 0 -> State.InLongArray(call.result.size, 0, nextState)
                else -> error("Expected ${Call.BeginLongArray::class.simpleName} with non-negative size, but got $call")
            }

            call is Call.ReadByte && type == TAG_Byte -> nextState
            call is Call.ReadShort && type == TAG_Short -> nextState
            call is Call.ReadInt && type == TAG_Int -> nextState
            call is Call.ReadLong && type == TAG_Long -> nextState
            call is Call.ReadFloat && type == TAG_Float -> nextState
            call is Call.ReadDouble && type == TAG_Double -> nextState
            call is Call.ReadString && type == TAG_String -> nextState

            else -> error("Expected value call for $type, but got $this")
        }

        is State.InRoot -> when (call) {
            is Call.BeginRootTag -> when {
                call.result.type != TAG_End -> State.AwaitingValue(call.result.type, State.Complete)
                else -> error("${NbtWriter::beginRootTag.name} must not be called with $TAG_End")
            }

            else -> error("Expected ${Call.BeginRootTag::class.simpleName} for first call, but got $call")
        }

        is State.InCompound -> when {
            ended -> when (call) {
                is Call.EndCompound -> nextState
                else -> error("Expected ${Call.BeginCompoundEntry::class.simpleName}, but got $call")
            }

            else -> when (call) {
                is Call.BeginCompoundEntry -> when {
                    call.result.type == TAG_End && call.result.name == "" -> State.InCompound(true, nextState)
                    call.result.type == TAG_End -> error("Expected ${Call.BeginCompoundEntry::class.simpleName} with $TAG_End to have empty name, but got $call")
                    else -> State.AwaitingValue(call.result.type, this)
                }

                else -> error("Expected ${Call.BeginCompoundEntry::class.simpleName}, but got $call")
            }
        }

        is State.InList -> when {
            // Size is unknown, so begin entry call is required
            size == null -> when (call) {
                is Call.BeginListEntry -> when {
                    call.result -> State.AwaitingValue(entryType, copy(count = count + 1))
                    else -> copy(size = count)
                }

                else -> error("Expected ${Call.BeginListEntry::class.simpleName}, but got $call")
            }

            // Size is known, so begin entry call is not needed, and is read directly
            count < size -> State.AwaitingValue(entryType, copy(count = count + 1)).transition(call)

            else -> when (call) {
                Call.EndList -> nextState
                else -> error("Expected ${Call.EndList::class.simpleName}, but got $call")
            }
        }

        is State.InByteArray -> when {
            // Size is unknown, so begin entry call is required
            size == null -> when (call) {
                is Call.BeginByteArrayEntry -> when {
                    call.result -> State.AwaitingValue(TAG_Byte, copy(count = count + 1))
                    else -> copy(size = count)
                }

                else -> error("Expected ${Call.BeginByteArrayEntry::class.simpleName}, but got $call")
            }

            // Size is known, so begin entry call is not needed, and is read directly
            count < size -> when (call) {
                is Call.ReadByte -> copy(count = count + 1)
                else -> error("Expected ${Call.ReadByte::class.simpleName}, but got $call")
            }

            else -> when (call) {
                Call.EndByteArray -> nextState
                else -> error("Expected ${Call.EndByteArray::class.simpleName}, but got $call")
            }
        }

        is State.InIntArray -> when {
            // Size is unknown, so begin entry call is required
            size == null -> when (call) {
                is Call.BeginIntArrayEntry -> when {
                    call.result -> State.AwaitingValue(TAG_Int, copy(count = count + 1))
                    else -> copy(size = count)
                }

                else -> error("Expected ${Call.BeginIntArrayEntry::class.simpleName}, but got $call")
            }

            // Size is known, so begin entry call is not needed, and is read directly
            count < size -> when (call) {
                is Call.ReadInt -> copy(count = count + 1)
                else -> error("Expected ${Call.ReadInt::class.simpleName}, but got $call")
            }

            else -> when (call) {
                Call.EndIntArray -> nextState
                else -> error("Expected ${Call.EndIntArray::class.simpleName}, but got $call")
            }
        }

        is State.InLongArray -> when {
            // Size is unknown, so begin entry call is required
            size == null -> when (call) {
                is Call.BeginLongArrayEntry -> when {
                    call.result -> State.AwaitingValue(TAG_Long, copy(count = count + 1))
                    else -> copy(size = count)
                }

                else -> error("Expected ${Call.BeginLongArrayEntry::class.simpleName}, but got $call")
            }

            // Size is known, so begin entry call is not needed, and is read directly
            count < size -> when (call) {
                is Call.ReadLong -> copy(count = count + 1)
                else -> error("Expected ${Call.ReadLong::class.simpleName}, but got $call")
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
        data class InCompound(val ended: Boolean, val nextState: State) : State
        data class InList(val entryType: NbtTagType, val size: Int?, val count: Int, val nextState: State) : State
        data class InByteArray(val size: Int?, val count: Int, val nextState: State) : State
        data class InIntArray(val size: Int?, val count: Int, val nextState: State) : State
        data class InLongArray(val size: Int?, val count: Int, val nextState: State) : State
    }

    private sealed interface Call {
        data class BeginRootTag(val result: NbtReader.RootTagInfo) : Call

        data object BeginCompound : Call
        data class BeginCompoundEntry(val result: NbtReader.CompoundEntryInfo) : Call
        data object EndCompound : Call

        data class BeginList(val result: NbtReader.ListInfo) : Call
        data class BeginListEntry(val result: Boolean) : Call
        data object EndList : Call

        data class BeginByteArray(val result: NbtReader.ArrayInfo) : Call
        data class BeginByteArrayEntry(val result: Boolean) : Call
        data object EndByteArray : Call

        data class BeginIntArray(val result: NbtReader.ArrayInfo) : Call
        data class BeginIntArrayEntry(val result: Boolean) : Call
        data object EndIntArray : Call

        data class BeginLongArray(val result: NbtReader.ArrayInfo) : Call
        data class BeginLongArrayEntry(val result: Boolean) : Call
        data object EndLongArray : Call

        data class ReadByte(val result: Byte) : Call
        data class ReadShort(val result: Short) : Call
        data class ReadInt(val result: Int) : Call
        data class ReadLong(val result: Long) : Call
        data class ReadFloat(val result: Float) : Call
        data class ReadDouble(val result: Double) : Call
        data class ReadString(val result: String) : Call
    }
}
