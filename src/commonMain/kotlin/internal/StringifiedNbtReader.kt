package net.benwoodworth.knbt.internal

import net.benwoodworth.knbt.ExperimentalNbtApi
import net.benwoodworth.knbt.OkioApi
import net.benwoodworth.knbt.internal.CharSource.ReadResult
import net.benwoodworth.knbt.internal.CharSource.ReadResult.Companion.EOF
import net.benwoodworth.knbt.internal.NbtTagType.*
import okio.Closeable

@OptIn(OkioApi::class, ExperimentalNbtApi::class)
internal class StringifiedNbtReader(val source: CharSource) : NbtReader, Closeable {
    private companion object {
        // TODO https://youtrack.jetbrains.com/issue/KT-49065
        // val DOUBLE = Regex("""[-+]?(?:[0-9]+\.?|[0-9]*\.[0-9]+)(?:e[-+]?[0-9]+)?d?""", RegexOption.IGNORE_CASE)
        val DOUBLE_A = Regex("""[-+]?[0-9]+\.?(?:e[-+]?[0-9]+)?d?""", RegexOption.IGNORE_CASE)
        val DOUBLE_B = Regex("""[-+]?[0-9]*\.[0-9]+(?:e[-+]?[0-9]+)?d?""", RegexOption.IGNORE_CASE)

        // val FLOAT = Regex("""[-+]?(?:[0-9]+\.?|[0-9]*\.[0-9]+)(?:e[-+]?[0-9]+)?f""", RegexOption.IGNORE_CASE)
        val FLOAT_A = Regex("""[-+]?[0-9]+\.?(?:e[-+]?[0-9]+)?f""", RegexOption.IGNORE_CASE)
        val FLOAT_B = Regex("""[-+]?[0-9]*\.[0-9]+(?:e[-+]?[0-9]+)?f""", RegexOption.IGNORE_CASE)

        val BYTE = Regex("""[-+]?(?:0|[1-9][0-9]*)b""", RegexOption.IGNORE_CASE)
        val LONG = Regex("""[-+]?(?:0|[1-9][0-9]*)l""", RegexOption.IGNORE_CASE)
        val SHORT = Regex("""[-+]?(?:0|[1-9][0-9]*)s""", RegexOption.IGNORE_CASE)
        val INT = Regex("""[-+]?(?:0|[1-9][0-9]*)""")

        fun ReadResult.isUnquotedStringCharacter(): Boolean =
            this != EOF && toChar().let {
                it in '0'..'9' || it in 'A'..'Z' || it in 'a'..'z' || it == '_' || it == '-' || it == '.' || it == '+'
            }

    }

    private val buffer = StringBuilder()
    private var firstEntry = true

    override fun close(): Unit = source.close()

    private fun ReadResult.isWhitespace(): Boolean =
        this != EOF && toChar().isWhitespace()

    private fun CharSource.skipWhitespace(): CharSource {
        while (peek().read().isWhitespace()) {
            read()
        }
        return this
    }

    private fun CharSource.bufferUnquotedString() {
        buffer.clear()

        while (peek().read().isUnquotedStringCharacter()) {
            buffer.append(read().toChar())
        }
    }

    private fun CharSource.bufferQuotedString() {
        buffer.clear()

        val quote = ReadResult(read().toChar())
        val backslash = ReadResult('\\')

        while (true) {
            when (val char = read()) {
                EOF -> throw NbtDecodingException("Unexpected EOF in String")
                quote -> break
                backslash -> when (val esc = read()) {
                    EOF -> throw NbtDecodingException("Unexpected EOF in String")
                    quote, backslash -> buffer.append(esc)
                    else -> throw NbtDecodingException("Invalid escape: \\$esc")
                }
                else -> buffer.append(char.toChar())
            }
        }
    }

    private fun CharSource.expect(char: Char, ignoreCase: Boolean = false) {
        val actual = read()
        if (actual == EOF) {
            throw NbtDecodingException("Expected '$char', but was EOF")
        } else if (!actual.toChar().equals(char, ignoreCase)) {
            throw NbtDecodingException("Expected '$char', but was '$actual'")
        }
    }

    private fun CharSource.peekTagType(): NbtTagType? {
        val peek = peek()
        return when (peek.read()) {
            EOF -> null
            ReadResult('[') -> when (peek.skipWhitespace().read()) {
                ReadResult('B') -> if (peek.skipWhitespace().read() == ReadResult(';')) TAG_Byte_Array else TAG_List
                ReadResult('I') -> if (peek.skipWhitespace().read() == ReadResult(';')) TAG_Int_Array else TAG_List
                ReadResult('L') -> if (peek.skipWhitespace().read() == ReadResult(';')) TAG_Long_Array else TAG_List
                else -> TAG_List
            }
            ReadResult('{') -> TAG_Compound
            ReadResult('\''), ReadResult('"') -> TAG_String
            else -> {
                peek().bufferUnquotedString()
                when {
                    buffer.isEmpty() -> null
                    FLOAT_A.matches(buffer) -> TAG_Float
                    FLOAT_B.matches(buffer) -> TAG_Float
                    BYTE.matches(buffer) -> TAG_Byte
                    LONG.matches(buffer) -> TAG_Long
                    SHORT.matches(buffer) -> TAG_Short
                    INT.matches(buffer) -> TAG_Int
                    DOUBLE_A.matches(buffer) -> TAG_Double
                    DOUBLE_B.matches(buffer) -> TAG_Double
                    "true".contentEquals(buffer, true) -> TAG_Byte
                    "false".contentEquals(buffer, true) -> TAG_Byte
                    else -> TAG_String
                }
            }
        }
    }

