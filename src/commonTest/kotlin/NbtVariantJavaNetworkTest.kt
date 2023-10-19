package net.benwoodworth.knbt

import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.of
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.okio.decodeFromBufferedSource
import net.benwoodworth.knbt.test.asSource
import net.benwoodworth.knbt.test.data.isEmptyNamedVersion
import net.benwoodworth.knbt.test.data.isUnnamedVersion
import net.benwoodworth.knbt.test.data.nbtTagEdgeCases
import net.benwoodworth.knbt.test.data.nbtVariantEdgeCases
import okio.buffer
import kotlin.test.*

@OptIn(OkioApi::class)
class NbtVariantJavaNetworkTest {
    private fun javaNetworkNbt(protocolVersion: Int) = Nbt {
        variant = NbtVariant.JavaNetwork(protocolVersion)
        compression = NbtCompression.None
    }

    private fun javaNetworkNbt(variant: NbtVariant.JavaNetwork) =
        javaNetworkNbt(variant.protocolVersion)

    private val javaNbt = Nbt {
        variant = NbtVariant.Java
        compression = NbtCompression.None
    }

    @Test
    fun negative_protocol_version_should_throw() = runTest {
        checkAll(
            Exhaustive.of(-1, Int.MIN_VALUE)
        ) { protocolVersion ->
            val failure = assertFailsWith<IllegalArgumentException> {
                NbtVariant.JavaNetwork(protocolVersion)
            }

            assertEquals("Protocol version must be non-negative, but is $protocolVersion", failure.message)
        }
    }

    @Test
    fun base_snapshot_protocol_version_should_throw() {
        val baseVersion = 0x40000000

        val failure = assertFailsWith<IllegalArgumentException> {
            NbtVariant.JavaNetwork(baseVersion)
        }

        val versionHex = baseVersion.toString(16)
        val firstVersionHex = (baseVersion + 1).toString(16)
        assertEquals("Invalid snapshot protocol version: 0x$versionHex. Snapshot versions start at 0x$firstVersionHex", failure.message)
    }

    @Test
    fun equals_should_be_true_if_type_and_protocol_version_are_the_same() = runTest {
        val variants = nbtVariantEdgeCases
            .filterIsInstance<NbtVariant.JavaNetwork>()

        val others = nbtVariantEdgeCases +
                variants.map { NbtVariant.JavaNetwork(it.protocolVersion) } + // Different instance
                Any() +
                null

        checkAll(
            variants.exhaustive(),
            others.exhaustive()
        ) { variant, other ->
            val expectedEquals = other is NbtVariant.JavaNetwork && variant.protocolVersion == other.protocolVersion

            assertEquals(expectedEquals, variant == other)
        }
    }

    @Test
    fun hash_code_should_be_the_same_for_equal_variants() = runTest {
        val variants = nbtVariantEdgeCases
            .filterIsInstance<NbtVariant.JavaNetwork>()

        checkAll(
            variants.exhaustive()
        ) { variant ->
            val equalVariant = NbtVariant.JavaNetwork(variant.protocolVersion)

            assertEquals(variant.hashCode(), equalVariant.hashCode())
        }
    }

    @Test
    fun string_representation_should_have_protocol_version() = runTest {
        val variants = nbtVariantEdgeCases
            .filterIsInstance<NbtVariant.JavaNetwork>()

        checkAll(
            variants.exhaustive()
        ) { variant ->
            val expected =
                "${NbtVariant.JavaNetwork::class.simpleName}(${NbtVariant.JavaNetwork::protocolVersion.name} = ${variant.protocolVersion})"

            assertEquals(expected, variant.toString())
        }
    }

