/*
 * NOTICE: Modified from `JsonTransformingSerializerTest` in kotlinx.serialization v1.5.0.
 *
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.test.parameterizeTest
import net.benwoodworth.knbt.test.parameters.parameterOfDecoderVerifyingNbt
import net.benwoodworth.knbt.test.parameters.parameterOfEncoderVerifyingNbt
import net.benwoodworth.knbt.test.reportedAs
import kotlin.test.Test
import kotlin.test.assertEquals

class NbtTransformingSerializerTest {
    @Serializable
    private data class StringData(val data: String)

    @Serializable
    private data class Example(
        val name: String,
        @Serializable(UnwrappingJsonListSerializer::class) val data: StringData,
        @SerialName("more_data") @Serializable(WrappingJsonListSerializer::class) val moreData: List<StringData> = emptyList()
    )

    private object WrappingJsonListSerializer : // TODO JSON instead of NBT
        NbtTransformingSerializer<List<StringData>>(ListSerializer(StringData.serializer())) {
        @OptIn(UnsafeNbtApi::class)
        override fun transformDeserialize(tag: NbtTag): NbtTag =
            // TODO if-then foldable
            if (tag !is NbtList<*>) NbtList(tag.type, listOf(tag)) else tag
    }

    private object UnwrappingJsonListSerializer :
        NbtTransformingSerializer<StringData>(StringData.serializer()) {
        override fun transformDeserialize(tag: NbtTag): NbtTag {
            if (tag !is NbtList<*>) return tag
            require(tag.size == 1) { "Array size must be equal to 1 to unwrap it" }
            return tag[0]
        }
    }

    private object DroppingNameSerializer : NbtTransformingSerializer<Example>(Example.serializer()) {
        override fun transformSerialize(tag: NbtTag): NbtTag =
            NbtCompound(tag.nbtCompound.content.filterNot { (k, v) -> k == "name" && v == NbtString("First") })
    }

    @Test
    fun testExampleCanBeParsed() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt()

        val testDataInput by parameterOf(
            """{"name":"test","data":{"data":"str1"},"more_data":[{"data":"str2"}]}""",
            """{"name":"test","data":{"data":"str1"},"more_data":{"data":"str2"}}""",
            """{"name":"test","data":[{"data":"str1"}],"more_data":[{"data":"str2"}]}""",
            """{"name":"test","data":[{"data":"str1"}],"more_data":{"data":"str2"}}"""
        )
        val goldenVal = Example("test", StringData("str1"), listOf(StringData("str2")))

        nbt.verifyDecoder(
            Example.serializer(),
            StringifiedNbt.decodeFromString<NbtTag>(testDataInput),
            testDecodedValue = { decodedValue ->
                assertEquals(goldenVal, decodedValue)
            }
        )
    }

    @Test
    fun testExampleDroppingNameSerializer() = parameterizeTest {
        val nbt by parameterOfEncoderVerifyingNbt()

        val testData by parameterOf(
            Example("First", StringData("str1")) to """{"data":{"data":"str1"}}""",
            Example("Second", StringData("str1")) to """{"name":"Second","data":{"data":"str1"}}"""
        ).reportedAs(this, "input") { it.first }

        val (input, goldenVal) = testData

        nbt.verifyEncoder(
            DroppingNameSerializer,
            input,
            StringifiedNbt.decodeFromString<NbtTag>(goldenVal)
        )
    }

    @Serializable
    private data class DocExample(
        @Serializable(DocJsonListSerializer::class) val data: String
    )

    private object DocJsonListSerializer :
        NbtTransformingSerializer<String>(serializer()) {
        override fun transformDeserialize(tag: NbtTag): NbtTag {
            if (tag !is NbtList<*>) return tag
            require(tag.size == 1) { "Array size must be equal to 1 to unwrap it" }
            return tag[0]
        }
    }

    @Test
    fun testDocumentationSample() = parameterizeTest {
        val nbt by parameterOfDecoderVerifyingNbt()

        val input by parameterOf(
            """{"data":["str1"]}""",
            """{"data":"str1"}"""
        )

        val correctExample = DocExample("str1")

        nbt.verifyDecoder(
            DocExample.serializer(),
            StringifiedNbt.decodeFromString<NbtTag>(input),
            testDecodedValue = { decodedValue ->
                assertEquals(correctExample, decodedValue)
            }
        )
    }
}
