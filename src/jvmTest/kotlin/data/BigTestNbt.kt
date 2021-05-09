package data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.buildNbt
import net.benwoodworth.knbt.getResourceAsStream
import net.benwoodworth.knbt.tag.*
import java.util.zip.GZIPInputStream

val bigTestTag = buildNbt("Level") {
    put("longTest", 9223372036854775807L)

    put("shortTest", 32767.toShort())

    put("stringTest", "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!")

    put("floatTest", 0.49823147058486938f)

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

    putNbtList<NbtLong>("listTest (long)") {
        add(11L)
        add(12L)
        add(13L)
        add(14L)
        add(15L)
    }

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

    put("doubleTest", 0.49312871321823148)
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

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Level

            if (longTest != other.longTest) return false
            if (shortTest != other.shortTest) return false
            if (stringTest != other.stringTest) return false
            if (floatTest != other.floatTest) return false
            if (intTest != other.intTest) return false
            if (nestedCompoundTest != other.nestedCompoundTest) return false
            if (listTestLong != other.listTestLong) return false
            if (listTestCompound != other.listTestCompound) return false
            if (byteTest != other.byteTest) return false
            if (!byteArrayTest.contentEquals(other.byteArrayTest)) return false
            if (doubleTest != other.doubleTest) return false

            return true
        }

        override fun hashCode(): Int {
            var result = longTest.hashCode()
            result = 31 * result + shortTest
            result = 31 * result + stringTest.hashCode()
            result = 31 * result + floatTest.hashCode()
            result = 31 * result + intTest
            result = 31 * result + nestedCompoundTest.hashCode()
            result = 31 * result + listTestLong.hashCode()
            result = 31 * result + listTestCompound.hashCode()
            result = 31 * result + byteTest
            result = 31 * result + byteArrayTest.contentHashCode()
            result = 31 * result + doubleTest.hashCode()
            return result
        }
    }
}

val bigTestClass = BigTestNbt(
    level = BigTestNbt.Level(
        longTest = 9223372036854775807L,
        shortTest = 32767,
        stringTest = "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!",
        floatTest = 0.49823147058486938f,
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
        doubleTest = 0.49312871321823148,
    )
)

val bigTestBytesDecompressed = getResourceAsStream("/bigtest.nbt")
    .use { GZIPInputStream(it).readBytes().asList() }

val bigTestBytes = getResourceAsStream("/bigtest.nbt")
    .use { it.readBytes().asList() }

val bigTestBytesZlib = getResourceAsStream("/bigtest-zlib.nbt")
    .use { it.readBytes().asList() }
