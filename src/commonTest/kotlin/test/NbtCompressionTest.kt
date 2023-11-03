package net.benwoodworth.knbt.test

import io.kotest.property.Exhaustive
import io.kotest.property.assume
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.of
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToByteArray
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.NbtCompression.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

class NbtCompressionTest {
    val compressionEdgeCases = buildList {
        add(None)

        add(Gzip(null))
        add(Zlib(null))

        (0..9).forEach { level ->
            add(Gzip(level))
            add(Zlib(level))
        }
    }

    val compressionEqualsEdgeCases = buildList {
        addAll(compressionEdgeCases)

        // Different instances
        compressionEdgeCases.forEach { compression ->
            when (compression) {
                is Gzip -> add(Gzip(compression.level))
                is Zlib -> add(Zlib(compression.level))
            }
        }
    }

    val levelEdgeCases = listOf(Int.MIN_VALUE, -1, 0, 9, 10, Int.MAX_VALUE, null)

    @Test
    fun none_should_only_equal_if_other_value_is_none() = runTest {
        checkAll(
            compressionEqualsEdgeCases.exhaustive()
        ) { other ->
            assertEquals(other is None, None == other)
        }
    }

    @Test
    fun gzip_level_should_default_to_null() {
        assertEquals(null, Gzip().level)
    }

    @Test
    fun zlib_level_should_default_to_null() {
        assertEquals(null, Gzip().level)
    }

    @Test
    fun gzip_and_zlib_should_throw_with_invalid_level() = runTest {
        val invalidLevels = levelEdgeCases.filter { it != null && it !in 0..9 }

        checkAll(
            Exhaustive.of(::Gzip, ::Zlib),
            invalidLevels.exhaustive()
        ) { construct, invalidLevel ->
            val failure = assertFailsWith<IllegalArgumentException> {
                construct(invalidLevel)
            }

            assertEquals("Compression level must be in 0..9 or null, but is $invalidLevel", failure.message)
        }
    }

    @Test
    fun gzip_should_only_equal_gzip_with_same_level() = runTest {
        checkAll(
            compressionEdgeCases.filterIsInstance<Gzip>().exhaustive(),
            compressionEqualsEdgeCases.exhaustive()
        ) { gzip, other ->
            assertEquals(other is Gzip && gzip.level == other.level, gzip == other)
        }
    }

    @Test
    fun zlib_should_only_equal_zlib_with_same_level() = runTest {
        checkAll(
            compressionEdgeCases.filterIsInstance<Zlib>().exhaustive(),
            compressionEqualsEdgeCases.exhaustive()
        ) { zlib, other ->
            assertEquals(other is Zlib && zlib.level == other.level, zlib == other)
        }
    }

    @Test
    fun compression_hash_codes_should_be_consistent_with_equals() = runTest {
        checkAll(
            compressionEqualsEdgeCases.exhaustive(),
            compressionEqualsEdgeCases.exhaustive()
        ) { a, b ->
            if (a == b) {
                assertEquals(a.hashCode(), b.hashCode())
            }
        }
    }

    @Test
    fun compression_hash_codes_should_all_be_different() = runTest {
        checkAll(
            compressionEdgeCases.exhaustive(),
            compressionEdgeCases.exhaustive()
        ) { a, b ->
            assume(a != b)

            assertNotEquals(a.hashCode(), b.hashCode())
        }
    }

    @Test
    fun none_string_representation_should_be_its_class_name() {
        assertEquals(None::class.simpleName, None.toString())
    }

    @Test
    fun gzip_string_representation_should_be_class_name_with_level_or_default() = runTest {
        checkAll(
            compressionEdgeCases.filterIsInstance<Gzip>().exhaustive()
        ) { gzip ->
            assertEquals("${Gzip::class.simpleName}(level = ${gzip.level ?: "default"})", gzip.toString())
        }
    }

    @Test
    fun zlib_string_representation_should_be_class_name_with_level_or_default() = runTest {
        checkAll(
            compressionEdgeCases.filterIsInstance<Zlib>().exhaustive()
        ) { zlib ->
            assertEquals("${Zlib::class.simpleName}(level = ${zlib.level ?: "default"})", zlib.toString())
        }
    }

    @Test
    fun gzip_detect_should_detect_with_default_level() = runTest {
        checkAll(
            compressionEdgeCases.filterIsInstance<Gzip>().exhaustive()
        ) { gzip ->
            val nbt = Nbt {
                variant = NbtVariant.Java
                compression = gzip
            }

            val serialized = nbt.encodeToByteArray(buildNbtCompound("name") {  })

            assertEquals(Gzip(null), NbtCompression.detect(serialized))
        }
    }

    @Test
    fun zlib_detect_should_detect_approximate_level() = runTest {
        fun Zlib.expectedDetectLevel(): Int? = when (level ?: 6) {
            in 0..1 -> 1
            in 2..5 -> 3
            6 -> null
            else -> 9
        }

        checkAll(
            compressionEdgeCases.filterIsInstance<Zlib>().exhaustive()
        ) { zlib ->
            val nbt = Nbt {
                variant = NbtVariant.Java
                compression = zlib
            }

            val serialized = nbt.encodeToByteArray(buildNbtCompound("name") {  })

            val expectedLevel = zlib.expectedDetectLevel()
            assertEquals(Zlib(expectedLevel), NbtCompression.detect(serialized))
        }
    }
}


