package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterize
import net.benwoodworth.knbt.*

private fun NbtTag.repeatInNbtList(size: Int): NbtList<*> = when (this) {
    is NbtByte -> NbtList(List(size) { this })
    is NbtShort -> NbtList(List(size) { this })
    is NbtInt -> NbtList(List(size) { this })
    is NbtLong -> NbtList(List(size) { this })
    is NbtFloat -> NbtList(List(size) { this })
    is NbtDouble -> NbtList(List(size) { this })
    is NbtByteArray -> NbtList(List(size) { this })
    is NbtString -> NbtList(List(size) { this })
    is NbtList<*> -> NbtList(List(size) { this })
    is NbtCompound -> NbtList(List(size) { this })
    is NbtIntArray -> NbtList(List(size) { this })
    is NbtLongArray -> NbtList(List(size) { this })
}

/**
 * [NbtList]s of every element type with sizes 0..2
 */
fun ParameterizeScope.parameterOfNbtListContentEdgeCases() = parameter {
    sequence {
        // TAG_End
        yield(NbtList(emptyList()))

        parameterize {
            val entry by parameterOfNbtTagSubtypeEdgeCases()
            val size by parameter(0..2)

            yield(entry.repeatInNbtList(size))
        }
    }
}
