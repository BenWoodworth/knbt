# knbt
[![Maven Central](https://img.shields.io/maven-central/v/net.benwoodworth.knbt/knbt)](https://search.maven.org/artifact/net.benwoodworth.knbt/knbt)
[![Kotlin](https://img.shields.io/badge/kotlin-1.5.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![kotlinx.serialization](https://img.shields.io/badge/kotlinx.serialization-1.2.0-blue.svg?logo=kotlin)](https://github.com/Kotlin/kotlinx.serialization)

An implementation of [Minecraft's NBT format](https://minecraft.fandom.com/wiki/NBT_format)
for [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).

Technical information about NBT can be found [here](https://wiki.vg/NBT).

Using the same version of kotlinx.serialization is recommended since parts of its API required for custom formats are
still experimental, and newer versions may have binary-incompatible changes that could break knbt's implementation.

## Configuration
```kotlin
val nbt = Nbt {
    encodeDefaults = false
    variant = Java // Java, Bedrock
    compression = None // None, Gzip, Zlib
    serializersModule = EmptySerializersModule
}
```

## Serialization
An `Nbt` instance can be used to encode/decode `@Serializable` data.
When serializing to/from NBT binary, the data must be a structure with a single named element (as per the NBT spec).

```kotlin
// ByteArray
byteArray = nbt.encodeToByteArray(value)
value = nbt.decodeFromByteArray(byteArray)

// NbtTag
nbtTag = nbt.encodeToNbtTag(value)
value = nbt.decodeFromNbtTag(nbtTag)

// Okio Sink/Source (Multiplatform)
nbt.encodeTo(sink, value)
value = nbt.decodeFrom(source)

// OutputStream/InputStream (JVM)
nbt.encodeTo(outputStream, value)
value = nbt.decodeFrom(inputStream)
```

## NbtTag classes

The sealed `NbtTag` interface has the following immutable implementations:
```kotlin
value class NbtByte : NbtTag
value class NbtShort : NbtTag
value class NbtInt : NbtTag
value class NbtLong : NbtTag
value class NbtFloat : NbtTag
value class NbtDouble : NbtTag
value class NbtString : NbtTag
class NbtByteArray : NbtTag, List<Byte>
class NbtIntArray : NbtTag, List<Int>
class NbtLongArray : NbtTag, List<Long>
class NbtList<T : NbtTag> : NbtTag, List<T> // Only contains entries of a single type
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

val nbtCompoundOfInts = mapOf("a" to 1, "b" to 2).toNbtCompound()

// bigtest.nbt (https://wiki.vg/NBT#bigtest.nbt)
val bigtest = buildNbt("Level") {
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
    put("listTest (long)", listOf(11L, 12L, 13L, 14L, 15L).toNbtList())
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
```

# Setup
## Gradle
```kotlin
plugins {
    kotlin("jvm") version "1.5.0" // or kotlin("multiplatform"), etc.
    kotlin("plugin.serialization") version "1.5.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.0")
    implementation("net.benwoodworth.knbt:knbt:$knbt_version")
}
```
