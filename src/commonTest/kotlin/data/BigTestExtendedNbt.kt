package net.benwoodworth.knbt.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.benwoodworth.knbt.*

val bigTestExtendedTag
    get() = buildNbt("Level") {
        put("longTest", 9223372036854775807L)
        put("shortTest", 32767.toShort())
        put("stringTest", "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!")
        put("floatTest", 0.49823147f.fix())
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
        putNbtList<NbtCompound>("listTest (compound)") {
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
        put(
            "intArrayTest (the first 1000 values of (n-500)^3*16, starting with n=0 (-2000000000, ...))",
            IntArray(1000) { n -> ((n - 500) pow 3) * 16 }
        )
        put(
            "longArrayTest (the first 1000 values of (n-500)^5*16, starting with n=0 (-500000000000000, ...))",
            LongArray(1000) { n -> ((n.toLong() - 500) pow 5) * 16 }
        )
    }

@Serializable
data class BigTestExtendedNbt(
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

        @SerialName("intArrayTest (the first 1000 values of (n-500)^3*16, starting with n=0 (-2000000000, ...))")
        val intArrayTest: IntArray,

        @SerialName("longArrayTest (the first 1000 values of (n-500)^5*16, starting with n=0 (-500000000000000, ...))")
        val longArrayTest: LongArray,
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
            if (other == null || this::class != other::class) return false

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
            if (!intArrayTest.contentEquals(other.intArrayTest)) return false
            if (!longArrayTest.contentEquals(other.longArrayTest)) return false

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
            result = 31 * result + intArrayTest.contentHashCode()
            result = 31 * result + longArrayTest.contentHashCode()
            return result
        }
    }
}

fun assertStructureEquals(expected: BigTestExtendedNbt, actual: BigTestExtendedNbt, message: String? = null): Unit =
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
        property("level.intArrayTest") { level.intArrayTest.asList() }
        property("level.longArrayTest") { level.longArrayTest.asList() }
    }

val bigTestExtendedClass
    get() = BigTestExtendedNbt(
        level = BigTestExtendedNbt.Level(
            longTest = 9223372036854775807L,
            shortTest = 32767,
            stringTest = "HELLO WORLD THIS IS A TEST STRING ÅÄÖ!",
            floatTest = 0.49823147f.fix(),
            intTest = 2147483647,
            nestedCompoundTest = BigTestExtendedNbt.Level.NestedCompoundTest(
                ham = BigTestExtendedNbt.Level.NestedCompoundTest.Entry(
                    name = "Hampus",
                    value = 0.75f,
                ),
                egg = BigTestExtendedNbt.Level.NestedCompoundTest.Entry(
                    name = "Eggbert",
                    value = 0.5f,
                )
            ),
            listTestLong = listOf(11L, 12L, 13L, 14L, 15L),
            listTestCompound = listOf(
                BigTestExtendedNbt.Level.ListTestCompoundEntry(
                    name = "Compound tag #0",
                    createdOn = 1264099775885L,
                ),
                BigTestExtendedNbt.Level.ListTestCompoundEntry(
                    name = "Compound tag #1",
                    createdOn = 1264099775885L,
                ),
            ),
            byteTest = 127,
            byteArrayTest = ByteArray(1000) { n -> ((n * n * 255 + n * 7) % 100).toByte() },
            doubleTest = 0.4931287132182315,
            intArrayTest = IntArray(1000) { n -> ((n - 500) pow 3) * 16 },
            longArrayTest = LongArray(1000) { n -> ((n.toLong() - 500) pow 5) * 16 },
        )
    )
