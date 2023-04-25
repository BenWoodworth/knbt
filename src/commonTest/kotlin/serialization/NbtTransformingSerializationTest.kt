/*
 * NOTICE: Modified from `JsonTransformingSerializerTest` in kotlinx.serialization v1.5.0.
 *
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package net.benwoodworth.knbt.serialization

import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import net.benwoodworth.knbt.*
import kotlin.test.*

class JsonTransformingSerializationTest : SerializationTest() {
    @Serializable
    private data class StringData(val data: String)

    @Serializable
    private data class Example(
        val name: String,
        @Serializable(UnwrappingJsonListSerializer::class) val data: StringData,
        @SerialName("more_data") @Serializable(WrappingJsonListSerializer::class) val moreData: List<StringData> = emptyList()
    )

    private object WrappingJsonListSerializer :
        NbtTransformingSerializer<List<StringData>>(ListSerializer(StringData.serializer())) {
        @OptIn(UnsafeNbtApi::class)
        override fun transformDeserialize(tag: NbtTag): NbtTag =
            if (tag !is NbtList<*>) NbtList(listOf(tag)) else tag
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
            NbtCompound(tag.nbtCompound.content.filterNot { (k, v) -> k == "name" && v.stringOrNull == "First" })
    }

    @Test
    fun testExampleCanBeParsed() {
        val testDataInput = listOf(
            """{"name":"test","data":{"data":"str1"},"more_data":[{"data":"str2"}]}""",
            """{"name":"test","data":{"data":"str1"},"more_data":{"data":"str2"}}""",
            """{"name":"test","data":[{"data":"str1"}],"more_data":[{"data":"str2"}]}""",
            """{"name":"test","data":[{"data":"str1"}],"more_data":{"data":"str2"}}"""
        )
        val goldenVal = Example("test", StringData("str1"), listOf(StringData("str2")))


        for (i in testDataInput.indices) {
            defaultNbt.testDecoding(
                Example.serializer(),
                StringifiedNbt.decodeFromString(testDataInput[i]),
                goldenVal
            )
        }
    }

    @Test
    fun testExampleDroppingNameSerializer() {
        val testDataInput = listOf(
            Example("First", StringData("str1")),
            Example("Second", StringData("str1"))
        )

        val goldenVals = listOf(
            """{"data":{"data":"str1"}}""",
            """{"name":"Second","data":{"data":"str1"}}"""
        )
        for (i in testDataInput.indices) {
            defaultNbt.testEncoding(
                DroppingNameSerializer,
                testDataInput[i],
                StringifiedNbt.decodeFromString(goldenVals[i])
            )
        }
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
    fun testDocumentationSample() {
        val correctExample = DocExample("str1")
        defaultNbt.testDecoding(DocExample.serializer(), StringifiedNbt.decodeFromString("""{"data":["str1"]}"""), correctExample)
        defaultNbt.testDecoding(DocExample.serializer(), StringifiedNbt.decodeFromString("""{"data":"str1"}"""), correctExample)
    }
}
