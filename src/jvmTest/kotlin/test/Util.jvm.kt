package net.benwoodworth.knbt.test

import kotlin.reflect.KClass

actual fun KClass<*>.qualifiedNameOrDefault(default: String?): String? =
    this.qualifiedName
