package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.NbtCompound
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.internal.NbtTagType.TAG_Compound
import net.benwoodworth.knbt.put
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfEncoderVerifyingNbt
import net.benwoodworth.knbt.test.parameters.parameterOfNbtTagSubtypeEdgeCases
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NbtWriterEncoderTest {
    @Test
    fun named_root_formats_should_throw_when_encoding_a_tag_that_is_not_a_compound_with_one_entry() = parameterizeTest {
        val nbt by parameterOfEncoderVerifyingNbt(includeNamedRootNbt = true)
        assume(nbt.capabilities.namedRoot)

        val tag by parameter {
            sequence {
                yieldAll(
                    this@parameterizeTest.parameterOfNbtTagSubtypeEdgeCases().arguments
                        .filter { it !is NbtCompound }
                )

                yield(buildNbtCompound { /* no entries */ })

                yield(buildNbtCompound { put("1", 1); put("2", 2) })
            }
        }

        val failure = assertFailsWith<NbtEncodingException> {
            nbt.verifyEncoder(NbtTag.serializer(), tag, tag)
        }

        assertEquals("The ${nbt.name} format only supports $TAG_Compound with one entry", failure.message)
    }
}
