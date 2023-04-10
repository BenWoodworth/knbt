package net.benwoodworth.knbt.internal

import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.*
import net.benwoodworth.knbt.internal.NbtTagType.*
import net.benwoodworth.knbt.test.NbtFormat
import kotlin.test.Test

class NbtWriterEncoderTest {
    @Test
    fun encoding_a_class_should_nest_into_a_compound_with_the_class_serial_name_as_the_key() {
        @Serializable
        @NbtNamed("RootKey")
        data class MyClass(val property: String)

        val myClass = MyClass("value")

        lateinit var encodedTag: NbtTag
        val encoder = NbtWriterEncoder(
            NbtFormat(),
            TreeNbtWriter { encodedTag = it }
        )

        encoder.encodeSerializableValue(MyClass.serializer(), myClass)

        val actualTag = encodedTag
        actualTag.shouldBeInstanceOf<NbtCompound>()
        actualTag shouldHaveSize 1
        actualTag shouldHaveKey "RootKey"
    }
}
