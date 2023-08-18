package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken

object Tokens {

    val whitespace = regexToken("\\s+", ignore = true)
    val newline = regexToken("[\r\n]+", ignore = true)

    val number = regexToken("-?\\d+")
    val string = regexToken("\"[^\"]*\"")
    val nil = literalToken("Nil")
    val identifier = regexToken("[a-zA-Z_][a-zA-Z0-9_]*")

    val equal = literalToken("=")

    fun asList() = listOf(
        whitespace,
        newline,

        number,
        string,
        nil,
        identifier,
        equal
    )
}