package net.benwoodworth.knbt.test.parameters

import kotlinx.serialization.KSerializer
import net.benwoodworth.knbt.NbtFormat

class SerializeAction(
    private val name: String,
    private val action: NbtFormat.() -> Unit
) {
    operator fun invoke(format: NbtFormat, serializer: KSerializer<Unit>) {

    }
}
