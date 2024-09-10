package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.JavaNetworkNbt.ProtocolType
import net.benwoodworth.knbt.JavaNetworkNbt.ProtocolType.*
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.test.asSource
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfNbtTagSubtypeEdgeCases
import okio.buffer
import kotlin.test.*

@OptIn(OkioApi::class)
class JavaNetworkNbtTest {
    private val baseNbt = JavaNetworkNbt {
        protocolVersion = 0
        compression = NbtCompression.None
    }

    private val javaNbt = JavaNbt {
        compression = NbtCompression.None
    }

    private data class ProtocolVersionRange(val type: ProtocolType, val versionRange: IntRange)

    private val protocolVersionRages = listOf(
        ProtocolVersionRange(EmptyNamedRoot, 0..763),
        ProtocolVersionRange(EmptyNamedRoot, 0x40000001..0x40000089),
        ProtocolVersionRange(UnnamedRoot, 764..<0x40000000),
        ProtocolVersionRange(UnnamedRoot, 0x40000090..Int.MAX_VALUE),
    )

    private val protocolVersionEdgeCases = protocolVersionRages
        .flatMap { (_, versionRange) ->
            listOf(versionRange.first, versionRange.last)
        }

    private val Int.protocolType: ProtocolType
        get() = protocolVersionRages
            .firstOrNull { this in it.versionRange }
            ?.type
            ?: throw IllegalArgumentException("Bad Java network protocol version: $this")

    @Test
    fun negative_protocol_version_should_throw() = parameterizeTest {
        val protocolVersion by parameterOf(-1, Int.MIN_VALUE)

        val failure = assertFailsWith<IllegalArgumentException> {
            JavaNetworkNbt { this.protocolVersion = protocolVersion }
        }

        assertEquals("Protocol version must be non-negative, but is $protocolVersion", failure.message)
    }

    @Test
    fun base_snapshot_protocol_version_should_throw() {
        val baseVersion = 0x40000000

        val failure = assertFailsWith<IllegalArgumentException> {
            JavaNetworkNbt { protocolVersion = baseVersion }
        }

        val versionHex = baseVersion.toString(16)
        val firstVersionHex = (baseVersion + 1).toString(16)

        assertEquals(
            "Invalid snapshot protocol version: 0x$versionHex. Snapshot versions start at 0x$firstVersionHex",
            failure.message
        )
    }

    @Test
    fun empty_name_protocol_version_should_write_the_same_as_Java_with_empty_name() = parameterizeTest {
        val protocolVersion by parameter(protocolVersionEdgeCases)
        assume(protocolVersion.protocolType == EmptyNamedRoot)

        val emptyNamedNbt = JavaNetworkNbt(baseNbt) { this.protocolVersion = protocolVersion }

        val nbtTag by parameterOfNbtTagSubtypeEdgeCases()

        val javaBytes = javaNbt
            .encodeToByteArray(NbtNamed("", nbtTag))

        val actualBytes = emptyNamedNbt.encodeToByteArray(nbtTag)

        assertContentEquals(javaBytes, actualBytes)
    }

    @Test
    fun empty_name_variant_should_read_correctly() = parameterizeTest {
        val protocolVersion by parameter(protocolVersionEdgeCases)
        assume(protocolVersion.protocolType == EmptyNamedRoot)

        val emptyNamedNbt = JavaNetworkNbt(baseNbt) { this.protocolVersion = protocolVersion }

        val nbtTag by parameterOfNbtTagSubtypeEdgeCases()

        val bytes = emptyNamedNbt.encodeToByteArray(nbtTag)

        val source = bytes.asSource().buffer()
        val decoded = emptyNamedNbt.decodeFromBufferedSource<NbtTag>(source)

        assertTrue(source.exhausted(), "Source was not exhausted")
        assertEquals(nbtTag, decoded)
    }

    @Test
    fun empty_name_variant_should_ignore_non_empty_root_names() = parameterizeTest {
        val protocolVersion by parameter(protocolVersionEdgeCases)
        assume(protocolVersion.protocolType == EmptyNamedRoot)

        val emptyNamedNbt = JavaNetworkNbt(baseNbt) { this.protocolVersion = protocolVersion }

        val nbtTag by parameterOfNbtTagSubtypeEdgeCases()

        val emptyNamedTag = emptyNamedNbt.encodeToByteArray(nbtTag)
        val emptyNamedTagType = emptyNamedTag.take(1)
        val emptyNamedTagValue = emptyNamedTag.drop(1 + 2) // Type + Value

        val name = "name"
        val nameBytes = emptyNamedNbt.encodeToByteArray(name).drop(1 + 2) // Type + empty root name

        val namedTagBytes = emptyNamedTagType + nameBytes + emptyNamedTagValue
        val decodedNbtTag = emptyNamedNbt.decodeFromByteArray<NbtTag>(namedTagBytes.toByteArray())

        assertEquals(nbtTag, decodedNbtTag)
    }

    @Test
    fun unnamed_variant_should_write_the_same_as_Java_with_no_name() = parameterizeTest {
        val protocolVersion by parameter(protocolVersionEdgeCases)
        assume(protocolVersion.protocolType == UnnamedRoot)

        val unnamedNamedNbt = JavaNetworkNbt(baseNbt) { this.protocolVersion = protocolVersion }

        val nbtTag by parameterOfNbtTagSubtypeEdgeCases()

        val javaBytes = javaNbt
            .encodeToByteArray(NbtNamed("", nbtTag))

        val tagTypeByte = javaBytes.take(1)
        val tagValueBytes = javaBytes.drop(3)
        val expectedBytes = (tagTypeByte + tagValueBytes).toByteArray()

        val actualBytes = unnamedNamedNbt
            .encodeToByteArray(nbtTag)

        assertContentEquals(expectedBytes, actualBytes)

    }

    @Test
    fun unnamed_variant_should_read_correctly() = parameterizeTest {
        val protocolVersion by parameter(protocolVersionEdgeCases)
        assume(protocolVersion.protocolType == UnnamedRoot)

        val unnamedNamedNbt = JavaNetworkNbt(baseNbt) { this.protocolVersion = protocolVersion }

        val nbtTag by parameterOfNbtTagSubtypeEdgeCases()

        val bytes = unnamedNamedNbt.encodeToByteArray(nbtTag)

        val source = bytes.asSource().buffer()
        val decoded = unnamedNamedNbt.decodeFromBufferedSource<NbtTag>(source)

        assertTrue(source.exhausted(), "Source was not exhausted")
        assertEquals(nbtTag, decoded)
    }
}
