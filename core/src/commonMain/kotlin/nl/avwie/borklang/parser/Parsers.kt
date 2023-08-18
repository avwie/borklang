package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.parser.Parser

object Parsers {
    val number: Parser<AST.Constant.Number> = Tokens.number.use { AST.Constant.Number(text.toInt()) }
    val string: Parser<AST.Constant.String> = Tokens.string.use { AST.Constant.String(text.substring(1, text.length - 1)) }
    val constant: Parser<AST.Constant> = number or string

    val identifier: Parser<AST.Identifier> = Tokens.identifier.use { AST.Identifier(text) }
    val nil: Parser<AST.Nil> = Tokens.nil use { AST.Nil }
    val expression: Parser<AST.Expression> = identifier or constant or nil

    val assignment: Parser<AST.Assignment> = (identifier and Tokens.equal and expression).map { (identifier, _, expression) -> AST.Assignment(identifier, expression) }

    val statement: Parser<AST.Statement> = assignment or expression

    val program: Parser<AST> = oneOrMore(statement).map { statements ->
        if (statements.size == 1) statements[0] else AST.Program(statements)
    }
}

object Grammar : Grammar<AST>() {
    override val tokens: List<Token> = Tokens.asList()
    override val rootParser: Parser<AST> = Parsers.program
}

