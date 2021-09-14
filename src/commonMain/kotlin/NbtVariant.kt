package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public sealed class NbtVariant {
    internal abstract fun BufferedSource.asBinarySource(): BinarySource
    internal abstract fun BufferedSink.asBinarySink(): BinarySink

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

    public object Java : NbtVariant() {
        override fun BufferedSource.asBinarySource(): BinarySource =
            BigEndianBinarySource(this)

        override fun BufferedSink.asBinarySink(): BinarySink =
            BigEndianBinarySink(this)

        override fun toString(): String = "Java"
    }

    public object Bedrock : NbtVariant() {
        override fun BufferedSource.asBinarySource(): BinarySource =
            LittleEndianBinarySource(this)

        override fun BufferedSink.asBinarySink(): BinarySink =
            LittleEndianBinarySink(this)

        override fun toString(): String = "Bedrock"
    }

    public object BedrockNetwork : NbtVariant() {
        override fun BufferedSource.asBinarySource(): BinarySource =
            LittleEndianBase128BinarySource(this)

        override fun BufferedSink.asBinarySink(): BinarySink =
            LittleEndianBase128BinarySink(this)

        override fun toString(): String = "BedrockNetwork"
    }

    @Deprecated(
        "Removed in favor of NbtVariant.Java",
        ReplaceWith("NbtVariant.Java", "net.benwoodworth.knbt.NbtVariant"),
        DeprecationLevel.ERROR,
    )
    public object BigEndian : NbtVariant() {
        override fun BufferedSource.asBinarySource(): BinarySource = Java.run { asBinarySource() }
        override fun BufferedSink.asBinarySink(): BinarySink = Java.run { asBinarySink() }

        override fun toString(): String = "BigEndian"
    }

    @Deprecated(
        "Removed in favor of NbtVariant.Bedrock",
        ReplaceWith("NbtVariant.Bedrock", "net.benwoodworth.knbt.NbtVariant"),
        DeprecationLevel.ERROR,
    )
    public object LittleEndian : NbtVariant() {
        override fun BufferedSource.asBinarySource(): BinarySource = Bedrock.run { asBinarySource() }
        override fun BufferedSink.asBinarySink(): BinarySink = Bedrock.run { asBinarySink() }

        override fun toString(): String = "LittleEndian"
    }

    @Deprecated(
        "Removed in favor of NbtVariant.BedrockNetwork",
        ReplaceWith("NbtVariant.BedrockNetwork", "net.benwoodworth.knbt.NbtVariant"),
        DeprecationLevel.ERROR,
    )
    public object LittleEndianBase128 : NbtVariant() {
        override fun BufferedSource.asBinarySource(): BinarySource = BedrockNetwork.run { asBinarySource() }
        override fun BufferedSink.asBinarySink(): BinarySink = BedrockNetwork.run { asBinarySink() }

        override fun toString(): String = "LittleEndianBase128"
    }
}
