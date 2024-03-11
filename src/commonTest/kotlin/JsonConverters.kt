package net.benwoodworth.knbt

import kotlinx.serialization.json.*

// A lot of knbt's functionality is designed to match kotlinx.serialization's JSON format, so these help ensure that
// serialization behavior is correctly matched

fun NbtTag.toJsonElement(): JsonElement = when (this) {
    is NbtByte -> JsonPrimitive(value)
    is NbtShort -> JsonPrimitive(value)
    is NbtInt -> JsonPrimitive(value)
    is NbtLong -> JsonPrimitive(value)
    is NbtFloat -> JsonPrimitive(value)
    is NbtDouble -> JsonPrimitive(value)

    is NbtString -> JsonPrimitive(value)

    is NbtList<*> -> JsonArray(map { it.toJsonElement() })

    is NbtByteArray -> JsonArray(map { JsonPrimitive(it) })
    is NbtIntArray -> JsonArray(map { JsonPrimitive(it) })
    is NbtLongArray -> JsonArray(map { JsonPrimitive(it) })

    is NbtCompound -> JsonObject(mapValues { (_, value) -> value.toJsonElement() })
}

fun JsonElement.withRootName(name: String): JsonObject =
    buildJsonObject {
        put(name, this@withRootName)
    }
