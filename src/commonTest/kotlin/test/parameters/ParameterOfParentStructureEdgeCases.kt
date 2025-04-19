package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import net.benwoodworth.knbt.NbtArray
import net.benwoodworth.knbt.NbtByte
import net.benwoodworth.knbt.NbtByteArray
import net.benwoodworth.knbt.NbtInt
import net.benwoodworth.knbt.NbtIntArray
import net.benwoodworth.knbt.NbtLong
import net.benwoodworth.knbt.NbtLongArray
import net.benwoodworth.knbt.buildNbtCompound
import net.benwoodworth.knbt.buildNbtList
import net.benwoodworth.knbt.test.serializers.ListSerializerWithAnnotations

fun ParameterizeScope.parameterOfNestedSerializableTypeEdgeCases(
    nestedType: SerializableTypeEdgeCase
) = parameter {
    sequence {
        val nestedSerializer = nestedType.serializer()

        yield(run {
            @Serializable
            class ClassWithValue<T>(val value: T)

            val serializer = ClassWithValue.serializer(nestedSerializer)

            SerializableTypeEdgeCase(
                "$nestedType nested in Class",
                serializer.descriptor,
                encodeValue = { encodeSerializableValue(serializer, ClassWithValue(Unit)) },
                decodeValue = { decodeSerializableValue(serializer) },
                buildNbtCompound { put("value", nestedType.valueTag) }
            )
        })

        yield(run {
            val serializer = MapSerializer(String.serializer(), nestedSerializer)

            SerializableTypeEdgeCase(
                "$nestedType nested in Map",
                serializer.descriptor,
                encodeValue = { encodeSerializableValue(serializer, mapOf("value" to Unit)) },
                decodeValue = { decodeSerializableValue(serializer) },
                buildNbtCompound { put("value", nestedType.valueTag) }
            )
        })

        yield(run {
            val listSerializer = ListSerializer(nestedSerializer)

            SerializableTypeEdgeCase(
                "$nestedType nested in List",
                listSerializer.descriptor,
                encodeValue = { encodeSerializableValue(listSerializer, listOf(Unit)) },
                decodeValue = { decodeSerializableValue(listSerializer) },
                buildNbtList { add(nestedType.valueTag) }
            )
        })

        yield(run {
            val listSerializer = ListSerializer(nestedSerializer)

            SerializableTypeEdgeCase(
                "$nestedType nested in List",
                listSerializer.descriptor,
                encodeValue = { encodeSerializableValue(listSerializer, listOf(Unit)) },
                decodeValue = { decodeSerializableValue(listSerializer) },
                buildNbtList { add(nestedType.valueTag) }
            )
        })

        val nbtArray = when (nestedType.valueTag) {
            is NbtByte -> NbtByteArray(listOf(nestedType.valueTag.value))
            is NbtInt -> NbtIntArray(listOf(nestedType.valueTag.value))
            is NbtLong -> NbtLongArray(listOf(nestedType.valueTag.value))
            else -> null
        }

        if (nbtArray != null) {
            yield(run {
                val nbtArraySerializer = ListSerializerWithAnnotations(nestedSerializer, listOf(NbtArray()))

                SerializableTypeEdgeCase(
                    "$nestedType nested in @NbtArray List",
                    nbtArraySerializer.descriptor,
                    encodeValue = { encodeSerializableValue(nbtArraySerializer, listOf(Unit)) },
                    decodeValue = { decodeSerializableValue(nbtArraySerializer) },
                    buildNbtList { add(nestedType.valueTag) }
                )
            })
        }
    }
}
