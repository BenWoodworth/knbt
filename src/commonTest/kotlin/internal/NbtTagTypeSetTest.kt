package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.NbtTagType
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class NbtTagTypeSetTest {
    private class TestCase(
        private val description: String,
        val entries: List<NbtTagType>
    ) {
        override fun toString(): String = "$description: $entries"
    }

    private val testCases = buildList {
        add(TestCase("All", NbtTagType.entries))
        add(TestCase("None", NbtTagType.entries))

        NbtTagType.entries.forEach { tagType ->
            add(TestCase("Only $tagType", listOf(tagType)))
        }

        NbtTagType.entries.forEach { tagType ->
            add(TestCase("All except $tagType", NbtTagType.entries - tagType))
        }
    }

    @Test
    fun contains_should_be_correct() = parameterizeTest {
        val testCase by parameter(testCases)
        val element by parameter(NbtTagType.entries)

        val set = NbtTagTypeSet(testCase.entries)

        assertEquals(element in testCase.entries, element in set, "$element in $set")
    }

    @Test
    fun equals_should_be_true_for_same_entries() = parameterizeTest {
        val testCase by parameter(testCases)
        val otherTestCase by parameter(testCases)
        assume(testCase.entries.toSet() == otherTestCase.entries.toSet())

        val set = NbtTagTypeSet(testCase.entries)
        val otherSet = NbtTagTypeSet(testCase.entries)

        assertEquals(set, otherSet)
    }

    @Test
    fun equals_should_be_false_for_different_entries() = parameterizeTest {
        val testCase by parameter(testCases)
        val otherTestCase by parameter(testCases)
        assume(testCase.entries.toSet() != otherTestCase.entries.toSet())

        val set = NbtTagTypeSet(testCase.entries)
        val otherSet = NbtTagTypeSet(otherTestCase.entries)

        assertNotEquals(set, otherSet)
    }

    @Test
    fun hash_code_should_be_the_same_if_equal() = parameterizeTest {
        val testCase by parameter(testCases)
        val otherTestCase by parameter(testCases)

        val set = NbtTagTypeSet(testCase.entries)
        val otherSet = NbtTagTypeSet(otherTestCase.entries)
        assume(set == otherSet)

        assertEquals(set.hashCode(), otherSet.hashCode())
    }

    @Test
    fun hash_code_should_not_be_the_same_if_unequal() = parameterizeTest {
        val testCase by parameter(testCases)
        val otherTestCase by parameter(testCases)

        val set = NbtTagTypeSet(testCase.entries)
        val otherSet = NbtTagTypeSet(otherTestCase.entries)
        assume(set != otherSet)

        assertNotEquals(set.hashCode(), otherSet.hashCode())
    }

    @Test
    fun to_string_should_be_the_same_as_a_set() = parameterizeTest {
        val testCase by parameter(testCases)

        assertEquals(
            LinkedHashSet(testCase.entries.sortedBy { it.id }).toString(),
            NbtTagTypeSet(testCase.entries).toString()
        )
    }
}
