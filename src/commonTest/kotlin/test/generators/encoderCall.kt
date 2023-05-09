package net.benwoodworth.knbt.test.generators

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.CompositeEncoder
import net.benwoodworth.knbt.NbtTag

data class EncodeValueCall(
    val descriptor: SerialDescriptor,
    val value: Any,
    val nbtTag: NbtTag,
    val call: CompositeEncoder.(descriptor: SerialDescriptor, index: Int) -> Unit,
)

data class EncodeElementCall(
    val descriptor: SerialDescriptor,
    val value: Any,
    val nbtTag: NbtTag,
    val call: CompositeEncoder.(descriptor: SerialDescriptor, index: Int) -> Unit,
)
//
//fun Arb.Companion.encodeElementCall(): Arb<EncodeElementCall> = arbitrary {
//
////    encodeBooleanElement
////    encodeByteElement
////    encodeShortElement
////    encodeCharElement
////    encodeIntElement
////    encodeLongElement
////    encodeFloatElement
////    encodeDoubleElement
////    encodeStringElement
//}
