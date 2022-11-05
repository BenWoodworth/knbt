package net.benwoodworth.knbt.internal

internal data class NbtPath(private val path: List<Node>) {
    constructor(vararg path: Node) : this(path.toList())

    operator fun plus(node: Node): NbtPath =
        NbtPath(path + node)

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
        val type: NbtTagType
    }

    data class RootNode(
        override val type: NbtTagType,
    ) : Node

    data class NameNode(
        val name: String,
        override val type: NbtTagType,
    ) : Node

    data class IndexNode(
        val index: Int,
        override val type: NbtTagType,
    ) : Node
}
