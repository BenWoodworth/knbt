# knbt

[![Maven Central](https://img.shields.io/maven-central/v/net.benwoodworth.knbt/knbt)](https://search.maven.org/artifact/net.benwoodworth.knbt/knbt)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/net.benwoodworth.knbt/knbt?server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/content/repositories/snapshots/net/benwoodworth/knbt/knbt/)
[![KDoc](https://img.shields.io/badge/api-KDoc-blue)](https://benwoodworth.github.io/knbt)
[![Kotlin](https://img.shields.io/badge/kotlin-1.5.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![kotlinx.serialization](https://img.shields.io/badge/kotlinx.serialization-1.2.0-blue.svg?logo=kotlin)](https://github.com/Kotlin/kotlinx.serialization)

An implementation of [Minecraft's NBT format](https://minecraft.fandom.com/wiki/NBT_format)
for [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization).

Technical information about NBT can be found [here](https://wiki.vg/NBT).

### Features

- Kotlin Multiplatform: JVM, JS, Linux, Windows, macOS, iOS, watchOS
- Supports all NBT variants: Java, Bedrock Files, Bedrock Network
- Supports all NBT compressions: gzip, zlib
- Type-safe NbtTag classes, and convenient builder DSLs

## Serialization

`Nbt` and `StringifiedNbt` instances can be used to encode/decode `@Serializable` data.

When serializing to/from binary NBT, the resulting tag must be a compound with a single entry (as per the NBT spec).

### Configuration

```kotlin
import net.benwoodworth.knbt.*

// variant and compression are required
val nbt = Nbt {
    variant = NbtVariant.Java // Java, Bedrock, BedrockNetwork
    compression = NbtCompression.None // None, Gzip, Zlib
    compressionLevel = null // in 0..9
    encodeDefaults = false
    ignoreUnknownKeys = false
    serializersModule = EmptySerializersModule
}

val snbt = StringifiedNbt {
    prettyPrint = false
    prettyPrintIndent = "    "
    encodeDefaults = false
    ignoreUnknownKeys = false
    serializersModule = EmptySerializersModule
}
```

### Encoding and Decoding

```kotlin
// ByteArray
byteArray = nbt.encodeToByteArray(value)
value = nbt.decodeFromByteArray(byteArray)

// NbtTag
nbtTag = nbt.encodeToNbtTag(value)
value = nbt.decodeFromNbtTag(nbtTag)

// Okio Sink/Source (Multiplatform)
nbt.encodeToSink(value, sink)
value = nbt.decodeFromSource(source)

// OutputStream/InputStream (JVM)
nbt.encodeToStream(value, outputStream)
value = nbt.decodeFromStream(inputStream)

// String
string = snbt.encodeToString(value)
value = snbt.decodeFromString(string)
```

### Serializing Classes

Serializable classes will have the class's `@SerialName` as the root tag's name.

```kotlin
@Serializable
@SerialName("root")
class Example(val string: String, val int: Int)

// Serializes to: {root : {string : "Hello, world!", int : 42}}
nbt.encodeToNbtTag(Example(string = "Hello, World!", int = 42))
```

### Reading/Writing NBT Files (JVM)

```kotlin
import kotlin.io.path.*
import net.benwoodworth.knbt.*

val file = Path("file.nbt")

val nbt = Nbt {
    TODO()
}

// Read from file
val tag: NbtTag = file.inputStream().use { input ->
    nbt.decodeFromStream(input)
}

// Write to file
file.outputStream().use { output ->
    nbt.encodeToStream(tag, output)
}
```

## NbtTag classes

```kotlin
sealed interface NbtTag

class NbtByte : NbtTag
class NbtShort : NbtTag
class NbtInt : NbtTag
class NbtLong : NbtTag
class NbtFloat : NbtTag
class NbtDouble : NbtTag
class NbtString : NbtTag
class NbtByteArray : NbtTag, List<Byte>
class NbtIntArray : NbtTag, List<Int>
class NbtLongArray : NbtTag, List<Long>
class NbtList<T : NbtTag> : NbtTag, List<T> // Only contains entries of a single type
class NbtCompound : NbtTag, Map<String, NbtTag>
```

`NbtTag`s can be created with constructors and builder functions:

```kotlin
val nbtByte = NbtByte(5)

val nbtIntArray = NbtIntArray(intArrayOf(1, 2, 3, 4, 5))

val nbtListOfStrings = buildNbtList {
    add("these")
    add("are")
    add("strings")
}

val nbtCompound = buildNbtCompound {
    put("int", 1)
    put("string", ":)")
    put("byteArray", byteArrayOf(1, 1, 2, 3, 5, 8))
}

// bigtest.nbt (https://wiki.vg/NBT#bigtest.nbt)
val bigtest = buildNbtCompound("Level") {
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
    putNbtList("listTest (long)") {
        add(11L)
        add(12L)
        add(13L)
        add(14L)
        add(15L)
    }
    putNbtList("listTest (compound)") {
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
```

# Setup

Using the same version of kotlinx.serialization is recommended since parts of its API required for custom formats are
still experimental, and newer versions may have binary-incompatible changes that could break knbt's implementation.

### Upgrading knbt

While in beta, all new minor releases (v0.**#**.0) will have breaking API/functionality changes. Read
the [release notes](https://github.com/BenWoodworth/knbt/releases) for information.

Replacement refactorings will be provided where possible for broken APIs. Change the minor version one at a time
(e.g. 0.1.0 -> 0.2.0 -> 0.3.0) and apply quick fixes. Deprecated APIs will then be removed in 0.#.1 releases.

### Gradle

```kotlin
plugins {
    kotlin("jvm") version "1.5.0" // or kotlin("multiplatform"), etc.
    //kotlin("plugin.serialization") version "1.5.0"
}

repositories {
    mavenCentral()
    //maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    implementation("net.benwoodworth.knbt:knbt:$knbt_version")
    //implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.2.0")
}
```
