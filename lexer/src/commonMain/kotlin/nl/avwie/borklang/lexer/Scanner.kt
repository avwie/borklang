package nl.avwie.borklang.lexer

interface Scanner {

    val line: Int
    val column: Int
    fun isEof(): Boolean

    fun scan(): Char

    fun peek(): Char?

    companion object {
        fun instance(source: String): Scanner = ScannerImpl(source)
    }
}

internal class ScannerImpl(
    private val source: String
) : Scanner {

    private var index = 0

    override val line: Int
        get() = source.substring(0, index).count { it == '\n' } + 1

    override val column: Int
        get() = source.substring(0, index).lastIndexOf('\n').let { if (it == -1) index else index - it }

    override fun isEof(): Boolean = index >= source.length

    override fun scan(): Char = source[index++]

    override fun peek(): Char? = source.getOrNull(index)
}