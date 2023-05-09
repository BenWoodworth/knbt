package net.benwoodworth.knbt.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.elementNames
import net.benwoodworth.knbt.NbtArray
import net.benwoodworth.knbt.NbtNamed

internal enum class NbtListKind { List, ByteArray, IntArray, LongArray }

@OptIn(ExperimentalSerializationApi::class, ExperimentalUnsignedTypes::class)
private fun SerialDescriptor.getNbtListKind(
    additionalAnnotations: List<Annotation> = emptyList()
): NbtListKind {
    when (this) {
        ByteArraySerializer().descriptor -> return NbtListKind.ByteArray
        IntArraySerializer().descriptor -> return NbtListKind.IntArray
        LongArraySerializer().descriptor -> return NbtListKind.LongArray

        UByteArraySerializer().descriptor -> return NbtListKind.ByteArray
        UIntArraySerializer().descriptor -> return NbtListKind.IntArray
        ULongArraySerializer().descriptor -> return NbtListKind.LongArray
    }

    val hasNbtArrayAnnotation = annotations.any { it is NbtArray } || additionalAnnotations.any { it is NbtArray }
    if (!hasNbtArrayAnnotation) return NbtListKind.List

    if (elementsCount == 0) throw NbtException(
        "$serialName has @NbtArray and zero elements, but one is required for determining array type"
    )

    return when (val elementKind = getElementDescriptor(0).kind) {
        PrimitiveKind.BYTE -> NbtListKind.ByteArray
        PrimitiveKind.INT -> NbtListKind.IntArray
        PrimitiveKind.LONG -> NbtListKind.LongArray

        else -> throw NbtException(
            "$serialName has @NbtArray with element kind $elementKind, but BYTE, INT, or LONG is required"
        )
    }
}

internal val SerialDescriptor.nbtListKind: NbtListKind
    get() = getNbtListKind()

@OptIn(ExperimentalSerializationApi::class)
internal fun SerialDescriptor.getElementNbtListKind(index: Int): NbtListKind =
    getElementDescriptor(index).getNbtListKind(getElementAnnotations(index))


@OptIn(ExperimentalSerializationApi::class)
internal val SerialDescriptor.nbtNamed: String?
    get() = annotations
        .firstOrNull { it is NbtNamed }
        ?.let { it as NbtNamed }
        ?.name

@OptIn(ExperimentalSerializationApi::class)
internal inline fun SerialDescriptor.checkPolymorphicStructure(onError: (message: String) -> Unit) {
    val message = when {
        elementsCount != 2 -> {
            val elements = elementNames.toList()
            "Expected polymorphic structure 2 elements named 'type' and 'value', but got $elementsCount: $elements"
        }

        getElementName(0) != "type" ->
            "Expected polymorphic structure element 0 to be named 'type', but got '${getElementName(0)}'"

        getElementDescriptor(0).kind != PrimitiveKind.STRING ->
            "Expected polymorphic structure 'type' to have kind STRING, but got ${getElementDescriptor(0).kind}"

        getElementName(1) != "value" ->
            "Expected polymorphic structure element 1 to be named 'value', but got '${getElementName(1)}'"

        getElementDescriptor(1).kind != SerialKind.CONTEXTUAL ->
            "Expected polymorphic structure 'value' to have kind CONTEXTUAL, but got ${getElementDescriptor(1).kind}"

        else -> null
    }

    if (message != null) onError(message)
}
