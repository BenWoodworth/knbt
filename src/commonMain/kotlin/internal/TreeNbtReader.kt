package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.NbtReader.*
import net.benwoodworth.knbt.tag.*

internal class TreeNbtReader(private val tag: NbtTag) : NbtReader {
    private val iteratorStack = ArrayDeque<Iterator<Any>>()

    private var nextTag: NbtTag? = tag
    private lateinit var nextTagName: String

    private fun iterate() {
        val next = iteratorStack.lastOrNull()
            ?.let { if (it.hasNext()) it.next() else null }

        nextTag = when (next) {
            null -> null
            is Map.Entry<*, *> -> { // NbtCompound
                nextTagName = next.key as String
                next.value as NbtTag
            }
            is NbtTag -> next // NbtList
            is Byte -> next.toNbtByte() // NbtByteArray
            is Int -> next.toNbtInt() // NbtIntArray
            is Long -> next.toNbtLong() // NbtLongArray
            else -> error("Unhandled value: $next")
        }
    }

    override fun beginRootTag(): RootTagInfo =
        RootTagInfo(tag.type)

    override fun beginCompound() {
        val compound = nextTag as NbtCompound<*>
        iteratorStack += compound.iterator()
        iterate()
    }

    override fun beginCompoundEntry(): CompoundEntryInfo =
        nextTag?.let { CompoundEntryInfo(it.type, nextTagName) }
            ?: CompoundEntryInfo.End

    override fun endCompound() {
        iteratorStack.removeLast()
        iterate()
    }

    override fun beginList(): ListInfo {
        val list = nextTag as NbtList<*>
        iteratorStack += list.iterator()
        iterate()
        return ListInfo(list.elementType, list.size)
    }

    override fun beginListEntry(): Boolean = nextTag != null

    override fun endList() {
        iteratorStack.removeLast()
        iterate()
    }

    override fun beginByteArray(): ArrayInfo {
        val array = nextTag as NbtByteArray
        iteratorStack += array.iterator()
        iterate()
        return ArrayInfo(array.size)
    }

    override fun beginByteArrayEntry(): Boolean = nextTag != null

    override fun endByteArray() {
        iteratorStack.removeLast()
        iterate()
    }

    override fun beginIntArray(): ArrayInfo {
        val array = nextTag as NbtIntArray
        iteratorStack += array.iterator()
        iterate()
        return ArrayInfo(array.size)
    }

    override fun beginIntArrayEntry(): Boolean = nextTag != null

    override fun endIntArray() {
        iteratorStack.removeLast()
        iterate()
    }

    override fun beginLongArray(): ArrayInfo {
        val array = nextTag as NbtLongArray
        iteratorStack += array.iterator()
        iterate()
        return ArrayInfo(array.size)
    }

    override fun beginLongArrayEntry(): Boolean = nextTag != null

    override fun endLongArray() {
        iteratorStack.removeLast()
        iterate()
    }

    override fun readByte(): Byte {
        return (nextTag as NbtByte).value
            .also { iterate() }
    }

    override fun readShort(): Short {
        return (nextTag as NbtShort).value
            .also { iterate() }
    }

    override fun readInt(): Int {
        return (nextTag as NbtInt).value
            .also { iterate() }
    }

    override fun readLong(): Long {
        return (nextTag as NbtLong).value
            .also { iterate() }
    }

    override fun readFloat(): Float {
        return (nextTag as NbtFloat).value
            .also { iterate() }
    }

    override fun readDouble(): Double {
        return (nextTag as NbtDouble).value
            .also { iterate() }
    }

    override fun readString(): String {
        return (nextTag as NbtString).value
            .also { iterate() }
    }
}


