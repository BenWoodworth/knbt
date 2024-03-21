package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public abstract class NbtVariant private constructor() {
    internal abstract fun getBinarySource(source: BufferedSource): BinarySource
    internal abstract fun getBinarySink(sink: BufferedSink): BinarySink

    public data object Java : NbtVariant() {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            BigEndianBinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            BigEndianBinarySink(sink)
    }

    public data object Bedrock : NbtVariant() {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            LittleEndianBinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            LittleEndianBinarySink(sink)
    }

    public data object BedrockNetwork : NbtVariant() {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            LittleEndianBase128BinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            LittleEndianBase128BinarySink(sink)
    }
}
