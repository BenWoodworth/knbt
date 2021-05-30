package net.benwoodworth.knbt.internal

import okio.BufferedSource

internal interface BinarySource {
    fun close()

    fun readByte(): Byte
    fun readInt(): Int
    fun readShort(): Short
    fun readLong(): Long
    fun readFloat(): Float
    fun readDouble(): Double
    fun readString(): String
}

internal class BigEndianBinarySource(private val source: BufferedSource) : BinarySource {
    override fun close(): Unit = source.close()

    override fun readByte(): Byte =
        source.readByte()

    override fun readShort(): Short =
        source.readShort()

    override fun readInt(): Int =
        source.readInt()

    override fun readLong(): Long =
        source.readLong()

    override fun readFloat(): Float =
        Float.fromBits(source.readInt())

    override fun readDouble(): Double =
        Double.fromBits(source.readLong())

    override fun readString(): String {
        val byteCount = source.readShort().toUShort().toLong()
        return source.readUtf8(byteCount)
    }
}

internal class LittleEndianBinarySource(private val source: BufferedSource) : BinarySource {
    override fun close(): Unit = source.close()

    override fun readByte(): Byte =
        source.readByte()

    override fun readShort(): Short =
        source.readShortLe()

    override fun readInt(): Int =
        source.readIntLe()

    override fun readLong(): Long =
        source.readLongLe()

    override fun readFloat(): Float =
        Float.fromBits(source.readIntLe())

    override fun readDouble(): Double =
        Double.fromBits(source.readLongLe())

    override fun readString(): String {
        val byteCount = source.readShortLe().toUShort().toLong()
        return source.readUtf8(byteCount)
    }
}
