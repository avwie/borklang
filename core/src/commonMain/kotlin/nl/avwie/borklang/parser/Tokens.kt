package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken

object Tokens {

    val whitespace = regexToken("\\s+", ignore = true)
    val newline = regexToken("[\r\n]+", ignore = true)
    val semicolon = literalToken(";")
    val leftBrace = literalToken("{")
    val rightBrace = literalToken("}")

    val const = literalToken("const")
    val let = literalToken("let")
    val nil = literalToken("Nil")

    val number = regexToken("-?\\d+")
    val string = regexToken("\"[^\"]*\"")
    val identifier = regexToken("[a-zA-Z_][a-zA-Z0-9_]*")

    val plus = literalToken("+")
    val minus = literalToken("-")
    val multiply = literalToken("*")
    val divide = literalToken("/")
    val modulo = literalToken("%")

    val doubleEqual = literalToken("==")
    val equal = literalToken("=")
    val lessThan = literalToken("<")
    val lessThanOrEqual = literalToken("<=")
    val greaterThan = literalToken(">")
    val greaterThanOrEqual = literalToken(">=")

    fun asList() = listOf(
        whitespace,
        newline,
        semicolon,
        leftBrace,
        rightBrace,

        // keywords
        const,
        let,
        nil,

        // literals
        number,
        string,
        identifier,

        // operators
        plus,
        minus,
        multiply,
        divide,
        modulo,

        doubleEqual,
        equal,
        lessThan,
        lessThanOrEqual,
        greaterThan,
        greaterThanOrEqual,
    )
}