package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.lexer.regexToken

object Tokens {
    val whitespace = regexToken("\\s+", ignore = true)
    val number = regexToken("-?\\d+")

    fun asList() = listOf(whitespace, number)
}