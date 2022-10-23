package net.benwoodworth.knbt.mocks

import net.benwoodworth.knbt.fix
import net.benwoodworth.knbt.internal.NbtTagType
import net.benwoodworth.knbt.internal.NbtWriter
import net.benwoodworth.knbt.util.VerifyingMockFactory

internal object VerifyingNbtWriterMock :
    VerifyingMockFactory<NbtWriter, VerifyingNbtWriterMock.Builder>(VerifyingNbtWriterMock::Builder) {

    private class Mock : NbtWriter, VerifyingMockFactory.Mock() {
        override fun beginRootTag(type: NbtTagType): Unit = ::beginRootTag.called(type)
        override fun beginCompound(): Unit = ::beginCompound.called()
        override fun beginCompoundEntry(type: NbtTagType, name: String): Unit = ::beginCompoundEntry.called(type, name)
        override fun endCompound(): Unit = ::endCompound.called()
        override fun beginList(type: NbtTagType, size: Int): Unit = ::beginList.called(type, size)
        override fun beginListEntry(): Unit = ::beginListEntry.called()
        override fun endList(): Unit = ::endList.called()
        override fun beginByteArray(size: Int): Unit = ::beginByteArray.called(size)
        override fun beginByteArrayEntry(): Unit = ::beginByteArrayEntry.called()
        override fun endByteArray(): Unit = ::endByteArray.called()
        override fun beginIntArray(size: Int): Unit = ::beginIntArray.called(size)
        override fun beginIntArrayEntry(): Unit = ::beginIntArrayEntry.called()
        override fun endIntArray(): Unit = ::endIntArray.called()
        override fun beginLongArray(size: Int): Unit = ::beginLongArray.called(size)
        override fun beginLongArrayEntry(): Unit = ::beginLongArrayEntry.called()
        override fun endLongArray(): Unit = ::endLongArray.called()
        override fun writeByte(value: Byte): Unit = ::writeByte.called(value)
        override fun writeShort(value: Short): Unit = ::writeShort.called(value)
        override fun writeInt(value: Int): Unit = ::writeInt.called(value)
        override fun writeLong(value: Long): Unit = ::writeLong.called(value)
        override fun writeFloat(value: Float): Unit = ::writeFloat.called(value.fix())
        override fun writeDouble(value: Double): Unit = ::writeDouble.called(value)
        override fun writeString(value: String): Unit = ::writeString.called(value)
    }

    class Builder : VerifyingMockFactory.Builder<NbtWriter>(VerifyingNbtWriterMock::Mock) {
        fun beginRootTag(type: NbtTagType): Unit = ::beginRootTag.called(type)
        fun beginCompound(): Unit = ::beginCompound.called()
        fun beginCompoundEntry(type: NbtTagType, name: String): Unit = ::beginCompoundEntry.called(type, name)
        fun endCompound(): Unit = ::endCompound.called()
        fun beginList(type: NbtTagType, size: Int): Unit = ::beginList.called(type, size)
        fun beginListEntry(): Unit = ::beginListEntry.called()
        fun endList(): Unit = ::endList.called()
        fun beginByteArray(size: Int): Unit = ::beginByteArray.called(size)
        fun beginByteArrayEntry(): Unit = ::beginByteArrayEntry.called()
        fun endByteArray(): Unit = ::endByteArray.called()
        fun beginIntArray(size: Int): Unit = ::beginIntArray.called(size)
        fun beginIntArrayEntry(): Unit = ::beginIntArrayEntry.called()
        fun endIntArray(): Unit = ::endIntArray.called()
        fun beginLongArray(size: Int): Unit = ::beginLongArray.called(size)
        fun beginLongArrayEntry(): Unit = ::beginLongArrayEntry.called()
        fun endLongArray(): Unit = ::endLongArray.called()
        fun writeByte(value: Byte): Unit = ::writeByte.called(value)
        fun writeShort(value: Short): Unit = ::writeShort.called(value)
        fun writeInt(value: Int): Unit = ::writeInt.called(value)
        fun writeLong(value: Long): Unit = ::writeLong.called(value)
        fun writeFloat(value: Float): Unit = ::writeFloat.called(value.fix())
        fun writeDouble(value: Double): Unit = ::writeDouble.called(value)
        fun writeString(value: String): Unit = ::writeString.called(value)
    }
}
