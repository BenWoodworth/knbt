package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public sealed class NbtVariant {
    internal abstract fun BufferedSource.asBinarySource(): BinarySource
    internal abstract fun BufferedSink.asBinarySink(): BinarySink

    public companion object {
        /**
         * Alias for [BigEndian].
         */
        public inline val Java: BigEndian
            get() = BigEndian

        /**
         * Alias for [LittleEndian].
         */
        public inline val Bedrock: LittleEndian
            get() = LittleEndian

        /**
         * Alias for [LittleEndianBase128].
         */
        public inline val BedrockNetwork: LittleEndianBase128
            get() = LittleEndianBase128
    }

    public object BigEndian : NbtVariant() {
        override fun BufferedSource.asBinarySource(): BinarySource =
            BigEndianBinarySource(this)

        override fun BufferedSink.asBinarySink(): BinarySink =
            BigEndianBinarySink(this)

        override fun toString(): String = "BigEndian"
    }

    public object LittleEndian : NbtVariant() {
        override fun BufferedSource.asBinarySource(): BinarySource =
            LittleEndianBinarySource(this)

        override fun BufferedSink.asBinarySink(): BinarySink =
            LittleEndianBinarySink(this)

        override fun toString(): String = "LittleEndian"
    }

    public object LittleEndianBase128 : NbtVariant() {
        override fun BufferedSource.asBinarySource(): BinarySource =
            LittleEndianBase128BinarySource(this)

        override fun BufferedSink.asBinarySink(): BinarySink =
            LittleEndianBase128BinarySink(this)

        override fun toString(): String = "LittleEndianBase128"
    }
}
