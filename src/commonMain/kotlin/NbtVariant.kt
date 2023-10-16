package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public abstract class NbtVariant private constructor(private val name: String) {
    internal abstract fun getNbtReader(source: BufferedSource): BinaryNbtReader
    internal abstract fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter

    override fun toString(): String = name

    public object Java : NbtVariant("Java") {
        override fun getNbtReader(source: BufferedSource): BinaryNbtReader =
            JavaNbtReader(source)

        override fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter =
            JavaNbtWriter(sink)
    }

    public object Bedrock : NbtVariant("Bedrock") {
        override fun getNbtReader(source: BufferedSource): BinaryNbtReader =
            BedrockNbtReader(source)

        override fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter =
            BedrockNbtWriter(sink)
    }

    public object BedrockNetwork : NbtVariant("BedrockNetwork") {
        override fun getNbtReader(source: BufferedSource): BinaryNbtReader =
            BedrockNetworkNbtReader(source)

        override fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter =
            BedrockNetworkNbtWriter(sink)
    }
}
