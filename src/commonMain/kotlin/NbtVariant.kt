package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public sealed class NbtVariant {
    internal abstract fun getBinarySource(source: BufferedSource): BinarySource
    internal abstract fun getBinarySink(sink: BufferedSink): BinarySink

    public object Java : NbtVariant() {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            BigEndianBinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            BigEndianBinarySink(sink)
    }

    public object Bedrock : NbtVariant() {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            LittleEndianBinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            LittleEndianBinarySink(sink)
    }
}
