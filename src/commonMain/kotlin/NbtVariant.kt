package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public sealed class NbtVariant {
    internal abstract fun getBinarySource(source: BufferedSource): BinarySource
    internal abstract fun getBinarySink(sink: BufferedSink): BinarySink

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
