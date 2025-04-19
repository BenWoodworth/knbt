package net.benwoodworth.knbt.external

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.Nbt
import net.benwoodworth.knbt.NbtNamed
import kotlin.test.Test
import net.benwoodworth.knbt.NbtTag
import net.benwoodworth.knbt.encodeToNbtTag
import net.benwoodworth.knbt.NbtType.TAG_End
import net.benwoodworth.knbt.NbtType.TAG_Compound
import net.benwoodworth.knbt.NbtType.TAG_List
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.SerializableTypeEdgeCase
import net.benwoodworth.knbt.test.parameters.parameterOfVerifyingNbt
import kotlin.test.assertFails
import kotlin.test.fail

class NullSerializerTest {
    companion object {
        private val NbtEnd: NbtNamed<NbtTag>? = null

        val nullSerializer = serializer<Int?>()

//        val nullType = SerializableTypeEdgeCase(
//            "Null",
//            nullSerializer.descriptor,
//            encodeValue = { encodeSerializableValue(nullSerializer, null) },
//            decodeValue = { decodeSerializableValue(nullSerializer) },
//            valueTag =
//        )

        object NullSerializer
    }

    /**
     * The [NbtTag] hierarchy doesn't include [TAG_End] since [TAG_Compound]s and [TAG_List]s aren't allowed to contain
     * it. Because of that, building an API around
     * makes
     *
     * TODO
     */
    @Test
    fun should_serialize_as_a_root_TAG_End() = parameterizeTest {
        val nbt by parameterOfVerifyingNbt()
        assume(TAG_End in nbt.capabilities.rootTagTypes)

        val serializer = serializer<Nothing?>()

        nbt.verifyEncoderOrDecoder(
            serializer,
            value = null,
            encodedTag = NbtEnd
        )
    }

    @Test
    fun should_fail_when_encoding_as_a_compound_entry() = parameterizeTest {
        @Serializable
        class Compound(val entry: Nothing?)

//        val failure = assertFails {
            val tag = Nbt.encodeToNbtTag(Compound(null))
//            fail(tag.toString())
//        }
    }

    @Test
    fun should_fail_when_encoding_as_a_list_entry() = parameterizeTest {
//        val failure = assertFails {
            val tag = Nbt.encodeToNbtTag(listOf(null))
//            fail(tag.toString())
//        }
    }
}
