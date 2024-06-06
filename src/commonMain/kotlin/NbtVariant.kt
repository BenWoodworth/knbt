package net.benwoodworth.knbt

import net.benwoodworth.knbt.internal.*
import okio.BufferedSink
import okio.BufferedSource

public abstract class NbtVariant private constructor() {
    internal abstract val capabilities: NbtCapabilities

    internal abstract fun getNbtReader(source: BufferedSource): BinaryNbtReader
    internal abstract fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter

    public data object Java : NbtVariant() {
        override val capabilities: NbtCapabilities =
            NbtCapabilities(namedRoot = true)

        override fun getNbtReader(source: BufferedSource): BinaryNbtReader =
            JavaNbtReader(source)

        override fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter =
            JavaNbtWriter(sink)
    }

    /**
     * Serializes unnamed [NbtTag]s for use in network packets.
     *
     * #### Protocol Version Changelog
     *
     * | Release | Snapshot   | Notes                                  |
     * |---------|------------|----------------------------------------|
     * | 0       | 0x40000001 | Serializes with an empty root tag name |
     * | 764     | 0x40000090 | Serializes without a root tag name     |
     */
    public class JavaNetwork(public val protocolVersion: Int) : NbtVariant() {
        init {
            require(protocolVersion >= 0) { "Protocol version must be non-negative, but is $protocolVersion" }
            require(protocolVersion != 0x40000000) { "Invalid snapshot protocol version: 0x40000000. Snapshot versions start at 0x40000001" }
        }

        private val protocolVersionVariant = when (protocolVersion) {
            in 0..763, in 0x40000001..0x40000089 -> EmptyNamedRoot
            else -> UnnamedRoot
        }

        override fun equals(other: Any?): Boolean =
            this === other || other is JavaNetwork && other.protocolVersion == this.protocolVersion

        override fun hashCode(): Int = protocolVersion

        override fun toString(): String = "JavaNetwork(protocolVersion = $protocolVersion)"


        override val capabilities: NbtCapabilities
            get() = protocolVersionVariant.capabilities

        override fun getNbtReader(source: BufferedSource): BinaryNbtReader =
            protocolVersionVariant.getNbtReader(source)

        override fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter =
            protocolVersionVariant.getNbtWriter(sink)

        private object EmptyNamedRoot : NbtVariant() {
            override val capabilities: NbtCapabilities =
                NbtCapabilities(namedRoot = false)

            override fun getNbtReader(source: BufferedSource): BinaryNbtReader =
                JavaNetworkNbtReader.EmptyNamedRoot(source)

            override fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter =
                JavaNetworkNbtWriter.EmptyNamedRoot(sink)
        }

        private object UnnamedRoot : NbtVariant() {
            override val capabilities: NbtCapabilities =
                NbtCapabilities(namedRoot = false)

            override fun getNbtReader(source: BufferedSource): BinaryNbtReader =
                JavaNetworkNbtReader.UnnamedRoot(source)

            override fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter =
                JavaNetworkNbtWriter.UnnamedRoot(sink)
        }
    }

    public data object Bedrock : NbtVariant() {
        override val capabilities: NbtCapabilities =
            NbtCapabilities(namedRoot = true)

        override fun getNbtReader(source: BufferedSource): BinaryNbtReader =
            BedrockNbtReader(source)

        override fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter =
            BedrockNbtWriter(sink)
    }

    public data object BedrockNetwork : NbtVariant() {
        override val capabilities: NbtCapabilities =
            NbtCapabilities(namedRoot = false)

        override fun getNbtReader(source: BufferedSource): BinaryNbtReader =
            BedrockNetworkNbtReader(source)

        override fun getNbtWriter(sink: BufferedSink): BinaryNbtWriter =
            BedrockNetworkNbtWriter(sink)
    }
}
