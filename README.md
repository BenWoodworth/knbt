# knbt
Minecraft NBT support for kotlinx.serialization

## NbtTag classes

The sealed `NbtTag` interface has the following immutable implementations:
```kotlin
class NbtByte : NbtTag
class NbtShort : NbtTag
class NbtInt : NbtTag
class NbtLong : NbtTag
class NbtFloat : NbtTag
class NbtDouble : NbtTag
class NbtByteArray : NbtTag, List<Byte>
class NbtIntArray : NbtTag, List<Int>
class NbtLongArray : NbtTag, List<Long>
class NbtString : NbtTag
class NbtList<T : NbtTag> : NbtTag, List<T>
class NbtCompound<T : NbtTag> : NbtTag, Map<String, T>
```

`NbtTag`s can be created with factory/conversion/builder functions:
```kotlin
val nbtByte = 5.toNbtByte()

val nbtIntArray = nbtIntArrayOf(1, 2, 3, 4, 5)

val nbtListOfInts = listOf(1L, 2L, 3L).toNbtList()

val nbtListOfStrings = buildNbtList<NbtString> { 
    add("these")
    add("are")
    add("strings")
}

// bigtest.nbt
val bigtest = buildNbtCompound {
    putNbtCompound("Level") {
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

        putNbtList<NbtCompound<*>>("listTest (compound)") {
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
}
```
