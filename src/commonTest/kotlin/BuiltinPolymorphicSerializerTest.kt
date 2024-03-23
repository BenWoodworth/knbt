package net.benwoodworth.knbt

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import net.benwoodworth.knbt.BuiltinPolymorphicSerializerTest.ConflictingDiscriminatorTestCase.Companion.conflictingDiscriminatorTestCases
import net.benwoodworth.knbt.BuiltinPolymorphicSerializerTest.PolymorphicTestCase.Companion.polymorphicTestCases
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

@OptIn(ExperimentalSerializationApi::class)
class BuiltinPolymorphicSerializerTest {
    private class PolymorphicTestCase<T>(
        nbtFormat: NbtFormatForParameter,
        serializersModule: SerializersModule,
        classDiscriminator: String,
        val polymorphicSerializer: KSerializer<T>,
        val data: T
    ) {
        val nbt = nbtFormat {
            this.serializersModule = serializersModule
            this.classDiscriminator = classDiscriminator
        }
        val json = Json {
            this.serializersModule = serializersModule
            this.classDiscriminator = classDiscriminator
        }

        override fun toString(): String =
            polymorphicSerializer.descriptor.serialName

        companion object {
            fun ParameterizeScope.polymorphicTestCases(): List<PolymorphicTestCase<*>> {
                val nbtFormat by parameter(nbtFormats)
                val classDiscriminator by parameterOf("typeA", "typeB")

                return listOf(
                    PolymorphicTestCase(
                        nbtFormat,
                        EmptySerializersModule(),
                        classDiscriminator,
                        SealedData.serializer(),
                        ConcreteSealedData("value")
                    ),
                    PolymorphicTestCase(
                        nbtFormat,
                        SerializersModule {
                            polymorphic(OpenData::class, ConcreteOpenData::class, ConcreteOpenData.serializer())
                        },
                        classDiscriminator,
                        OpenData.serializer(),
                        ConcreteOpenData("value")
                    ),
                )
            }
        }

        @Serializable
        @SerialName("SealedData")
        private sealed class SealedData

        @Serializable
        @SerialName("ConcreteSerialName")
        private data class ConcreteSealedData(
            val element: String
        ) : SealedData()


        @Serializable
        @SerialName("OpenData")
        private sealed class OpenData

        @Serializable
        @SerialName("ConcreteSerialName")
        private data class ConcreteOpenData(
            val element: String
        ) : OpenData()
    }

    @Test
    fun should_encode_root_sealed_class_nested_under_polymorphic_serial_name() = parameterizeTest {
        val testCase by parameter(polymorphicTestCases())

        fun <T> PolymorphicTestCase<T>.test() {
            val dataNbt = nbt.encodeToNbtTag(polymorphicSerializer, data)

            assertIs<NbtCompound>(dataNbt)

            val polymorphicSerialName = polymorphicSerializer.descriptor.serialName
            assertEquals(setOf(polymorphicSerialName), dataNbt.keys, "root keys of data nbt: $dataNbt")
        }
        testCase.test()
    }

    @Test
    fun should_encode_sealed_data_the_same_as_json() = parameterizeTest {
        val testCase by parameter(polymorphicTestCases())

        fun <T> PolymorphicTestCase<T>.test() {
            val dataNbt = nbt.encodeToNbtTag(polymorphicSerializer, data)
            val dataJson = json.encodeToJsonElement(polymorphicSerializer, data)

            val polymorphicSerialName = polymorphicSerializer.descriptor.serialName
            assertEquals(dataJson.withRootName(polymorphicSerialName), dataNbt.toJsonElement())
        }
        testCase.test()
    }

    @Test
    fun should_decode_sealed_data() = parameterizeTest {
        val testCase by parameter(polymorphicTestCases())

        fun <T> PolymorphicTestCase<T>.test() {// TODO
            val dataNbt = nbt.encodeToNbtTag(polymorphicSerializer, data)
            val dataDecoded = nbt.decodeFromNbtTag(polymorphicSerializer, dataNbt)

            assertEquals(data, dataDecoded)
        }
        testCase.test()
    }

