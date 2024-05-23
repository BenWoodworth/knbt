package net.benwoodworth.knbt.test

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import com.benwoodworth.parameterize.parameterOf

import kotlin.math.E
import kotlin.math.PI

fun ParameterizeScope.parameterOfBooleans() =
    parameterOf(true, false)

fun ParameterizeScope.parameterOfBytes() = parameter {
    sequence {
        for (i in Byte.MIN_VALUE..Byte.MAX_VALUE) {
            yield(i.toByte())
        }
    }
}

fun ParameterizeScope.parameterOfShorts() = parameter {
    sequence {
        yield(Short.MIN_VALUE)
        yield(0.toShort())
        yield(Short.MAX_VALUE)
        for (i in -1000..1000 step 7) {
            yield(i.toShort())
        }
    }
}

fun ParameterizeScope.parameterOfInts() = parameter {
    sequence {
        yield(Int.MIN_VALUE)
        yield(0)
        yield(Int.MAX_VALUE)
        for (i in -1000..1000 step 7) {
            yield(i)
        }
    }
}

fun ParameterizeScope.parameterOfLongs() = parameter {
    sequence {
        yield(Long.MIN_VALUE)
        yield(0L)
        yield(Long.MAX_VALUE)
        for (i in -1000..1000 step 7) {
            yield(i.toLong())
        }
    }
}

fun ParameterizeScope.parameterOfFloats() = parameter {
    sequence {
        yield(E.toFloat())
        yield(PI.toFloat())
        yield(Float.MAX_VALUE)
        yield(Float.MIN_VALUE)
        yield(-Float.MAX_VALUE)
        yield(-Float.MIN_VALUE)
        yield(Float.NEGATIVE_INFINITY)
        yield(Float.POSITIVE_INFINITY)
        yield(Float.NaN)
        yield(0.0f)
        yield(-0.0f)
        yield(1.0f)
        yield(-1.0f)
    }
}

fun ParameterizeScope.parameterOfDoubles() = parameter {
    sequence {
        yield(E)
        yield(PI)
        yield(Double.MAX_VALUE)
        yield(Double.MIN_VALUE)
        yield(-Double.MAX_VALUE)
        yield(-Double.MIN_VALUE)
        yield(Double.NEGATIVE_INFINITY)
        yield(Double.POSITIVE_INFINITY)
        yield(Double.NaN)
        yield(0.0)
        yield(-0.0)
        yield(1.0)
        yield(-1.0)
    }
}

fun ParameterizeScope.parameterOfStrings() = parameter {
    sequence {
        yield("")
        yield("String")
        yield("HELLO WORLD THIS IS A TEST STRING ÅÄÖ!")

        yield(buildString {
            for (i in 0..127) append(i.toChar())
        })
    }
}

fun ParameterizeScope.parameterOfByteArrays() = parameter {
    sequence {
        yield(byteArrayOf())
        yield(byteArrayOf(Byte.MIN_VALUE))
        yield(byteArrayOf(Byte.MAX_VALUE))
        yield(ByteArray(256) { it.toByte() })
    }
}

fun ParameterizeScope.parameterOfIntArrays() = parameter {
    sequence {
        yield(intArrayOf())
        yield(intArrayOf(Int.MIN_VALUE))
        yield(intArrayOf(Int.MAX_VALUE))
        yield(IntArray(256) { it })
    }
}

fun ParameterizeScope.parameterOfLongArrays() = parameter {
    sequence {
        yield(longArrayOf())
        yield(longArrayOf(Long.MIN_VALUE))
        yield(longArrayOf(Long.MAX_VALUE))
        yield(LongArray(256) { it.toLong() })
    }
}
