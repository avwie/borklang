package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.parser.Parser

object Parsers {
    val number: Parser<AST.Constant.Number> = Tokens.number.use { AST.Constant.Number(text.toInt()) }
    val string: Parser<AST.Constant.String> = Tokens.string.use { AST.Constant.String(text.substring(1, text.length - 1)) }
    val constant: Parser<AST.Constant> = number or string
    val nil: Parser<AST.Nil> = Tokens.nil use { AST.Nil }

    val identifier: Parser<AST.Identifier> = Tokens.identifier.use { AST.Identifier(text) }

    val argumentsList: Parser<List<AST.Expression>> = (
            parser { expression } and zeroOrMore(Tokens.comma and parser { expression })
        )
        .map { (first, rest) ->
            listOf(first) + rest.map { (_, expression) -> expression }
        }

    val functionCall: Parser<AST.FunctionCall> = (
            identifier and skip(Tokens.leftParenthesis) and optional(argumentsList) and skip(Tokens.rightParenthesis)
        )
        .map { (identifier, arguments) ->
            AST.FunctionCall(identifier, arguments ?: emptyList())
        }

    val expression: Parser<AST.Expression> =  functionCall or identifier or constant or nil

    val assignment: Parser<AST.Assignment> = (
            identifier and skip(Tokens.equal) and expression
        ).map { (identifier, expression) ->
            AST.Assignment(identifier, expression)
        }

    val valueDeclaration: Parser<AST.Declaration> = (
            (Tokens.const or Tokens.let) and assignment
        )
        .map { (token, assignment) ->
            when (token.type) {
                Tokens.const -> AST.Declaration.Constant(assignment.identifier, assignment.expression)
                Tokens.let -> AST.Declaration.Variable(assignment.identifier, assignment.expression)
                else -> throw IllegalStateException("Unknown declaration type: ${token.type}")
            }
        }

    val parameterList: Parser<List<AST.Identifier>> = (
            identifier and zeroOrMore(Tokens.comma and identifier)
        )
        .map { (first, rest) ->
            listOf(first) + rest.map { (_, identifier) -> identifier }
        }

    val functionDeclaration: Parser<AST.Declaration.Function> = (
            skip(Tokens.fn) and
            identifier and
            skip(Tokens.leftParenthesis) and
            optional(parameterList) and
            skip(Tokens.rightParenthesis) and
            parser { block }
        )
        .map { (identifier, parameters, block) ->
            AST.Declaration.Function(identifier, parameters ?: emptyList(), block)
        }

    val declaration = functionDeclaration or valueDeclaration

    val block: Parser<AST.Block> = (
            skip(Tokens.leftBrace) and
            oneOrMore(parser { statement }) and
            skip(Tokens.rightBrace)
        )
        .map { statements ->  AST.Block(statements) }

    val statement: Parser<AST.Statement> = (
            block or
            declaration or
            assignment or
            expression
        ) and skip(zeroOrMore(Tokens.newline or Tokens.semicolon))

    val program: Parser<AST> = oneOrMore(statement).map { statements ->
        if (statements.size == 1) statements[0] else AST.Program(statements)
    }
}

object Grammar : Grammar<AST>() {
    override val tokens: List<Token> = Tokens
    override val rootParser: Parser<AST> = Parsers.program
}

