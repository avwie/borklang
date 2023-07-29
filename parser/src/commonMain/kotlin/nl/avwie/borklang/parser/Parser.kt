package nl.avwie.borklang.parser

import nl.avwie.borklang.ast.Expression
import nl.avwie.borklang.lexer.Lexer
import nl.avwie.borklang.tokens.Token

interface Parser {

    fun parse(tokens: List<Token>): Expression

    companion object {
        fun instance(): Parser = ParserImpl()
    }
}

fun Parser.parse(input: String): Expression = parse(Lexer.instance().tokenize(input).toList())

internal class ParserImpl() : Parser {

    private val remaining = ArrayDeque<Token>()

    override fun parse(tokens: List<Token>): Expression {
        remaining.clear()
        remaining.addAll(tokens)

        val result = expression()
        if (!isEOF()) throw IllegalStateException("Expected EOF, but found ${peek()}")
        return result
    }

    private fun expression(): Expression = when (peek()) {
        is Token.Literal -> literal()
        is Token.Identifier -> identifier()
        is Token.Bracket.Open -> list()
        else -> throw IllegalStateException("Expected expression, but found ${peek()}")
    }

    private fun literal(): Expression.Literal = when (peek()) {
        is Token.Literal -> Expression.Literal(require())
        else -> throw IllegalStateException("Expected literal, but found ${peek()}")
    }

    private fun identifier(): Expression.Identifier = when (peek()) {
        is Token.Identifier -> Expression.Identifier(require())
        else -> throw IllegalStateException("Expected identifier, but found ${peek()}")
    }

    private fun list(): Expression {
        require<Token.Bracket.Open>()
        val expressions = mutableListOf<Expression>()

        while (peek() !is Token.Bracket.Close) {
            if (isEOF()) throw IllegalStateException("Expected closing bracket, but found EOF")

            when (peek()) {
                is Token.Keyword -> expressions.add(keyword())
                is Token.Operator -> expressions.add(operator())
                is Token.Identifier -> expressions.add(call())
                else -> expressions.add(expression())
            }
        }
        require<Token.Bracket.Close>()

        return when {
            expressions.isEmpty() -> Expression.Nil
            expressions.size == 1 -> expressions.first()
            else -> Expression.Block(expressions)
        }
    }

    private fun operator(): Expression.Operator {
        val operator = require<Token.Operator>()

        val expressions = mutableListOf<Expression>()
        while (peek() !is Token.Bracket.Close) {
            expressions.add(expression())
        }

        return when (operator) {
            is Token.Operator.Unary -> when {
                expressions.isEmpty() -> throw IllegalStateException("Unary operator requires 1 argument")
                expressions.size > 1 -> throw IllegalStateException("Unary operator requires 1 argument, but found ${expressions.size}")
                expressions.first() !is Expression.Simple -> throw IllegalStateException("Unary operator requires a simple expression, but found ${expressions.first()}")
                else -> Expression.Operator.Unary(operator, expressions.first() as Expression.Simple)
            }

            is Token.Operator.Binary -> when {
                expressions.size < 2 -> throw IllegalStateException("Binary operator requires 2 arguments, but found ${expressions.size}")
                expressions.any { it !is Expression.Simple } -> throw IllegalStateException("Binary operator requires simple expressions")
                else -> Expression.Operator.Binary(operator, expressions[0] as Expression.Simple, expressions[1] as Expression.Simple)
            }
        }
    }

    private fun keyword(): Expression {
        val keyword = require<Token.Keyword>()
        return when(keyword) {
            is Token.Keyword.Const -> constant()
            is Token.Keyword.Fn -> function()
            is Token.Keyword.If -> conditional()
            is Token.Keyword.While -> loop()
            is Token.Keyword.Var -> variable()
            is Token.Keyword.Set -> assignment()
        }
    }

    private fun constant(): Expression.Declaration.Constant {
        val name = require<Token.Identifier>()
        val value = literal()
        return Expression.Declaration.Constant(name.value, value)
    }

    private fun function(): Expression.Declaration.Function {
        val name = require<Token.Identifier>()
        require<Token.Bracket.Open>()
        val parameters = mutableListOf<Token.Identifier>()
        while (peek() !is Token.Bracket.Close) {
            parameters.add(Expression.Identifier(require<Token.Identifier>()).identifier)
        }
        require<Token.Bracket.Close>()
        val body = expression()
        return Expression.Declaration.Function(name.value, parameters, body)
    }

    private fun conditional(): Expression.Control.Conditional {
        require<Token.Keyword.If>()
        val condition = expression()
        if (condition !is Expression.Simple) throw IllegalStateException("Conditional requires a simple expression")

        val then = expression()
        val otherwise = if (peek() !is Token.Bracket.Close) {
            expression()
        } else null
        return Expression.Control.Conditional(condition, then, otherwise)
    }

    private fun loop(): Expression.Control.Loop {
        val condition = expression()
        if (condition !is Expression.Simple) throw IllegalStateException("Loop requires a simple expression")
        val body = expression()
        return Expression.Control.Loop(condition, body)
    }

    private fun variable(): Expression.Declaration.Variable {
        val name = require<Token.Identifier>()
        val value = expression()
        if (value !is Expression.Simple) throw IllegalStateException("Variable requires a simple expression")
        return Expression.Declaration.Variable(name.value, value)
    }

    private fun assignment(): Expression.Assignment {
        val name = require<Token.Identifier>()
        val value = expression()
        if (value !is Expression.Simple) throw IllegalStateException("Assignment requires a simple expression")
        return Expression.Assignment(name, value)
    }

    private fun call(): Expression.Call {
        val name = require<Token.Identifier>()
        val arguments = mutableListOf<Expression>()
        while (peek() !is Token.Bracket.Close) {
            arguments.add(expression())
        }
        if (arguments.any { it !is Expression.Simple }) throw IllegalStateException("Function calls require simple expressions")
        return Expression.Call(name, arguments.filterIsInstance<Expression.Simple>())
    }

    private fun next(): Token? = remaining.removeFirstOrNull()
    private fun peek(): Token? = remaining.firstOrNull()

    private inline fun <reified T : Token> require(): T {
        if (peek() !is T) throw IllegalStateException("Expected ${T::class}, but found ${peek()}")
        return next() as T
    }

    private fun isEOF(): Boolean = peek() == Token.EOF
}