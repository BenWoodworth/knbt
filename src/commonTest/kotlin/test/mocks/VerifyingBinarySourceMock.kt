package net.benwoodworth.knbt.test.mocks

import net.benwoodworth.knbt.internal.BinarySource

internal object VerifyingBinarySourceMock :
    VerifyingMockFactory<BinarySource, VerifyingBinarySourceMock.Builder>(VerifyingBinarySourceMock::Builder) {

    private class Mock : BinarySource, VerifyingMockFactory.Mock() {
        override fun close(): Unit = ::close.called()
        override fun readByte(): Byte = ::readByte.called()
        override fun readShort(): Short = ::readShort.called()
        override fun readInt(): Int = ::readInt.called()
        override fun readLong(): Long = ::readLong.called()
        override fun readFloat(): Float = ::readFloat.called()
        override fun readDouble(): Double = ::readDouble.called()
        override fun readString(): String = ::readString.called()
    }

    class Builder : VerifyingMockFactory.Builder<BinarySource>(VerifyingBinarySourceMock::Mock) {
        fun close(): Unit = ::close.called()
        fun readByte(): Call<Byte> = ::readByte.called()
        fun readShort(): Call<Short> = ::readShort.called()
        fun readInt(): Call<Int> = ::readInt.called()
        fun readLong(): Call<Long> = ::readLong.called()
        fun readFloat(): Call<Float> = ::readFloat.called()
        fun readDouble(): Call<Double> = ::readDouble.called()
        fun readString(): Call<String> = ::readString.called()
    }
}
