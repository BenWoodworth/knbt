package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
import net.benwoodworth.knbt.test.parameters.parameterOfSerializableTypeEdgeCases
import net.benwoodworth.knbt.test.parameters.serializer
import net.benwoodworth.knbt.test.withNbtName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NbtFormatConfigurationTest {
    @Serializable
    @NbtName("")
    private data class TestData(
        val a: Int,
        val b: Int,
    )

    @Test
    fun should_ignore_unknown_key() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt { ignoreUnknownKeys = true }

        val entries = mutableListOf<Pair<String, NbtTag>>(
            "a" to NbtInt(1),
            "b" to NbtInt(2),
        )

        val unknownKey = "unknown" to NbtString("value")
        val unknownKeyIndex by parameter(0..entries.size)

        entries.add(unknownKeyIndex, unknownKey)
        val tag = buildNbtCompound("") {
            entries.forEach { (name, value) ->
                put(name, value)
            }
        }

        nbt.verifyDecoder(
            TestData.serializer(),
            tag,
            testDecodedValue = { decodedValue ->
                assertEquals(TestData(1, 2), decodedValue)
            }
        )
    }

    @Test
    fun should_throw_for_unknown_keys_if_not_permitted() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt { ignoreUnknownKeys = false }

        val tag = buildNbtCompound("") {
            put("a", 1)
            put("b", 2)
            put("unknown", "value")
        }

        assertFailsWith<SerializationException> {
            nbt.verifyDecoder(
                TestData.serializer(),
                tag
            )
        }
    }

    @Test
    fun should_throw_for_mismatched_root_name() = parameterizeTest(recordFailures = 100) {
        val rootName = "expected_root_name"
        val encodedRootName = "encoded_root_name"

        val nbt by parameterOfDecoderVerifyingNbt()
        assume(nbt.capabilities.namedRoot)

        val serializableType by parameterOfSerializableTypeEdgeCases()

        val failure = assertFailsWith<NbtDecodingException> {
            nbt.verifyDecoder(
                serializableType.serializer().withNbtName(rootName),
                NbtNamed(encodedRootName, serializableType.valueTag),
            )
        }

        assertEquals(
            "Encountered root NBT name '$encodedRootName', but expected '$rootName'.\n" +
                    "Use '${NbtFormatBuilder::lenientNbtNames.name} = true' in NBT builder to ignore mismatched names.",
            failure.message,
            "message"
        )
    }

    @Test
    fun should_not_throw_for_mismatched_root_name_in_unnamed_formats() = parameterizeTest {
        val rootName = "expected_root_name"
        val encodedRootName = "encoded_root_name"

        val nbt by parameterOfDecoderVerifyingNbt()
        assume(!nbt.capabilities.namedRoot)

        val serializableType by parameterOfSerializableTypeEdgeCases()

        nbt.verifyDecoder(
            serializableType.serializer().withNbtName(rootName),
            NbtNamed(encodedRootName, serializableType.valueTag),
        )
    }

    @Test
    fun should_not_throw_for_mismatched_root_name_with_lenient_nbt_names() = parameterizeTest {
        val rootName = "expected_root_name"
        val encodedRootName = "encoded_root_name"

        val nbt by parameterOfDecoderVerifyingNbt { lenientNbtNames = true }
        val serializableType by parameterOfSerializableTypeEdgeCases()

        nbt.verifyDecoder(
            serializableType.serializer().withNbtName(rootName),
            NbtNamed(encodedRootName, serializableType.valueTag),
        )
    }
}
