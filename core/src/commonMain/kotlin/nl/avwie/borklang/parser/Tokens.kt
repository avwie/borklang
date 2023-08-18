package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.lexer.regexToken

internal val whitespace = regexToken("\\s+", ignore = true)
internal val number = regexToken("-?\\d+")

val Tokens = listOf(
    whitespace,
    number
)