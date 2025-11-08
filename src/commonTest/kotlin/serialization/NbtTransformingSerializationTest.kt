/*
 * NOTICE: Modified from `JsonTransformingSerializerTest` in kotlinx.serialization v1.5.0.
 *
 * Copyright 2017-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license.
 */

package net.benwoodworth.knbt.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.serializer
import net.benwoodworth.knbt.*
import kotlin.test.Test
import kotlin.test.assertEquals

class JsonTransformingSerializationTest {
    @Serializable
    @SerialName("StringData")
    private data class StringData(val data: String)

    @Serializable
    @SerialName("Example")
    private data class Example(
        val name: String,
        @Serializable(UnwrappingJsonListSerializer::class) val data: StringData,
        @SerialName("more_data") @Serializable(WrappingJsonListSerializer::class) val moreData: List<StringData> = emptyList()
    )

    private object WrappingJsonListSerializer :
        NbtTransformingSerializer<List<StringData>>(ListSerializer(StringData.serializer())) {
        @OptIn(UnsafeNbtApi::class)
        override fun transformDeserialize(tag: NbtTag): NbtTag =
            tag as? NbtList<*> ?: NbtList(listOf(tag))
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
            NbtCompound(tag.nbtCompound.filterNot { (k, v) -> k == "name" && v.nbtString.value == "First" })
    }

    @Test
    fun testExampleCanBeParsed() {
        val testDataInput = listOf(
            """{Example:{name:"test",data:{data:"str1"},more_data:[{data:"str2"}]}}""",
            """{Example:{name:"test",data:{data:"str1"},more_data:{data:"str2"}}}""",
            """{Example:{name:"test",data:[{data:"str1"}],more_data:[{data:"str2"}]}}""",
            """{Example:{name:"test",data:[{data:"str1"}],more_data:{data:"str2"}}}"""
        )
        val goldenVal = Example("test", StringData("str1"), listOf(StringData("str2")))


        for (i in testDataInput.indices) {
            assertEquals(
                goldenVal,
                StringifiedNbt.decodeFromString(Example.serializer(), testDataInput[i]),
                "failed test on ${testDataInput[i]}"
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
            """{Example:{data:{data:"str1"}}}""",
            """{Example:{name:"Second",data:{data:"str1"}}}"""
        )
        for (i in testDataInput.indices) {
            assertEquals(
                goldenVals[i],
                StringifiedNbt.encodeToString(DroppingNameSerializer, testDataInput[i]),
                "failed test on ${testDataInput[i]}"
            )
        }
    }

    @Serializable
    @SerialName("DocExample")
    private data class DocExample(
        @Serializable(DocJsonListSerializer::class)
        val data: String
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
        assertEquals(
            correctExample,
            StringifiedNbt.decodeFromString(DocExample.serializer(), """{DocExample:{data:["str1"]}}""")
        )
        assertEquals(
            correctExample,
            StringifiedNbt.decodeFromString(DocExample.serializer(), """{DocExample:{data:"str1"}}""")
        )
    }
}
