package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public abstract class NbtVariant private constructor(private val name: String) {
    internal abstract fun getBinarySource(source: BufferedSource): BinarySource
    internal abstract fun getBinarySink(sink: BufferedSink): BinarySink

    override fun toString(): String = name

    public object Java : NbtVariant("Java") {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            BigEndianBinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            BigEndianBinarySink(sink)
    }

    public object Bedrock : NbtVariant("Bedrock") {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            LittleEndianBinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            LittleEndianBinarySink(sink)
    }

    public object BedrockNetwork : NbtVariant("BedrockNetwork") {
        override fun getBinarySource(source: BufferedSource): BinarySource =
            LittleEndianBase128BinarySource(source)

        override fun getBinarySink(sink: BufferedSink): BinarySink =
            LittleEndianBase128BinarySink(sink)
    }
}
