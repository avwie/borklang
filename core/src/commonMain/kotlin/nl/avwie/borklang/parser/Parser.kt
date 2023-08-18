package nl.avwie.borklang.parser

import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.lexer.Token
import com.github.h0tk3y.betterParse.parser.Parser

object Grammar : Grammar<AST>() {

    override val tokens: List<Token> = Tokens.asList()

    private val number: Parser<AST.Constant> by Tokens.number.use { AST.Constant(text.toInt()) }

    override val rootParser: Parser<AST> = number
}

