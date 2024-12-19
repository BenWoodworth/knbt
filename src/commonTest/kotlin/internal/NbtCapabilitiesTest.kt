package net.benwoodworth.knbt.internal

import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.test.NamedAction
import net.benwoodworth.knbt.test.assume
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfSerializableTypeEdgeCases
import net.benwoodworth.knbt.test.parameters.serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class NbtCapabilitiesTest {
    private class NbtWithCapabilities(
        override val name: String,
        override val capabilities: NbtCapabilities
    ) : NbtFormat() {
        override val serializersModule: SerializersModule = Nbt.serializersModule
        override val configuration: NbtFormatConfiguration = Nbt.configuration

        fun <T> encodeToNbtTag(serializer: SerializationStrategy<T>, value: T): NbtNamed<NbtTag> =
            encodeToNbtTagUnsafe(serializer, value)

        fun <T> decodeFromNbtTag(deserializer: DeserializationStrategy<T>, tag: NbtNamed<NbtTag>): T =
            decodeFromNbtTagUnsafe(deserializer, tag)
    }

    @Test
    fun serializing_unsupported_root_type_should_fail() = parameterizeTest {
        val unsupportedType by parameter(NbtTagType.entries)
        val serializableType by parameterOfSerializableTypeEdgeCases()

        assume(serializableType.valueTag.type == unsupportedType)

        val nbt = NbtWithCapabilities(
            "Non-$unsupportedType Root",
            Nbt.capabilities.copy(
                rootTagTypes = NbtTagTypeSet(NbtTagType.entries - unsupportedType)
            )
        )

        val serialize by parameterOf(
            NamedAction("Encode") {
                nbt.encodeToNbtTag(serializableType.serializer(), Unit)
            },
            NamedAction("Decode") {
                val tag = NbtNamed("", serializableType.valueTag)
                nbt.decodeFromNbtTag(serializableType.serializer(), tag)
            }
        )

        val failure = assertFailsWith<NbtException> {
            serialize()
        }

        assertEquals(
            "The ${nbt.name} format does not support root ${serializableType.valueTag.type} values. " +
                    "Supported types: ${nbt.capabilities.rootTagTypes}",
            failure.message
        )
    }
}
