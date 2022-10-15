package net.benwoodworth.knbt.util

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.core.spec.style.scopes.RootTestWithConfigBuilder
import io.kotest.core.test.TestScope
import kotlin.jvm.JvmName

/*
 * Kotest 5 doesn't support nested tests in Kotlin/JS, so until it is supported
 * this spec can be used, flattening all the nested tests to the root level.
 *
 * Posted by @BenWoodworth in Kotest issue #3141:
 * https://github.com/kotest/kotest/issues/3141#issuecomment-1278433891
 */

private fun String.toContainerPrefix(): String = "$this -- "

open class FlatSpec private constructor() : FunSpec() {
    // Using primary constructor causes issue with Kotlin/JS IR:
    // https://youtrack.jetbrains.com/issue/KT-54450
    constructor(body: FlatSpec.() -> Unit = {}) : this() {
        body()
    }

    // Overload context functions with non-nested versions
    @JvmName("context\$FlatSpec")
    fun context(name: String, test: FlatSpecContainerScope.() -> Unit): Unit =
        test(FlatSpecContainerScope(this, name.toContainerPrefix(), false))

    @JvmName("xcontext\$FlatSpec")
    fun xcontext(name: String, test: FlatSpecContainerScope.() -> Unit): Unit =
        test(FlatSpecContainerScope(this, name.toContainerPrefix(), true))


    // Suppress FunSpec's context functions so they can't be used
    @Deprecated("Unsupported", level = DeprecationLevel.HIDDEN)
    override fun context(name: String, test: suspend FunSpecContainerScope.() -> Unit): Nothing = error("Unsupported")

    @Deprecated("Unsupported", level = DeprecationLevel.HIDDEN)
    override fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit): Nothing = error("Unsupported")

    @ExperimentalKotest
    @Deprecated("Unsupported", level = DeprecationLevel.HIDDEN)
    override fun context(name: String): Nothing = error("Unsupported")

    @ExperimentalKotest
    @Deprecated("Unsupported", level = DeprecationLevel.HIDDEN)
    override fun xcontext(name: String): Nothing = error("Unsupported")
}

@KotestTestScope
class FlatSpecContainerScope(
    private val flatSpec: FlatSpec,
    private val prefix: String,
    private val ignored: Boolean,
) {
    fun test(name: String): RootTestWithConfigBuilder =
        if (ignored) {
            flatSpec.xtest(prefix + name)
        } else {
            flatSpec.test(prefix + name)
        }

    fun test(name: String, test: suspend TestScope.() -> Unit): Unit =
        if (ignored) {
            flatSpec.xtest(prefix + name, test)
        } else {
            flatSpec.test(prefix + name, test)
        }

    fun xtest(name: String): RootTestWithConfigBuilder =
        flatSpec.xtest(prefix + name)

    fun xtest(name: String, test: suspend TestScope.() -> Unit): Unit =
        flatSpec.xtest(prefix + name, test)

    fun context(name: String, test: FlatSpecContainerScope.() -> Unit): Unit =
        if (ignored) {
            flatSpec.xcontext(prefix + name, test)
        } else {
            flatSpec.context(prefix + name, test)
        }

    fun xcontext(name: String, test: FlatSpecContainerScope.() -> Unit): Unit =
        flatSpec.xcontext(prefix + name, test)
}
