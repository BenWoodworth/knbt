package net.benwoodworth.knbt.test.generators

import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.map
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.builtins.NothingSerializer
import kotlinx.serialization.descriptors.*
import kotlin.jvm.JvmName

@JvmName("emptySerialDescriptor\$T")
@OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
inline fun <reified T : SerialKind> Exhaustive.Companion.emptySerialDescriptor(): Exhaustive<SerialDescriptor> =
    Exhaustive.serialKind<T>().map { kind: SerialKind ->
        val name = "emptySerialDescriptor<$kind>()"

        when (kind) {
            is PrimitiveKind -> PrimitiveSerialDescriptor(name, kind)
            StructureKind.CLASS -> buildClassSerialDescriptor(name)
            StructureKind.LIST -> listSerialDescriptor(NothingSerializer().descriptor)
            StructureKind.MAP -> mapSerialDescriptor(NothingSerializer().descriptor, NothingSerializer().descriptor)

            is PolymorphicKind,
            SerialKind.CONTEXTUAL,
            SerialKind.ENUM,
            StructureKind.OBJECT,
            -> buildSerialDescriptor(name, kind)
        }
    }

@OptIn(ExperimentalSerializationApi::class)
fun Exhaustive.Companion.emptySerialDescriptor(): Exhaustive<SerialDescriptor> =
    Exhaustive.emptySerialDescriptor<SerialKind>()
