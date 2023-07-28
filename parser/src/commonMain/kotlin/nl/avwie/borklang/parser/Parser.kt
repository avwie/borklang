package nl.avwie.borklang.parser

import nl.avwie.borklang.lexer.Lexer
import nl.avwie.borklang.lexer.Token

interface Parser {
    fun parse(): SExpression

    companion object {
        fun instance(tokens: List<Token>): Parser = ParserImpl(tokens)
    }
}

fun Parser.Companion.instance(source: String): Parser = instance(Lexer.instance(source).getTokens().toList())

internal class ParserImpl(
    tokens: List<Token>
) : Parser {

    private val remaining = ArrayDeque(tokens)

    override fun parse(): SExpression {
        val result = parseSExpression()

        if (!isEOF()) throw IllegalStateException("Expected EOF, but found ${peek()}")
        return result
    }

    private fun parseSExpression(): SExpression = when (val token = peek()) {
        is Token.Literal -> parseLiteral()
        is Token.Bracket.Open -> parseList()
        is Token.Operator -> parseOperator()
        else -> throw IllegalStateException("Unexpected token $token")
    }

    private fun parseLiteral(): SExpression = when (val token = require<Token.Literal>()) {
        is Token.Literal.Integer -> SExpression.Number.Integer(token.value)
        is Token.Literal.Float -> SExpression.Number.Float(token.value)
        is Token.Literal.String -> SExpression.String(token.value)
        is Token.Literal.Boolean -> SExpression.Boolean(token.value)
        is Token.Literal.Identifier -> SExpression.Identifier(token.value)
        else -> throw IllegalStateException("Unexpected token $token")
    }

    private fun parseList(): SExpression.List {
        require<Token.Bracket.Open>()

        val expressions = mutableListOf<SExpression>()
        while (peek() !is Token.Bracket.Close) {
            expressions.add(parseSExpression())
        }

        when {
            expressions.isEmpty() -> throw IllegalStateException("Empty list")
            expressions[0] is SExpression.Operator -> (expressions[0] as SExpression.Operator).also { operator ->
                when (operator.operator) {
                    Token.Operator.Not -> {
                        if (expressions.size != 2) throw IllegalStateException("Not operator expects 1 argument")
                    }
                    else -> {
                        if (expressions.size != 3) throw IllegalStateException("Operator ${operator.operator} expects 2 arguments")
                    }
                }
            }
        }

        require<Token.Bracket.Close>()
        return SExpression.List(expressions)
    }

    private fun parseOperator(): SExpression.Operator = SExpression.Operator(require())

    private fun next(): Token? = remaining.removeFirstOrNull()
    private fun peek(): Token? = remaining.firstOrNull()

    private inline fun <reified T : Token> require(): T {
        if (peek() !is T) throw IllegalStateException("Expected ${T::class}, but found ${peek()}")
        return next() as T
    }

    private fun isEOF(): Boolean = peek() == Token.EOF
}