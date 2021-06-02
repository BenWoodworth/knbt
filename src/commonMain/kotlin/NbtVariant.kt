package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public sealed class NbtVariant {
    internal abstract fun getBinarySource(source: BufferedSource): BinarySource
    internal abstract fun getBinarySink(sink: BufferedSink): BinarySink

    @Deprecated("Use BigEndian instead.", ReplaceWith("BigEndian"))
    public object Java : NbtVariant() {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            BigEndian.getBinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            BigEndian.getBinarySink(sink)

        override fun toString(): String = "Java"
    }

    public object BigEndian : NbtVariant() {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            BigEndianBinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            BigEndianBinarySink(sink)

        override fun toString(): String = "BigEndian"
    }

    public object LittleEndian : NbtVariant() {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            LittleEndianBinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            LittleEndianBinarySink(sink)

        override fun toString(): String = "LittleEndian"
    }

    public object LittleEndianBase128 : NbtVariant() {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            LittleEndianBase128BinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            LittleEndianBase128BinarySink(sink)

        override fun toString(): String = "LittleEndianBase128"
    }
}
