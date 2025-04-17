package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.NbtType

internal class NbtPath(private val path: List<Node>) : List<NbtPath.Node> by path {
    override fun toString(): String = buildString {
        fun Char.isSafeNameChar(): Boolean = when (this) {
            in '0'..'9', in 'a'..'z', in 'A'..'Z', '_' -> true
            else -> false
        }

        fun String.isSafeName(): Boolean =
            isNotEmpty() && all { it.isSafeNameChar() }

        val trimmedPath = if (path.size > 1 && path[0] is RootNode) {
            path.subList(1, path.size)
        } else {
            path
        }

        trimmedPath.forEachIndexed { i, node ->
            when (node) {
                is RootNode -> {
                    append("{root}")
                }

                is NameNode -> {
                    if (i != 0) append('.')

                    if (node.name.isSafeName()) {
                        append(node.name)
                    } else {
                        append('`').append(node.name).append('`')
                    }
                }

                is IndexNode -> {
                    append('[').append(node.index).append(']')
                }
            }
        }
    }


    sealed interface Node {
        val type: NbtType
    }

    class RootNode(
        override val type: NbtType,
    ) : Node

    class NameNode(
        val name: String,
        override val type: NbtType,
    ) : Node

    class IndexNode(
        val index: Int,
        override val type: NbtType,
    ) : Node
}
