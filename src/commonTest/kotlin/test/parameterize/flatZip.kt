package net.benwoodworth.knbt.test.parameterize

// TODO
inline fun <T, R> Iterable<T>.flatZip(map: (T) -> List<R>): List<Pair<T, R>> = buildList {
    this@flatZip.forEach { element ->
        map(element).forEach { mapped ->
            add(element to mapped)
        }
    }
}
