package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.parser.Parser

object Parsers {
    val constant: Parser<AST.Constant> = Tokens.number.use { AST.Constant(text.toInt()) }
    val identifier: Parser<AST.Identifier> = Tokens.identifier.use { AST.Identifier(text) }

    val program: Parser<AST> = constant or identifier
}

object Grammar : Grammar<AST>() {
    override val tokens: List<Token> = Tokens.asList()
    override val rootParser: Parser<AST> = Parsers.program
}

