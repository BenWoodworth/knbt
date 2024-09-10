package net.benwoodworth.knbt

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.test.asSource
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.isEmptyNamedVersion
import net.benwoodworth.knbt.test.parameters.isUnnamedVersion
import net.benwoodworth.knbt.test.parameters.parameterOfNbtTagSubtypeEdgeCases
import net.benwoodworth.knbt.test.parameters.parameterOfNbtVariantEdgeCases
import okio.buffer
import kotlin.test.*

@OptIn(OkioApi::class)
class NbtVariantJavaNetworkTest {
    private fun javaNetworkNbt(protocolVersion: Int) = BinaryNbtFormat {
        variant = NbtVariant.JavaNetwork(protocolVersion)
        compression = NbtCompression.None
    }

    private fun javaNetworkNbt(variant: NbtVariant.JavaNetwork) =
        javaNetworkNbt(variant.protocolVersion)

    private val javaNbt = BinaryNbtFormat {
        variant = NbtVariant.Java
        compression = NbtCompression.None
    }

    private fun ParameterizeScope.parameterOfJavaNetworkEdgeCases() =
        parameterOfNbtVariantEdgeCases().arguments
            .filterIsInstance<NbtVariant.JavaNetwork>()
            .let { parameter(it) }

    @Test
    fun negative_protocol_version_should_throw() = parameterizeTest {
        val protocolVersion by parameterOf(-1, Int.MIN_VALUE)

        val failure = assertFailsWith<IllegalArgumentException> {
            NbtVariant.JavaNetwork(protocolVersion)
        }

        assertEquals("Protocol version must be non-negative, but is $protocolVersion", failure.message)
    }

    @Test
    fun base_snapshot_protocol_version_should_throw() {
        val baseVersion = 0x40000000

        val failure = assertFailsWith<IllegalArgumentException> {
            NbtVariant.JavaNetwork(baseVersion)
        }

        val versionHex = baseVersion.toString(16)
        val firstVersionHex = (baseVersion + 1).toString(16)

        assertEquals(
            "Invalid snapshot protocol version: 0x$versionHex. Snapshot versions start at 0x$firstVersionHex",
            failure.message
        )
    }

    @Test
    fun equals_should_be_true_if_type_and_protocol_version_are_the_same() = parameterizeTest {
        val variant by parameterOfJavaNetworkEdgeCases()

        val other by parameter {
            this@parameterizeTest.parameterOfNbtVariantEdgeCases().arguments +
                    NbtVariant.JavaNetwork(variant.protocolVersion) + // Different instance
                    Any() +
                    null
        }

        val expectedEquals = other is NbtVariant.JavaNetwork &&
                variant.protocolVersion == (other as NbtVariant.JavaNetwork).protocolVersion

        assertEquals(expectedEquals, variant == other)
    }

    @Test
    fun hash_code_should_be_the_same_for_equal_variants() = parameterizeTest {
        val variant by parameterOfJavaNetworkEdgeCases()

        val equalVariant = NbtVariant.JavaNetwork(variant.protocolVersion)

        assertEquals(variant.hashCode(), equalVariant.hashCode())
    }

    @Test
    fun string_representation_should_have_protocol_version() = parameterizeTest {
        val variant by parameterOfJavaNetworkEdgeCases()

        val expected =
            "${NbtVariant.JavaNetwork::class.simpleName}(${NbtVariant.JavaNetwork::protocolVersion.name} = ${variant.protocolVersion})"

        assertEquals(expected, variant.toString())
    }

    @Test
    fun empty_name_variant_should_write_the_same_as_Java_with_empty_name() = parameterizeTest {
        val variant by parameterOfJavaNetworkEdgeCases()
        assume(variant.isEmptyNamedVersion)

        val nbtTag by parameterOfNbtTagSubtypeEdgeCases()

        val javaBytes = javaNbt
            .encodeToByteArray(NbtNamed("", nbtTag))

        val actualBytes = javaNetworkNbt(variant)
            .encodeToByteArray(nbtTag)

        assertContentEquals(javaBytes, actualBytes)
    }

    @Test
    fun empty_name_variant_should_read_correctly() = parameterizeTest {
        val variant by parameterOfJavaNetworkEdgeCases()
        assume(variant.isEmptyNamedVersion)

        val nbtTag by parameterOfNbtTagSubtypeEdgeCases()

        val bytes = javaNetworkNbt(variant).encodeToByteArray(nbtTag)

        val source = bytes.asSource().buffer()
        val decoded = javaNetworkNbt(variant).decodeFromBufferedSource<NbtTag>(source)

        assertTrue(source.exhausted(), "Source was not exhausted")
        assertEquals(nbtTag, decoded)
    }

    @Test
    fun empty_name_variant_should_ignore_non_empty_root_names() = parameterizeTest {
        val variant by parameterOfJavaNetworkEdgeCases()
        assume(variant.isEmptyNamedVersion)

        val emptyNamedNbt = javaNetworkNbt(variant)

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
        val variant by parameterOfJavaNetworkEdgeCases()
        assume(variant.isUnnamedVersion)

        val nbtTag by parameterOfNbtTagSubtypeEdgeCases()

        val javaBytes = javaNbt
            .encodeToByteArray(NbtNamed("", nbtTag))

        val tagTypeByte = javaBytes.take(1)
        val tagValueBytes = javaBytes.drop(3)
        val expectedBytes = (tagTypeByte + tagValueBytes).toByteArray()

        val actualBytes = javaNetworkNbt(variant)
            .encodeToByteArray(nbtTag)

        assertContentEquals(expectedBytes, actualBytes)

    }

    @Test
    fun unnamed_variant_should_read_correctly() = parameterizeTest {
        val variant by parameterOfJavaNetworkEdgeCases()
        assume(variant.isUnnamedVersion)

        val nbtTag by parameterOfNbtTagSubtypeEdgeCases()

        val bytes = javaNetworkNbt(variant).encodeToByteArray(nbtTag)

        val source = bytes.asSource().buffer()
        val decoded = javaNetworkNbt(variant).decodeFromBufferedSource<NbtTag>(source)

        assertTrue(source.exhausted(), "Source was not exhausted")
        assertEquals(nbtTag, decoded)
    }
}
