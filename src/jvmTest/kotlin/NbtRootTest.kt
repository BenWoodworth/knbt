package net.benwoodworth.knbt

import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.*
import net.benwoodworth.knbt.tag.put
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSerializationApi::class, ExperimentalNbtApi::class)
class NbtRootTest {
    @Serializable(TestNbtClassSerializer::class)
    @NbtRoot("root-name")
    private data class TestNbtClass(
        val string: String,
        val int: Int,
    )

    private val testNbt = TestNbtClass(
        string = "string",
        int = 42,
    )

    private val testNbtTag = buildNbt("root-name") {
        put("string", "string")
        put("int", 42)
    }

    @Test
    fun Should_encode_correctly() {
        assertEquals(testNbtTag, Nbt.encodeToNbtTag(TestNbtClass.serializer(), testNbt))
    }

    @Test
    fun Should_decode_correctly() {
        assertEquals(testNbt, Nbt.decodeFromNbtTag(TestNbtClass.serializer(), testNbtTag))
    }

    // TODO Move to common once these issues are fixed
    // https://youtrack.jetbrains.com/issue/KT-46739
    // https://youtrack.jetbrains.com/issue/KT-46740
    //region TestNbtClassSerializer
    private object TestNbtClassSerializer : KSerializer<TestNbtClass> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TestNbtClass") {
            annotations = listOf(TestNbtClass::class.annotations.single { it is NbtRoot })
            element("string", String.serializer().descriptor)
            element("int", Int.serializer().descriptor)
        }

        override fun serialize(encoder: Encoder, value: TestNbtClass) {
            encoder.encodeStructure(descriptor) {
                encodeStringElement(descriptor, 0, value.string)
                encodeIntElement(descriptor, 1, value.int)
            }
        }

        override fun deserialize(decoder: Decoder): TestNbtClass {
            lateinit var string: String
            var stringDecoded = false

            var int = 0
            var intDecoded = false

            decoder.decodeStructure(descriptor) {
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        0 -> string = decodeStringElement(descriptor, 0).also { stringDecoded = true }
                        1 -> int = decodeIntElement(descriptor, 1).also { intDecoded = true }
                        CompositeDecoder.DECODE_DONE -> break
                        else -> throw SerializationException("Unknown index: $index")
                    }
                }
            }

            require(stringDecoded && intDecoded)
            return TestNbtClass(string, int)
        }
    }
    //endregion
}
