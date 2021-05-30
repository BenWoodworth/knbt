package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.NbtEncodingException
import okio.BufferedSink
import okio.Closeable

internal interface BinarySink : Closeable {
    fun writeByte(value: Byte)
    fun writeInt(value: Int)
    fun writeShort(value: Short)
    fun writeLong(value: Long)

    fun writeFloat(value: Float)
    fun writeDouble(value: Double)

    fun writeString(value: String)
}

internal class BigEndianBinarySink(private val sink: BufferedSink) : BinarySink {
    override fun close(): Unit = sink.close()

    override fun writeByte(value: Byte) {
        sink.writeByte(value.toInt())
    }

    override fun writeShort(value: Short) {
        sink.writeShort(value.toInt())
    }

    override fun writeInt(value: Int) {
        sink.writeInt(value)
    }

    override fun writeLong(value: Long) {
        sink.writeLong(value)
    }

    override fun writeFloat(value: Float) {
        sink.writeInt(value.toRawBits())
    }

    override fun writeDouble(value: Double) {
        sink.writeLong(value.toRawBits())
    }

    override fun writeString(value: String) {
        val bytes = value.encodeToByteArray()
        if (bytes.size > UShort.MAX_VALUE.toInt()) throw NbtEncodingException("String too long to encode")

        sink.writeShort(bytes.size)
        sink.write(bytes)
    }
}

internal class LittleEndianBinarySink(private val sink: BufferedSink) : BinarySink {
    override fun close(): Unit = sink.close()

    override fun writeByte(value: Byte) {
        sink.writeByte(value.toInt())
    }

    override fun writeShort(value: Short) {
        sink.writeShortLe(value.toInt())
    }

    override fun writeInt(value: Int) {
        sink.writeIntLe(value)
    }

    override fun writeLong(value: Long) {
        sink.writeLongLe(value)
    }

    override fun writeFloat(value: Float) {
        sink.writeIntLe(value.toRawBits())
    }

    override fun writeDouble(value: Double) {
        sink.writeLongLe(value.toRawBits())
    }

    override fun writeString(value: String) {
        val bytes = value.encodeToByteArray()
        if (bytes.size > UShort.MAX_VALUE.toInt()) throw NbtEncodingException("String too long to encode")

        sink.writeShortLe(bytes.size)
        sink.write(bytes)
    }
}
