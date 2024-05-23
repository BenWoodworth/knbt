package net.benwoodworth.knbt.test.generators

import com.benwoodworth.parameterize.ParameterizeScope
import com.benwoodworth.parameterize.parameter
import net.benwoodworth.knbt.NbtString

fun ParameterizeScope.parameterOfNbtStringEdgeCases() = parameter {
    sequenceOf(
        NbtString(""),
        NbtString("string"),

        // SNBT Special characters
        NbtString("'"),
        NbtString("\""),
        NbtString("\\"),

        // UTF-8
        NbtString("\u0000"), // 1 byte
        NbtString("\u007F"),

        NbtString("\u0080"), // 2 bytes
        NbtString("\u07FF"),

        NbtString("\u0800"), // 3 bytes (before surrogates)
        NbtString("\uD7FF"),

        NbtString("\uE000"), // 3 bytes (after surrogates)
        NbtString("\uFFFF"),

        NbtString("\uD800\uDC00"), // 4 bytes (U+100000..U+10FFFF with UTF-16 surrogates)
        NbtString("\uDBFF\uDFFF"),

        // Java "Modified" UTF-8: https://docs.oracle.com/javase/8/docs/api/java/io/DataInput.html#modified-utf-8
        NbtString("\u0001"), // First single-byte character, since NUL (U+0000) is encoded with two bytes
    )
}
