package net.benwoodworth.knbt.test.parameterize.arguments

class CompleteList<T>(
    override val size: Int,
    private val get: (index: Int) -> T
): List<T> {
    override fun contains(element: T): Boolean = true
    override fun containsAll(elements: Collection<T>): Boolean = true

    override fun get(index: Int): T {
        if (index !in indices) throw IndexOutOfBoundsException()
        return get.invoke(index)
    }

    override fun indexOf(element: T): Int = indexOfFirst { it == element }

    override fun isEmpty(): Boolean = size == 0

    override fun iterator(): Iterator<T> = CompleteListIterator()

    override fun lastIndexOf(element: T): Int = indexOfLast { it == element }

    override fun listIterator(): ListIterator<T> = CompleteListIterator()

    override fun listIterator(index: Int): ListIterator<T> = CompleteListIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> =
        CompleteList(toIndex - fromIndex + 1) { index ->
            get.invoke(index + fromIndex)
        }


    private inner class CompleteListIterator(
        private var index: Int = 0
    ) : ListIterator<T> {
        override fun hasNext(): Boolean = index < size

        override fun nextIndex(): Int = index

        override fun next(): T = get(index++)


        override fun hasPrevious(): Boolean = index > 0

        override fun previous(): T = get (--index)

        override fun previousIndex(): Int = index - 1
    }
}
