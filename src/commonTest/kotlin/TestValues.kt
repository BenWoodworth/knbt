package net.benwoodworth.knbt

import kotlin.math.E
import kotlin.math.PI

object TestValues {
    val bytes: Sequence<Byte> = sequence {
        for (i in Byte.MIN_VALUE..Byte.MAX_VALUE) {
            yield(i.toByte())
        }
    }

    val shorts: Sequence<Short> = sequence {
        yield(Short.MIN_VALUE)
        yield(0.toShort())
        yield(Short.MAX_VALUE)
        for (i in -1000..1000 step 7) {
            yield(i.toShort())
        }
    }

    val ints: Sequence<Int> = sequence {
        yield(Int.MIN_VALUE)
        yield(0)
        yield(Int.MAX_VALUE)
        for (i in -1000..1000 step 7) {
            yield(i)
        }
    }

    val longs: Sequence<Long> = sequence {
        yield(Long.MIN_VALUE)
        yield(0L)
        yield(Long.MAX_VALUE)
        for (i in -1000..1000 step 7) {
            yield(i.toLong())
        }
    }

    val floats: Sequence<Float> = sequence {
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

    val doubles: Sequence<Double> = sequence {
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

    val strings: Sequence<String> = sequence {
        yield("")
        yield("String")
        yield("HELLO WORLD THIS IS A TEST STRING ÅÄÖ!")

        yield(buildString {
            for (i in 0..127) append(i.toChar())
        })
    }

    val byteArrays: Sequence<ByteArray> = sequence {
        yield(byteArrayOf())
        yield(byteArrayOf(Byte.MIN_VALUE))
        yield(byteArrayOf(Byte.MAX_VALUE))
        yield(ByteArray(256) { it.toByte() })
    }

    val intArrays: Sequence<IntArray> = sequence {
        yield(intArrayOf())
        yield(intArrayOf(Int.MIN_VALUE))
        yield(intArrayOf(Int.MAX_VALUE))
        yield(IntArray(256) { it })
    }

    val longArrays: Sequence<LongArray> = sequence {
        yield(longArrayOf())
        yield(longArrayOf(Long.MIN_VALUE))
        yield(longArrayOf(Long.MAX_VALUE))
        yield(LongArray(256) { it.toLong() })
    }
}