    @Test
    fun empty_name_variant_should_write_the_same_as_Java_with_empty_name() = runTest {
        val emptyNameVariants = nbtVariantEdgeCases
            .filterIsInstance<NbtVariant.JavaNetwork>()
            .filter { it.isEmptyNamedVersion }

        checkAll(
            emptyNameVariants.exhaustive(),
            nbtTagEdgeCases.exhaustive()
        ) { variant, nbtTag ->
            val javaBytes = javaNbt
                .encodeToByteArray(
                    buildNbtCompound { put("", nbtTag) }
                )

            val actualBytes = javaNetworkNbt(variant)
                .encodeToByteArray(nbtTag)

            assertContentEquals(javaBytes, actualBytes)
        }
    }

    @Test
    fun empty_name_variant_should_read_correctly() = runTest {
        val variants = nbtVariantEdgeCases
            .filterIsInstance<NbtVariant.JavaNetwork>()
            .filter { it.isEmptyNamedVersion }

        checkAll(
            variants.exhaustive(),
            nbtTagEdgeCases.exhaustive()
        ) { variant, nbtTag ->
            val bytes = javaNetworkNbt(variant).encodeToByteArray(nbtTag)

            val source = bytes.asSource().buffer()
            val decoded = javaNetworkNbt(variant).decodeFromBufferedSource<NbtTag>(source)

            assertTrue(source.exhausted(), "Source was not exhausted")
            assertEquals(nbtTag, decoded)
        }
    }

    @Test
    fun empty_name_variant_should_ignore_non_empty_root_names() = runTest {
        val emptyNamedNbt = nbtVariantEdgeCases
            .filterIsInstance<NbtVariant.JavaNetwork>()
            .first { it.isEmptyNamedVersion }
            .let { javaNetworkNbt(it) }

        checkAll(
            nbtTagEdgeCases.exhaustive()
        ) { nbtTag ->
            val emptyNamedTag = emptyNamedNbt.encodeToByteArray(nbtTag)
            val emptyNamedTagType = emptyNamedTag.take(1)
            val emptyNamedTagValue = emptyNamedTag.drop(1 + 2) // Type + Value

            val name = "name"
            val nameBytes = emptyNamedNbt.encodeToByteArray(name).drop(1 + 2) // Type + empty root name

            val namedTagBytes = emptyNamedTagType + nameBytes + emptyNamedTagValue
            val decodedNbtTag = emptyNamedNbt.decodeFromByteArray<NbtTag>(namedTagBytes.toByteArray())

            assertEquals(nbtTag, decodedNbtTag)
        }
    }

    @Test
    fun unnamed_variant_should_write_the_same_as_Java_with_no_name() = runTest {
        val variants = nbtVariantEdgeCases
            .filterIsInstance<NbtVariant.JavaNetwork>()
            .filter { it.isUnnamedVersion }

        checkAll(
            variants.exhaustive(),
            nbtTagEdgeCases.exhaustive()
        ) { variant, nbtTag ->
            val javaBytes = javaNbt
                .encodeToByteArray(
                    buildNbtCompound { put("", nbtTag) }
                )

            val tagTypeByte = javaBytes.take(1)
            val tagValueBytes = javaBytes.drop(3)
            val expectedBytes = (tagTypeByte + tagValueBytes).toByteArray()

            val actualBytes = javaNetworkNbt(variant)
                .encodeToByteArray(nbtTag)

            assertContentEquals(expectedBytes, actualBytes)
        }
    }

    @Test
    fun unnamed_variant_should_read_correctly() = runTest {
        val variants = nbtVariantEdgeCases
            .filterIsInstance<NbtVariant.JavaNetwork>()
            .filter { it.isUnnamedVersion }

        checkAll(
            variants.exhaustive(),
            nbtTagEdgeCases.exhaustive()
        ) { variant, nbtTag ->
            val bytes = javaNetworkNbt(variant).encodeToByteArray(nbtTag)

            val source = bytes.asSource().buffer()
            val decoded = javaNetworkNbt(variant).decodeFromBufferedSource<NbtTag>(source)

            assertTrue(source.exhausted(), "Source was not exhausted")
            assertEquals(nbtTag, decoded)
        }
    }
}
