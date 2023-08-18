package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.lexer.regexToken

object Tokens {

    val whitespace = regexToken("\\s+", ignore = true)
    val number = regexToken("-?\\d+")
    val string = regexToken("\"[^\"]*\"")
    val identifier = regexToken("[a-zA-Z_][a-zA-Z0-9_]*")

    fun asList() = listOf(
        whitespace,
        number,
        string,
        identifier
    )
}