package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.assertStructureEquals
import net.benwoodworth.knbt.buildNbt
import net.benwoodworth.knbt.tag.*
import net.benwoodworth.knbt.toBinary

val bigTestTag = buildNbt("Level") {
    put("longTest", 9223372036854775807L)
    put("shortTest", 32767.toShort())
    put("stringTest", "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!")
    put("floatTest", 0.49823147f)
    put("intTest", 2147483647)
    putNbtCompound("nested compound test") {
        putNbtCompound("ham") {
            put("name", "Hampus")
            put("value", 0.75f)
        }
        putNbtCompound("egg") {
            put("name", "Eggbert")
            put("value", 0.5f)
        }
    }
    put("listTest (long)", listOf(11L, 12L, 13L, 14L, 15L).toNbtList())
    putNbtList<NbtCompound<NbtTag>>("listTest (compound)") {
        addNbtCompound {
            put("name", "Compound tag #0")
            put("created-on", 1264099775885L)
        }
        addNbtCompound {
            put("name", "Compound tag #1")
            put("created-on", 1264099775885L)
        }
    }
    put("byteTest", 127.toByte())
    put(
        "byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))",
        ByteArray(1000) { n -> ((n * n * 255 + n * 7) % 100).toByte() }
    )
    put("doubleTest", 0.4931287132182315)
}

@Serializable
data class BigTestNbt(
    @SerialName("Level")
    val level: Level,
) {
    @Serializable
    data class Level(
        val longTest: Long,

        val shortTest: Short,

        val stringTest: String,

        val floatTest: Float,

        val intTest: Int,

        @SerialName("nested compound test")
        val nestedCompoundTest: NestedCompoundTest,

        @SerialName("listTest (long)")
        val listTestLong: List<Long>,

        @SerialName("listTest (compound)")
        val listTestCompound: List<ListTestCompoundEntry>,

        val byteTest: Byte,

        @SerialName("byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))")
        val byteArrayTest: ByteArray,

        val doubleTest: Double,
    ) {
        @Serializable
        data class NestedCompoundTest(
            val ham: Entry,
            val egg: Entry,
        ) {
            @Serializable
            data class Entry(val name: String, val value: Float)
        }

        @Serializable
        data class ListTestCompoundEntry(
            val name: String,

            @SerialName("created-on")
            val createdOn: Long,
        )
    }
}

fun assertStructureEquals(expected: BigTestNbt, actual: BigTestNbt, message: String? = null): Unit =
    assertStructureEquals(expected, actual, message) {
        property("level.longTest") { level.longTest }
        property("level.shortTest") { level.shortTest }
        property("level.stringTest") { level.stringTest }
        property("level.floatTest") { level.floatTest.toBinary() }
        property("level.intTest") { level.intTest }
        property("level.nestedCompoundTest.ham.name") { level.nestedCompoundTest.ham.name }
        property("level.nestedCompoundTest.ham.value") { level.nestedCompoundTest.ham.value.toBinary() }
        property("level.nestedCompoundTest.egg.name") { level.nestedCompoundTest.egg.name }
        property("level.nestedCompoundTest.egg.value") { level.nestedCompoundTest.egg.value.toBinary() }
        property("level.listTestLong") { level.listTestLong }
        property("level.listTestCompound") { level.listTestCompound }
        property("level.byteTest") { level.byteTest }
        property("level.byteArrayTest") { level.byteArrayTest.asList() }
        property("level.doubleTest") { level.doubleTest.toBinary() }
    }

val bigTestClass = BigTestNbt(
    level = BigTestNbt.Level(
        longTest = 9223372036854775807L,
        shortTest = 32767,
        stringTest = "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!",
        floatTest = 0.49823147f,
        intTest = 2147483647,
        nestedCompoundTest = BigTestNbt.Level.NestedCompoundTest(
            ham = BigTestNbt.Level.NestedCompoundTest.Entry(
                name = "Hampus",
                value = 0.75f,
            ),
            egg = BigTestNbt.Level.NestedCompoundTest.Entry(
                name = "Eggbert",
                value = 0.5f,
            )
        ),
        listTestLong = listOf(11L, 12L, 13L, 14L, 15L),
        listTestCompound = listOf(
            BigTestNbt.Level.ListTestCompoundEntry(
                name = "Compound tag #0",
                createdOn = 1264099775885L,
            ),
            BigTestNbt.Level.ListTestCompoundEntry(
                name = "Compound tag #1",
                createdOn = 1264099775885L,
            ),
        ),
        byteTest = 127,
        byteArrayTest = ByteArray(1000) { n -> ((n * n * 255 + n * 7) % 100).toByte() },
        doubleTest = 0.4931287132182315,
    )
)
