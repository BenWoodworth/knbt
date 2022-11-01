package net.benwoodworth.knbt.test.mocks

import net.benwoodworth.knbt.internal.NbtReader
import net.benwoodworth.knbt.test.fix

internal object VerifyingNbtReaderMock :
    VerifyingMockFactory<NbtReader, VerifyingNbtReaderMock.Builder>(VerifyingNbtReaderMock::Builder) {

    private class Mock : NbtReader, VerifyingMockFactory.Mock() {
        override fun beginRootTag(): NbtReader.RootTagInfo = ::beginRootTag.called()
        override fun beginCompound(): Unit = ::beginCompound.called()
        override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo = ::beginCompoundEntry.called()
        override fun endCompound(): Unit = ::endCompound.called()
        override fun beginList(): NbtReader.ListInfo = ::beginList.called()
        override fun beginListEntry(): Boolean = ::beginListEntry.called()
        override fun endList(): Unit = ::endList.called()
        override fun beginByteArray(): NbtReader.ArrayInfo = ::beginByteArray.called()
        override fun beginByteArrayEntry(): Boolean = ::beginByteArrayEntry.called()
        override fun endByteArray(): Unit = ::endByteArray.called()
        override fun beginIntArray(): NbtReader.ArrayInfo = ::beginIntArray.called()
        override fun beginIntArrayEntry(): Boolean = ::beginIntArrayEntry.called()
        override fun endIntArray(): Unit = ::endIntArray.called()
        override fun beginLongArray(): NbtReader.ArrayInfo = ::beginLongArray.called()
        override fun beginLongArrayEntry(): Boolean = ::beginLongArrayEntry.called()
        override fun endLongArray(): Unit = ::endLongArray.called()
        override fun readByte(): Byte = ::readByte.called()
        override fun readShort(): Short = ::readShort.called()
        override fun readInt(): Int = ::readInt.called()
        override fun readLong(): Long = ::readLong.called()
        override fun readFloat(): Float = ::readFloat.called().fix()
        override fun readDouble(): Double = ::readDouble.called()
        override fun readString(): String = ::readString.called()
    }

    class Builder : VerifyingMockFactory.Builder<NbtReader>(VerifyingNbtReaderMock::Mock) {
        fun beginRootTag(): Call<NbtReader.RootTagInfo> = ::beginRootTag.called()
        fun beginCompound(): Unit = ::beginCompound.called()
        fun beginCompoundEntry(): Call<NbtReader.CompoundEntryInfo> = ::beginCompoundEntry.called()
        fun endCompound(): Unit = ::endCompound.called()
        fun beginList(): Call<NbtReader.ListInfo> = ::beginList.called()
        fun beginListEntry(): Call<Boolean> = ::beginListEntry.called()
        fun endList(): Unit = ::endList.called()
        fun beginByteArray(): Call<NbtReader.ArrayInfo> = ::beginByteArray.called()
        fun beginByteArrayEntry(): Call<Boolean> = ::beginByteArrayEntry.called()
        fun endByteArray(): Unit = ::endByteArray.called()
        fun beginIntArray(): Call<NbtReader.ArrayInfo> = ::beginIntArray.called()
        fun beginIntArrayEntry(): Call<Boolean> = ::beginIntArrayEntry.called()
        fun endIntArray(): Unit = ::endIntArray.called()
        fun beginLongArray(): Call<NbtReader.ArrayInfo> = ::beginLongArray.called()
        fun beginLongArrayEntry(): Call<Boolean> = ::beginLongArrayEntry.called()
        fun endLongArray(): Unit = ::endLongArray.called()
        fun readByte(): Call<Byte> = ::readByte.called()
        fun readShort(): Call<Short> = ::readShort.called()
        fun readInt(): Call<Int> = ::readInt.called()
        fun readLong(): Call<Long> = ::readLong.called()
        fun readFloat(): Call<Float> = ::readFloat.called()
        fun readDouble(): Call<Double> = ::readDouble.called()
        fun readString(): Call<String> = ::readString.called()

        infix fun Call<Float>.returns(value: Float): Unit =
            returns<Float>(value.fix())
    }
}
