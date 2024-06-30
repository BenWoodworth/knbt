package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.NbtCompression

fun ParameterizeScope.parameterOfNbtCompressions() = parameter {
    sequenceOf(
        NbtCompression.None,
        NbtCompression.Gzip,
        NbtCompression.Zlib
    )
}
