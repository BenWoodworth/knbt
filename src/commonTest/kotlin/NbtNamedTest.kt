package net.benwoodworth.knbt

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtNamedTest {
    private fun ParameterizeScope.parameterOfNbtNamedEdgeCases() = parameterOf(
        NbtNamed("name", "value"),
        NbtNamed("different-name", "value"),
        NbtNamed("name", "different-value"),
    )

    private fun ParameterizeScope.parameterOfEqualsEdgeCases(nbtNamed: NbtNamed<*>) = parameter {
        sequenceOf(
            nbtNamed,
            NbtNamed(nbtNamed.name, nbtNamed.value),
            NbtNamed("different_than_${nbtNamed.name}", nbtNamed.value),
            NbtNamed(nbtNamed.name, "different_than_${nbtNamed.value}"),
            "different_type",
            null
        )
    }

    @Test
    fun equals_should_check_for_type_name_and_value() = parameterizeTest {
        val nbtNamed by parameterOfNbtNamedEdgeCases()
        val other by parameterOfEqualsEdgeCases(nbtNamed)

        assertEquals(
            other is NbtNamed<*> &&
                    nbtNamed.name == (other as NbtNamed<*>).name &&
                    nbtNamed.value == (other as NbtNamed<*>).value,
            nbtNamed == other,
            "$nbtNamed == $other"
        )
    }

    @Test
    fun hash_code_should_be_the_same_if_equal() = parameterizeTest {
        val nbtNamed by parameterOfNbtNamedEdgeCases()
        val otherNbtNamed by parameterOfEqualsEdgeCases(nbtNamed)
        assume(nbtNamed == otherNbtNamed)

        assertEquals(nbtNamed.hashCode(), otherNbtNamed.hashCode(), "$nbtNamed.hashCode() == $otherNbtNamed.hashCode()")
    }

    @Test
    fun to_string_should_match_data_class_string_representation() = parameterizeTest {
        val nbtNamed by parameterOfNbtNamedEdgeCases()

        val similarDataClass = run {
            data class NbtNamed<out T>(val name: String, val value: T)
            NbtNamed(nbtNamed.name, nbtNamed.value)
        }

        assertEquals(similarDataClass.toString(), nbtNamed.toString())
    }
}
