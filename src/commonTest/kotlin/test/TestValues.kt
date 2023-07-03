package net.benwoodworth.knbt.test

object TestValues {
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
