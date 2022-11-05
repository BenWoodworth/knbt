package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.internal.NbtPath.*
import net.benwoodworth.knbt.internal.NbtTagType.*

internal class PathTrackingNbtReader(
    private val delegate: NbtReader,
) : NbtReader {
    private val path = ArrayDeque<Node>()
    private var firstEntry = false
    private var firstCollectionEntryType = TAG_End

    private fun onBeginCompound() {
        firstEntry = true
    }

    private fun onBeginCompoundEntry(info: NbtReader.CompoundEntryInfo) {
        if (!firstEntry) path.removeLast()
        firstEntry = false

        if (info.type != TAG_End) {
            path += NameNode(info.name, info.type)
        }
    }

    private fun onBeginCollection(type: NbtTagType) {
        firstEntry = true
        firstCollectionEntryType = type
    }

    private fun onBeginCollectionEntry(hasEntry: Boolean) {
        if (!hasEntry) {
            if (!firstEntry) path.removeLast()
            return
        }

        val type: NbtTagType
        val index: Int

        if (firstEntry) {
            firstEntry = false
            type = firstCollectionEntryType
            index = 0
        } else {
            val previous = path.removeLast() as IndexNode
            type = previous.type
            index = previous.index + 1
        }

        path += IndexNode(index, type)
    }

    fun getPath(): NbtPath =
        NbtPath(path.toList())

    override fun beginRootTag(): NbtReader.RootTagInfo =
        delegate.beginRootTag().also { info ->
            path += RootNode(info.type)
        }

    override fun beginCompound(): Unit =
        delegate.beginCompound().also {
            onBeginCompound()
        }

    override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo =
        delegate.beginCompoundEntry().also { info ->
            onBeginCompoundEntry(info)
        }

    override fun endCompound(): Unit =
        delegate.endCompound()

    override fun beginList(): NbtReader.ListInfo =
        delegate.beginList().also { info ->
            onBeginCollection(info.type)
        }

    override fun beginListEntry(): Boolean =
        delegate.beginListEntry().also { hasEntry ->
            onBeginCollectionEntry(hasEntry)
        }

    override fun endList(): Unit =
        delegate.endList()

    override fun beginByteArray(): NbtReader.ArrayInfo =
        delegate.beginByteArray().also {
            onBeginCollection(TAG_Byte)
        }

    override fun beginByteArrayEntry(): Boolean =
        delegate.beginByteArrayEntry().also { hasEntry ->
            onBeginCollectionEntry(hasEntry)
        }

    override fun endByteArray(): Unit =
        delegate.endByteArray()

    override fun beginIntArray(): NbtReader.ArrayInfo =
        delegate.beginIntArray().also {
            onBeginCollection(TAG_Int)
        }

    override fun beginIntArrayEntry(): Boolean =
        delegate.beginIntArrayEntry().also { hasEntry ->
            onBeginCollectionEntry(hasEntry)
        }

    override fun endIntArray(): Unit =
        delegate.endIntArray()

    override fun beginLongArray(): NbtReader.ArrayInfo =
        delegate.beginLongArray().also {
            onBeginCollection(TAG_Long)
        }

    override fun beginLongArrayEntry(): Boolean =
        delegate.beginLongArrayEntry().also { hasEntry ->
            onBeginCollectionEntry(hasEntry)
        }

    override fun endLongArray(): Unit =
        delegate.endLongArray()

    override fun readByte(): Byte =
        delegate.readByte()

    override fun readShort(): Short =
        delegate.readShort()

    override fun readInt(): Int =
        delegate.readInt()

    override fun readLong(): Long =
        delegate.readLong()

    override fun readFloat(): Float =
        delegate.readFloat()

    override fun readDouble(): Double =
        delegate.readDouble()

    override fun readString(): String =
        delegate.readString()
}
