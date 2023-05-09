package net.benwoodworth.knbt.test.generators

import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.exhaustive
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class)
private val serialKinds = listOf(
    PolymorphicKind.OPEN,
    PolymorphicKind.SEALED,

    PrimitiveKind.BOOLEAN,
    PrimitiveKind.BYTE,
    PrimitiveKind.CHAR,
    PrimitiveKind.DOUBLE,
    PrimitiveKind.FLOAT,
    PrimitiveKind.INT,
    PrimitiveKind.LONG,
    PrimitiveKind.SHORT,
    PrimitiveKind.STRING,

    SerialKind.CONTEXTUAL,
    SerialKind.ENUM,

    StructureKind.CLASS,
    StructureKind.LIST,
    StructureKind.MAP,
    StructureKind.OBJECT
)

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalSerializationApi::class)
fun <T : SerialKind> Exhaustive.Companion.serialKind(type: KClass<T>): Exhaustive<T> =
    (serialKinds.filter { type.isInstance(it) } as List<T>).exhaustive()

@JvmName("serialKind\$T")
@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : SerialKind> Exhaustive.Companion.serialKind(): Exhaustive<T> =
    Exhaustive.serialKind(T::class)