    private fun CharSource.readSnbtString(): String? =
        when (skipWhitespace().peek().read()) {
            EOF -> throw NbtDecodingException("Expected String, but was EOF")
            ReadResult('"'), ReadResult('\'') -> {
                bufferQuotedString()
                buffer.toString()
            }
            else -> {
                bufferUnquotedString()
                buffer.takeUnless { it.isEmpty() }?.toString()
            }
        }

    override fun beginRootTag(): NbtReader.RootTagInfo =
        NbtReader.RootTagInfo(
            source.skipWhitespace().peekTagType()
                ?: throw NbtDecodingException("Expected value, but got nothing")
        )

    override fun beginCompound() {
        source.skipWhitespace().expect('{')
        firstEntry = true
    }

    override fun beginCompoundEntry(): NbtReader.CompoundEntryInfo {
        source.skipWhitespace()

        return if (source.peek().read() == ReadResult('}')) {
            NbtReader.CompoundEntryInfo.End
        } else {
            if (firstEntry) {
                firstEntry = false
            } else {
                val char = source.read()
                if (char != ReadResult(',')) throw NbtDecodingException("Expected ',' or '}', but got '$char'")
            }

            val name = source.skipWhitespace().readSnbtString()
                ?: throw NbtDecodingException("Expected key but got nothing")

            source.skipWhitespace().expect(':')

            val type = source.skipWhitespace().peekTagType()
                ?: throw NbtDecodingException("Expected value but got nothing")

            return NbtReader.CompoundEntryInfo(type, name)
        }
    }

    override fun endCompound(): Unit =
        source.expect('}')

    private fun beginArray(type: Char): NbtReader.ArrayInfo {
        source.skipWhitespace().expect('[')
        source.skipWhitespace().expect(type, true)
        source.skipWhitespace().expect(';')

        firstEntry = true

        val empty = source.skipWhitespace().peek().read() == ReadResult(']')
        val size = if (empty) 0 else NbtReader.UNKNOWN_SIZE

        return NbtReader.ArrayInfo(size)
    }

    private fun beginCollectionEntry(): Boolean {
        source.skipWhitespace()

        return if (source.peek().read() == ReadResult(']')) {
            false
        } else {
            if (firstEntry) {
                firstEntry = false
            } else {
                val char = source.read()
                if (char != ReadResult(',')) throw NbtDecodingException("Expected ',' or ']', but got '$char'")
            }
            true
        }
    }

    private fun endCollection(): Unit =
        source.skipWhitespace().expect(']')

    override fun beginList(): NbtReader.ListInfo {
        source.skipWhitespace().expect('[')
        source.skipWhitespace()

        firstEntry = true

        val type = source.peekTagType() ?: TAG_End
        val size = if (type == TAG_End) 0 else NbtReader.UNKNOWN_SIZE

        return NbtReader.ListInfo(type, size)
    }

    override fun beginListEntry(): Boolean =
        beginCollectionEntry()

    override fun endList(): Unit =
        endCollection()

    override fun beginByteArray(): NbtReader.ArrayInfo =
        beginArray('B')

    override fun beginByteArrayEntry(): Boolean =
        beginCollectionEntry()

    override fun endByteArray(): Unit =
        endCollection()

    override fun beginIntArray(): NbtReader.ArrayInfo =
        beginArray('I')

    override fun beginIntArrayEntry(): Boolean =
        beginCollectionEntry()

    override fun endIntArray(): Unit =
        endCollection()

    override fun beginLongArray(): NbtReader.ArrayInfo =
        beginArray('L')

    override fun beginLongArrayEntry(): Boolean =
        beginCollectionEntry()

    override fun endLongArray(): Unit =
        endCollection()

    override fun readByte(): Byte {
        source.skipWhitespace().bufferUnquotedString()

        return when {
            buffer.contentEquals("true", true) -> 1
            buffer.contentEquals("false", true) -> 0
            else -> {
                if (!BYTE.matches(buffer)) throw NbtDecodingException("Expected Byte, but was '$buffer'")
                buffer.setLength(buffer.length - 1)
                buffer.toString().toByte()
            }
        }
    }

    override fun readShort(): Short {
        source.skipWhitespace().bufferUnquotedString()

        if (!SHORT.matches(buffer)) throw NbtDecodingException("Expected Short, but was '$buffer'")
        buffer.setLength(buffer.length - 1)
        return buffer.toString().toShort()
    }

    override fun readInt(): Int {
        source.skipWhitespace().bufferUnquotedString()

        if (!INT.matches(buffer)) throw NbtDecodingException("Expected Int, but was '$buffer'")
        return buffer.toString().toInt()
    }

    override fun readLong(): Long {
        source.skipWhitespace().bufferUnquotedString()

        if (!LONG.matches(buffer)) throw NbtDecodingException("Expected Long, but was '$buffer'")
        buffer.setLength(buffer.length - 1)
        return buffer.toString().toLong()
    }

    override fun readFloat(): Float {
        source.skipWhitespace().bufferUnquotedString()

        if (!FLOAT_A.matches(buffer) && !FLOAT_B.matches(buffer)) {
            throw NbtDecodingException("Expected Float, but was '$buffer'")
        }
        buffer.setLength(buffer.length - 1)
        return buffer.toString().toFloat()
    }

    override fun readDouble(): Double {
        source.skipWhitespace().bufferUnquotedString()

        if (!DOUBLE_A.matches(buffer) && !DOUBLE_B.matches(buffer)) {
            throw NbtDecodingException("Expected Double, but was '$buffer'")
        }
        if (buffer.last().equals('d', true)) buffer.setLength(buffer.length - 1)
        return buffer.toString().toDouble()
    }

    override fun readString(): String =
        source.readSnbtString() ?: throw NbtDecodingException("Expected String but got nothing")
}
