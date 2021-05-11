package net.benwoodworth.knbt.internal

import okio.Sink
import okio.Source

internal class NonClosingSource(private val source: Source) : Source by source {
    override fun close(): Unit = Unit
}

internal class NonClosingSink(private val sink: Sink) : Sink by sink {
    override fun close(): Unit = Unit
}
