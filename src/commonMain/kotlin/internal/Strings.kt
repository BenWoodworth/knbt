package net.benwoodworth.knbt.internal

/**
 * Convert [this] Byte to unsigned two-digit all-caps hexadecimal
 */
internal fun Byte.toHex(): String =
    this.toUByte().toString(16).uppercase().padStart(2, '0')