    @Serializable
    @SerialName("ParentSerialName")
    private data class Parent<T>(val polymorphic: T)

    @Test
    fun should_encode_nested_sealed_data_the_same_as_json() = parameterizeTest {
        val testCase by parameter(polymorphicTestCases())

        fun <T> PolymorphicTestCase<T>.test() {
            val nested = Parent(data)

            val serializer = Parent.serializer(polymorphicSerializer)
            val dataNbt = nbt.encodeToNbtTag(serializer, nested)
            val dataJson = json.encodeToJsonElement(serializer, nested)

            val parentSerialName = serializer.descriptor.serialName
            assertEquals(dataJson.withRootName(parentSerialName), dataNbt.toJsonElement())
        }
        testCase.test()
    }

    @Test
    fun should_decode_nested_sealed_data() = parameterizeTest {
        val testCase by parameter(polymorphicTestCases())

        fun <T> PolymorphicTestCase<T>.test() {
            val nested = Parent(data)

            val serializer = Parent.serializer(polymorphicSerializer)
            val dataNbt = nbt.encodeToNbtTag(serializer, nested)
            val dataDecoded = nbt.decodeFromNbtTag(serializer, dataNbt)

            assertEquals(nested, dataDecoded)
        }
        testCase.test()
    }

    @Test
    fun should_encode_list_nested_sealed_data_the_same_as_json() = parameterizeTest {
        val testCase by parameter(polymorphicTestCases())

        fun <T> PolymorphicTestCase<T>.test() {
            val dataList = listOf(data)

            val serializer = ListSerializer(polymorphicSerializer)
            val dataListNbt = nbt.encodeToNbtTag(serializer, dataList)
            val dataListJson = json.encodeToJsonElement(serializer, dataList)

            assertEquals(dataListJson, dataListNbt.toJsonElement())
        }
        testCase.test()
    }

    @Test
    fun should_decode_list_nested_sealed_data() = parameterizeTest {
        val testCase by parameter(polymorphicTestCases())

        fun <T> PolymorphicTestCase<T>.test() {
            val dataList = listOf(data)

            val serializer = ListSerializer(polymorphicSerializer)
            val dataListNbt = nbt.encodeToNbtTag(serializer, dataList)
            val dataListDecoded = nbt.decodeFromNbtTag(serializer, dataListNbt)

            assertEquals(dataList, dataListDecoded)
        }
        testCase.test()
    }

    @Test
    fun should_encode_map_nested_sealed_data_the_same_as_json() = parameterizeTest {
        val testCase by parameter(polymorphicTestCases())

        fun <T> PolymorphicTestCase<T>.test() {
            val dataMap = mapOf("key" to data)

            val serializer = MapSerializer(String.serializer(), polymorphicSerializer)
            val dataMapNbt = nbt.encodeToNbtTag(serializer, dataMap)
            val dataMapJson = json.encodeToJsonElement(serializer, dataMap)

            assertEquals(dataMapJson, dataMapNbt.toJsonElement())
        }
        testCase.test()
    }

    @Test
    fun should_decode_map_nested_sealed_data() = parameterizeTest {
        val testCase by parameter(polymorphicTestCases())

        fun <T> PolymorphicTestCase<T>.test() {
            val dataMap = mapOf("key" to data)

            val serializer = MapSerializer(String.serializer(), polymorphicSerializer)
            val dataMapNbt = nbt.encodeToNbtTag(serializer, dataMap)
            val dataMapDecoded = nbt.decodeFromNbtTag(serializer, dataMapNbt)

            assertEquals(dataMap, dataMapDecoded)
        }
        testCase.test()
    }

