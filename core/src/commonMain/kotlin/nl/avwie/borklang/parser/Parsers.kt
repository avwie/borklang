package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.parser.Parser

object Parsers {
    val number: Parser<AST.Constant.Number> = Tokens.number.use { AST.Constant.Number(text.toInt()) }
    val string: Parser<AST.Constant.String> = Tokens.string.use { AST.Constant.String(text.substring(1, text.length - 1)) }
    val constant: Parser<AST.Constant> = number or string

    val identifier: Parser<AST.Identifier> = Tokens.identifier.use { AST.Identifier(text) }

    val expression: Parser<AST.Expression> = identifier or constant
    val program: Parser<AST> = expression
}

object Grammar : Grammar<AST>() {
    override val tokens: List<Token> = Tokens.asList()
    override val rootParser: Parser<AST> = Parsers.program
}

