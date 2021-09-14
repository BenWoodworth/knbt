package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public abstract class NbtVariant private constructor(private val name: String) {
    internal abstract fun getBinarySource(source: BufferedSource): BinarySource
    internal abstract fun getBinarySink(sink: BufferedSink): BinarySink

    override fun toString(): String = name

    @Deprecated(
        "Aliases replaced with NbtVariant.* objects of the same name",
        ReplaceWith("NbtVariant", "net.benwoodworth.knbt.NbtVariant"),
        DeprecationLevel.ERROR,
    )
    public object Companion {
        @Deprecated(
            "Changed to object NbtVariant.Java",
            ReplaceWith("NbtVariant.Java", "net.benwoodworth.knbt.NbtVariant"),
            DeprecationLevel.ERROR,
        )
        public inline val Java: Java
            get() = NbtVariant.Java

        @Deprecated(
            "Changed to object NbtVariant.Bedrock",
            ReplaceWith("NbtVariant.Bedrock", "net.benwoodworth.knbt.NbtVariant"),
            DeprecationLevel.ERROR,
        )
        public inline val Bedrock: Bedrock
            get() = NbtVariant.Bedrock

        @Deprecated(
            "Changed to object NbtVariant.BedrockNetwork",
            ReplaceWith("NbtVariant.BedrockNetwork", "net.benwoodworth.knbt.NbtVariant"),
            DeprecationLevel.ERROR,
        )
        public inline val BedrockNetwork: BedrockNetwork
            get() = NbtVariant.BedrockNetwork
    }

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

    @Deprecated(
        "Removed in favor of NbtVariant.Java",
        ReplaceWith("NbtVariant.Java", "net.benwoodworth.knbt.NbtVariant"),
        DeprecationLevel.ERROR,
    )
    public object BigEndian : NbtVariant("BigEndian") {
        override fun getBinarySource(source: BufferedSource): BinarySource = Java.getBinarySource(source)
        override fun getBinarySink(sink: BufferedSink): BinarySink = Java.getBinarySink(sink)
    }

    @Deprecated(
        "Removed in favor of NbtVariant.Bedrock",
        ReplaceWith("NbtVariant.Bedrock", "net.benwoodworth.knbt.NbtVariant"),
        DeprecationLevel.ERROR,
    )
    public object LittleEndian : NbtVariant("LittleEndian") {
        override fun getBinarySource(source: BufferedSource): BinarySource = Bedrock.getBinarySource(source)
        override fun getBinarySink(sink: BufferedSink): BinarySink = Bedrock.getBinarySink(sink)
    }

    @Deprecated(
        "Removed in favor of NbtVariant.BedrockNetwork",
        ReplaceWith("NbtVariant.BedrockNetwork", "net.benwoodworth.knbt.NbtVariant"),
        DeprecationLevel.ERROR,
    )
    public object LittleEndianBase128 : NbtVariant("LittleEndianBase128") {
        override fun getBinarySource(source: BufferedSource): BinarySource = BedrockNetwork.getBinarySource(source)
        override fun getBinarySink(sink: BufferedSink): BinarySink = BedrockNetwork.getBinarySink(sink)
    }
}