    private class ConflictingDiscriminatorTestCase<T>(
        nbtFormat: NbtFormatForParameter,
        serializersModule: SerializersModule,
        val polymorphicSerializer: SerializationStrategy<T>,
        val concreteSerializer: SerializationStrategy<T>,
        val data: T
    ) {
        val nbt = nbtFormat { this.serializersModule = serializersModule }

        override fun toString(): String =
            polymorphicSerializer.descriptor.serialName

        companion object {
            fun ParameterizeScope.conflictingDiscriminatorTestCases(): List<ConflictingDiscriminatorTestCase<*>> {
                val nbtFormat by parameter(nbtFormats)

                return listOf(
                    ConflictingDiscriminatorTestCase(
                        nbtFormat,
                        EmptySerializersModule(),
                        SealedData.serializer(),
                        ConcreteSealedData.serializer(),
                        ConcreteSealedData("value")
                    ),
                    ConflictingDiscriminatorTestCase(
                        nbtFormat,
                        SerializersModule {
                            polymorphic(OpenData::class, ConcreteOpenData::class, ConcreteOpenData.serializer())
                        },
                        OpenData.serializer(),
                        ConcreteOpenData.serializer(),
                        ConcreteOpenData("value")
                    ),
                )
            }
        }

        @Serializable
        @SerialName("SealedData")
        private sealed class SealedData

        @Serializable
        @SerialName("ConcreteSerialName")
        private data class ConcreteSealedData(
            val type: String
        ) : SealedData()


        @Serializable
        @SerialName("OpenData")
        private sealed class OpenData

        @Serializable
        @SerialName("ConcreteSerialName")
        private data class ConcreteOpenData(
            val type: String
        ) : OpenData()
    }

    @Serializable
    private sealed class SealedDataWithConflictingDiscriminator

    @Serializable
    private data class ConcreteSealedDataWithConflictingDiscriminator(
        val type: Int
    ) : SealedDataWithConflictingDiscriminator()

    private val conflictingSealedDiscriminatorJsonResult = runCatching {
        val data = ConcreteSealedDataWithConflictingDiscriminator(1234)

        Json.encodeToJsonElement(SealedDataWithConflictingDiscriminator.serializer(), data)
    }

    /**
     * With [Json], the non-sealed discriminator conflict *actually* throws when building the configuration, since it's
     * possible to check for the conflict at that time. However, it uses experimental APIs to check, so for now, a
     * [NbtFormat] will throw during serialization like sealed classes instead for now.
     */
    @Test
    fun encoding_bad_data_with_conflicting_class_discriminator_should_have_same_failure_as_json() = parameterizeTest {
        val testCase by parameter(conflictingDiscriminatorTestCases())

        fun <T> ConflictingDiscriminatorTestCase<T>.test() {
            val jsonFailure = checkNotNull(conflictingSealedDiscriminatorJsonResult.exceptionOrNull()) { "jsonFailure" }

            val nbtFailure = assertFailsWith(jsonFailure::class) {
                nbt.encodeToNbtTag(polymorphicSerializer, data)
            }

            val polymorphicClassKind = when (polymorphicSerializer.descriptor.kind as PolymorphicKind) {
                PolymorphicKind.SEALED -> "Sealed"
                PolymorphicKind.OPEN -> "Polymorphic"
            }

            val expectedMessage = checkNotNull(jsonFailure.message) { "Expected JSON failure to have a message" }
                .replace("JSON", "NBT")
                .replace("Sealed class", "$polymorphicClassKind class")
                .replace(
                    SealedDataWithConflictingDiscriminator.serializer().descriptor.serialName,
                    polymorphicSerializer.descriptor.serialName
                )
                .replace(
                    ConcreteSealedDataWithConflictingDiscriminator.serializer().descriptor.serialName,
                    concreteSerializer.descriptor.serialName
                )
                .replace("You can either change class discriminator in JsonConfiguration, rename", "Consider renaming")
                .replace(" or fall back to array polymorphism", "")

            assertEquals(expectedMessage, nbtFailure.message)
        }
        testCase.test()
    }
}
