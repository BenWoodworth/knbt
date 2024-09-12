package net.benwoodworth.knbt

import com.benwoodworth.parameterize.parameterOf
import net.benwoodworth.knbt.test.parameterizeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BedrockNetworkNbtTest {
    @Test
    fun negative_protocol_version_should_throw() = parameterizeTest {
        val protocolVersion by parameterOf(-1, Int.MIN_VALUE)

        val failure = assertFailsWith<IllegalArgumentException> {
            BedrockNetworkNbt { this.protocolVersion = protocolVersion }
        }

        assertEquals("Protocol version must be non-negative, but is $protocolVersion", failure.message)
    }
}
