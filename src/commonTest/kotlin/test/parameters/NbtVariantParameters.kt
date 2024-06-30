package net.benwoodworth.knbt.test.parameters

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.NbtVariant

private val javaNetworkEmptyNamedProtocolVersions = listOf(
    0..763,
    0x40000001..0x40000089
)

val NbtVariant.JavaNetwork.isEmptyNamedVersion: Boolean
    get() = javaNetworkEmptyNamedProtocolVersions.any { protocolVersion in it }


private val javaNetworkUnnamedProtocolVersions = listOf(
    764..<0x40000000,
    0x40000090..Int.MAX_VALUE
)

val NbtVariant.JavaNetwork.isUnnamedVersion: Boolean
    get() = javaNetworkUnnamedProtocolVersions.any { protocolVersion in it }


fun ParameterizeScope.parameterOfNbtVariantEdgeCases() = parameter {
    buildList {
        add(NbtVariant.Java)

        javaNetworkEmptyNamedProtocolVersions.forEach { range ->
            add(NbtVariant.JavaNetwork(range.first))
            add(NbtVariant.JavaNetwork(range.last))
        }

        javaNetworkUnnamedProtocolVersions.forEach { range ->
            add(NbtVariant.JavaNetwork(range.first))
            add(NbtVariant.JavaNetwork(range.last))
        }

        add(NbtVariant.Bedrock)
        add(NbtVariant.BedrockNetwork)
    }
}
