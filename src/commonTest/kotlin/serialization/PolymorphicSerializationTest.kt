package net.benwoodworth.knbt.serialization

import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.decodeFromNbtTag
import net.benwoodworth.knbt.encodeToNbtTag
import net.benwoodworth.knbt.internal.NbtDecodingException
import net.benwoodworth.knbt.internal.NbtEncodingException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PolymorphicSerializationTest : SerializationTest() {
    @Serializable
    private sealed interface Sealed {
        data class A(val a: String) : Sealed
        data class B(val b: Int) : Sealed
    }

    @Test
    fun serializing_polymorphically_should_fail_with_helpful_unsupported_message() {
        val encodingException = assertFailsWith<NbtEncodingException> {
            defaultNbt.encodeToNbtTag<Sealed>(Sealed.A("a"))
        }

        val decodingException = assertFailsWith<NbtDecodingException> {
            defaultNbt.decodeFromNbtTag<Sealed>(NbtByte(4))
        }

        val expectedMessage = "Polymorphic serialization is not yet supported"
        assertEquals(expectedMessage, encodingException.message)
        assertEquals(expectedMessage, decodingException.message)
    }
}
