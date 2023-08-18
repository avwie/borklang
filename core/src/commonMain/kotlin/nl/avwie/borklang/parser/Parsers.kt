package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.parser.Parser

object Parsers {
    val number: Parser<AST.Constant.Number> = Tokens.number.use { AST.Constant.Number(text.toInt()) }
    val string: Parser<AST.Constant.String> = Tokens.string.use { AST.Constant.String(text.substring(1, text.length - 1)) }
    val constant: Parser<AST.Constant> = number or string
    val nil: Parser<AST.Nil> = Tokens.nil use { AST.Nil }

    val identifier: Parser<AST.Identifier> = Tokens.identifier.use { AST.Identifier(text) }
    val expression: Parser<AST.Expression> =  identifier or constant or nil

    val assignment: Parser<AST.Assignment> = (identifier and skip(Tokens.equal) and expression).map { (identifier, expression) -> AST.Assignment(identifier, expression) }

    val declaration: Parser<AST.Declaration> = ((Tokens.const or Tokens.let) and assignment).map { (token, assignment) ->
        when (token.type) {
            Tokens.const -> AST.Declaration.Constant(assignment.identifier, assignment.expression)
            Tokens.let -> AST.Declaration.Variable(assignment.identifier, assignment.expression)
            else -> throw IllegalStateException("Unknown declaration type: ${token.type}")
        }
    }

    val statement: Parser<AST.Statement> = (declaration or assignment or expression) and skip(zeroOrMore(Tokens.newline or Tokens.semicolon))

    val program: Parser<AST> = oneOrMore(statement).map { statements ->
        if (statements.size == 1) statements[0] else AST.Program(statements)
    }
}

object Grammar : Grammar<AST>() {
    override val tokens: List<Token> = Tokens.asList()
    override val rootParser: Parser<AST> = Parsers.program
}

